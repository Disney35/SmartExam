
package com.smartexam.service;

import com.smartexam.entity.*;
import com.smartexam.repository.*;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalTime;
import java.util.*;
import java.util.stream.Collectors;

@Service
public class SchedulingService {

    private final ExamRepository examRepository;
    private final RoomRepository roomRepository;
    private final InvigilatorRepository invigilatorRepository;
    private final EnrollmentRepository enrollmentRepository;
    private final AccommodationRepository accommodationRepository;
    private final RoomAvailabilityRepository roomAvailabilityRepository;
    private final InvigilatorAvailabilityRepository invigilatorAvailabilityRepository;
    private final ScheduleAssignmentRepository assignmentRepository;
    private final ExamRequirementRepository examRequirementRepository;
    private final RoomEquipmentRepository roomEquipmentRepository;

    private static final List<LocalTime> START_SLOTS = List.of(
            LocalTime.of(9, 0),
            LocalTime.of(13, 0),
            LocalTime.of(17, 0)
    );

    public SchedulingService(
            ExamRepository examRepository,
            RoomRepository roomRepository,
            InvigilatorRepository invigilatorRepository,
            EnrollmentRepository enrollmentRepository,
            AccommodationRepository accommodationRepository,
            RoomAvailabilityRepository roomAvailabilityRepository,
            InvigilatorAvailabilityRepository invigilatorAvailabilityRepository,
            ScheduleAssignmentRepository assignmentRepository,
            ExamRequirementRepository examRequirementRepository,
            RoomEquipmentRepository roomEquipmentRepository) {

        this.examRepository = examRepository;
        this.roomRepository = roomRepository;
        this.invigilatorRepository = invigilatorRepository;
        this.enrollmentRepository = enrollmentRepository;
        this.accommodationRepository = accommodationRepository;
        this.roomAvailabilityRepository = roomAvailabilityRepository;
        this.invigilatorAvailabilityRepository =
                invigilatorAvailabilityRepository;
        this.assignmentRepository = assignmentRepository;
        this.examRequirementRepository = examRequirementRepository;
        this.roomEquipmentRepository = roomEquipmentRepository;
    }

    @Transactional
    public ScheduleAssignment scheduleExam(String examId) {

        Exam exam = examRepository.findById(examId)
                .orElseThrow(() ->
                        new IllegalArgumentException(
                                "Exam not found: " + examId));

        // Preserve successful assignments.
        // Allow previously unscheduled exams to be retried.
        Optional<ScheduleAssignment> previous =
                assignmentRepository.findByExamExamId(examId);

        if (previous.isPresent()) {

            ScheduleAssignment oldAssignment = previous.get();

            if ("SCHEDULED".equalsIgnoreCase(
                    oldAssignment.getStatus())) {
                return oldAssignment;
            }

            assignmentRepository.delete(oldAssignment);
            assignmentRepository.flush();
        }

        if (exam.getExamDate() == null ||
                exam.getDurationMinutes() == null ||
                exam.getDurationMinutes() <= 0) {

            throw new IllegalArgumentException(
                    "Exam date and positive duration are required");
        }

        if (exam.getCourse() == null ||
                exam.getCourse().getCourseId() == null) {

            throw new IllegalArgumentException(
                    "Exam must have a valid course");
        }

        String courseId = exam.getCourse().getCourseId();

        List<Enrollment> allEnrollments =
                enrollmentRepository.findAll();

        // Find active enrollments for this exam's course.
        List<Enrollment> examEnrollments = allEnrollments.stream()
                .filter(e -> e.getCourse() != null)
                .filter(e -> e.getStudent() != null)
                .filter(e -> courseId.equals(
                        e.getCourse().getCourseId()))
                .filter(e -> "ACTIVE".equalsIgnoreCase(e.getStatus()))
                .collect(Collectors.toList());

        Set<String> studentIds = examEnrollments.stream()
                .map(e -> e.getStudent().getStudentId())
                .filter(Objects::nonNull)
                .collect(Collectors.toSet());

        int studentCount = studentIds.size();

        if (studentCount == 0) {
            return saveUnscheduled(
                    exam,
                    0,
                    exam.getDurationMinutes(),
                    "NO_ACTIVE_ENROLLMENTS: No active students are enrolled"
            );
        }

        // Read exam requirements once.
        List<ExamRequirement> requirements =
                examRequirementRepository.findByExamExamId(examId);

        // Read all active accommodations.
        List<Accommodation> accommodations =
                accommodationRepository.findAll();

        int maximumExtraTime = 0;
        boolean accessibilityRequired = false;

        for (Accommodation accommodation : accommodations) {

            if (!accommodation.isActive() ||
                    accommodation.getStudent() == null) {
                continue;
            }

            String studentId =
                    accommodation.getStudent().getStudentId();

            if (!studentIds.contains(studentId)) {
                continue;
            }

            if (accommodation.getExtraTimeMinutes() != null) {
                maximumExtraTime = Math.max(
                        maximumExtraTime,
                        accommodation.getExtraTimeMinutes()
                );
            }

            String type = accommodation.getAccommodationType();

            if (type != null) {
                String normalizedType =
                        type.trim().toUpperCase(Locale.ROOT);

                if (normalizedType.contains("ACCESS") ||
                        normalizedType.contains("WHEEL") ||
                        normalizedType.contains("MOBILITY")) {

                    accessibilityRequired = true;
                }
            }
        }

        // Apply the maximum additional time needed by any student.
        int effectiveDuration =
                exam.getDurationMinutes() + maximumExtraTime;

        List<Room> rooms = roomRepository.findAll();

        List<Invigilator> invigilators =
                invigilatorRepository.findAll();

        List<ScheduleAssignment> existingAssignments =
                assignmentRepository.findAll();

        List<RoomAvailability> roomWindows =
                roomAvailabilityRepository.findByAvailabilityDate(
                        exam.getExamDate()
                );

        List<InvigilatorAvailability> invigilatorWindows =
                invigilatorAvailabilityRepository.findByAvailabilityDate(
                        exam.getExamDate()
                );

        List<String> rejectionReasons = new ArrayList<>();

        // Try each configured examination time.
        for (LocalTime start : START_SLOTS) {

            LocalTime end = start.plusMinutes(effectiveDuration);

            // Reject slots extending beyond midnight.
            if (!end.isAfter(start)) {
                rejectionReasons.add(
                        "INVALID_TIME: Examination exceeds the daily time boundary"
                );
                continue;
            }

            for (Room room : rooms) {

                // 1. Check mandatory examination equipment.
                if (!roomMeetsRequirements(
                        requirements,
                        room.getRoomId())) {

                    rejectionReasons.add(
                            "EQUIPMENT_MISSING: Room " +
                            room.getRoomId() +
                            " does not have all mandatory exam equipment"
                    );
                    continue;
                }

                // 2. Check room capacity.
                if (room.getCapacity() == null ||
                        room.getCapacity() < studentCount) {

                    rejectionReasons.add(
                            "ROOM_CAPACITY: " + room.getRoomId() +
                            " cannot accommodate " + studentCount +
                            " students"
                    );
                    continue;
                }

                // 3. Check accessibility accommodation.
                if (accessibilityRequired &&
                        !Boolean.TRUE.equals(
                                room.getAccessibilityAvailable())) {

                    rejectionReasons.add(
                            "ACCESSIBILITY: " + room.getRoomId() +
                            " does not meet accessibility requirements"
                    );
                    continue;
                }

                // 4. Check room availability.
                boolean roomWindowAvailable =
                        roomWindows.stream().anyMatch(w ->
                                w.getRoom() != null &&
                                room.getRoomId().equals(
                                        w.getRoom().getRoomId()) &&
                                w.isAvailable() &&
                                w.getStartTime() != null &&
                                w.getEndTime() != null &&
                                !start.isBefore(w.getStartTime()) &&
                                !end.isAfter(w.getEndTime())
                        );

                if (!roomWindowAvailable) {

                    rejectionReasons.add(
                            "ROOM_UNAVAILABLE: " + room.getRoomId() +
                            " is not available for " + start + "-" + end
                    );
                    continue;
                }

                // 5. Prevent room double-booking.
                boolean roomAlreadyBooked =
                        existingAssignments.stream()
                                .filter(a -> "SCHEDULED".equalsIgnoreCase(
                                        a.getStatus()))
                                .filter(a -> a.getRoom() != null)
                                .filter(a -> room.getRoomId().equals(
                                        a.getRoom().getRoomId()))
                                .filter(a -> exam.getExamDate().equals(
                                        a.getExamDate()))
                                .anyMatch(a -> overlaps(
                                        start,
                                        end,
                                        a.getStartTime(),
                                        a.getEndTime()
                                ));

                if (roomAlreadyBooked) {

                    rejectionReasons.add(
                            "ROOM_CONFLICT: " + room.getRoomId() +
                            " is already booked"
                    );
                    continue;
                }

                // Check available invigilators.
                for (Invigilator invigilator : invigilators) {

                    // 6. Check general invigilator availability.
                    if (!invigilator.isAvailable()) {
                        continue;
                    }

                    // 7. Check unavailable dates.
                    if (invigilator.getUnavailableDates() != null &&
                            invigilator.getUnavailableDates().contains(
                                    exam.getExamDate())) {
                        continue;
                    }

                    // 8. Check invigilator availability window.
                    boolean invigilatorWindowAvailable =
                            invigilatorWindows.stream().anyMatch(w ->
                                    w.getInvigilator() != null &&
                                    invigilator.getInvigilatorId().equals(
                                            w.getInvigilator()
                                                    .getInvigilatorId()) &&
                                    w.isAvailable() &&
                                    w.getStartTime() != null &&
                                    w.getEndTime() != null &&
                                    !start.isBefore(w.getStartTime()) &&
                                    !end.isAfter(w.getEndTime())
                            );

                    if (!invigilatorWindowAvailable) {
                        continue;
                    }

                    // 9. Prevent invigilator double-booking.
                    boolean invigilatorAlreadyBooked =
                            existingAssignments.stream()
                                    .filter(a -> "SCHEDULED".equalsIgnoreCase(
                                            a.getStatus()))
                                    .filter(a -> a.getInvigilator() != null)
                                    .filter(a -> invigilator.getInvigilatorId()
                                            .equals(a.getInvigilator()
                                                    .getInvigilatorId()))
                                    .filter(a -> exam.getExamDate().equals(
                                            a.getExamDate()))
                                    .anyMatch(a -> overlaps(
                                            start,
                                            end,
                                            a.getStartTime(),
                                            a.getEndTime()
                                    ));

                    if (invigilatorAlreadyBooked) {
                        continue;
                    }

                    // 10. Prevent overlapping exams for students.
                    boolean studentConflict = false;

                    for (ScheduleAssignment existing :
                            existingAssignments) {

                        if (!"SCHEDULED".equalsIgnoreCase(
                                    existing.getStatus()) ||
                                !exam.getExamDate().equals(
                                        existing.getExamDate()) ||
                                !overlaps(
                                        start,
                                        end,
                                        existing.getStartTime(),
                                        existing.getEndTime()) ||
                                existing.getExam() == null ||
                                existing.getExam().getCourse() == null) {
                            continue;
                        }

                        String existingCourseId =
                                existing.getExam().getCourse().getCourseId();

                        Set<String> existingStudentIds =
                                allEnrollments.stream()
                                        .filter(e -> e.getCourse() != null)
                                        .filter(e -> e.getStudent() != null)
                                        .filter(e -> existingCourseId.equals(
                                                e.getCourse().getCourseId()))
                                        .filter(e -> "ACTIVE".equalsIgnoreCase(
                                                e.getStatus()))
                                        .map(e -> e.getStudent().getStudentId())
                                        .filter(Objects::nonNull)
                                        .collect(Collectors.toSet());

                        if (!Collections.disjoint(
                                studentIds,
                                existingStudentIds)) {

                            studentConflict = true;
                            break;
                        }
                    }

                    if (studentConflict) {

                        rejectionReasons.add(
                                "STUDENT_CONFLICT: One or more students " +
                                "already have an overlapping exam"
                        );
                        continue;
                    }

                    // All constraints passed. Create assignment.
                    ScheduleAssignment assignment =
                            new ScheduleAssignment();

                    assignment.setAssignmentId(
                            "ASN-" + examId + "-" + room.getRoomId()
                    );

                    assignment.setExam(exam);
                    assignment.setRoom(room);
                    assignment.setInvigilator(invigilator);
                    assignment.setExamDate(exam.getExamDate());
                    assignment.setStartTime(start);
                    assignment.setEndTime(end);
                    assignment.setStudentCount(studentCount);

                    assignment.setEffectiveDurationMinutes(
                            effectiveDuration
                    );

                    assignment.setStatus("SCHEDULED");

                    assignment.setDecisionReason(
                            "Assigned successfully. Active enrollment, " +
                            "room capacity, room availability, invigilator " +
                            "availability, student conflicts, mandatory " +
                            "equipment and active accommodations checked. " +
                            "Extra time applied: " +
                            maximumExtraTime + " minutes."
                    );

                    return assignmentRepository.save(assignment);
                }
            }
        }

        // No valid room and invigilator combination was found.
        String reason = rejectionReasons.isEmpty()
                ? "NO_SUITABLE_RESOURCE: No room and invigilator combination found"
                : String.join(
                        "; ",
                        rejectionReasons.stream()
                                .distinct()
                                .limit(10)
                                .collect(Collectors.toList())
                );

        return saveUnscheduled(
                exam,
                studentCount,
                effectiveDuration,
                reason
        );
    }

    // Check every mandatory equipment requirement for a room.
    private boolean roomMeetsRequirements(
            List<ExamRequirement> requirements,
            String roomId) {

        if (requirements == null || requirements.isEmpty()) {
            return true;
        }

        List<RoomEquipment> equipmentList =
                roomEquipmentRepository.findByRoomRoomId(roomId);

        if (equipmentList == null) {
            equipmentList = Collections.emptyList();
        }

        for (ExamRequirement requirement : requirements) {

            if (!requirement.isMandatory()) {
                continue;
            }

            if (!"EQUIPMENT".equalsIgnoreCase(
                    requirement.getRequirementType())) {
                continue;
            }

            String requiredName =
                    normalizeEquipmentName(
                            requirement.getRequirementValue());

            if (requiredName.isEmpty()) {
                return false;
            }

            boolean found = equipmentList.stream().anyMatch(e ->
                    e.isOperational() &&
                    e.getQuantity() != null &&
                    e.getQuantity() > 0 &&
                    requiredName.equals(
                            normalizeEquipmentName(e.getEquipmentName()))
            );

            if (!found) {
                return false;
            }
        }

        return true;
    }

    // Normalize equipment names to avoid case and whitespace mismatches.
    private String normalizeEquipmentName(String name) {

        if (name == null) {
            return "";
        }

        return name.trim()
                .replaceAll("\\s+", "_")
                .toUpperCase(Locale.ROOT);
    }

    // Check overlapping time intervals.
    private boolean overlaps(
            LocalTime start1,
            LocalTime end1,
            LocalTime start2,
            LocalTime end2) {

        if (start1 == null || end1 == null ||
                start2 == null || end2 == null) {
            return false;
        }

        return start1.isBefore(end2) &&
                end1.isAfter(start2);
    }

    // Save an unscheduled exam with an explanation.
    private ScheduleAssignment saveUnscheduled(
            Exam exam,
            int studentCount,
            int duration,
            String reason) {

        ScheduleAssignment assignment =
                new ScheduleAssignment();

        assignment.setAssignmentId(
                "ASN-" + exam.getExamId() + "-UNSCHEDULED"
        );

        assignment.setExam(exam);
        assignment.setExamDate(exam.getExamDate());
        assignment.setStudentCount(studentCount);
        assignment.setEffectiveDurationMinutes(duration);
        assignment.setStatus("UNSCHEDULED");
        assignment.setDecisionReason(reason);

        return assignmentRepository.save(assignment);
    }

    // Retrieve all scheduling decisions.
    public List<ScheduleAssignment> getAllAssignments() {
        return assignmentRepository.findAll();
    }

    // Retrieve one scheduling decision.
    public ScheduleAssignment getAssignment(String id) {

        return assignmentRepository.findById(id)
                .orElseThrow(() ->
                        new IllegalArgumentException(
                                "Assignment not found: " + id));
    }

    // Schedule all exams in date order.
    @Transactional
    public List<ScheduleAssignment> scheduleAllExams() {

        List<Exam> exams = examRepository.findAll();

        exams.sort(
                Comparator.comparing(
                        Exam::getExamDate,
                        Comparator.nullsLast(Comparator.naturalOrder())
                )
        );

        List<ScheduleAssignment> results =
                new ArrayList<>();

        for (Exam exam : exams) {
            results.add(scheduleExam(exam.getExamId()));
        }

        return results;
    }
}
