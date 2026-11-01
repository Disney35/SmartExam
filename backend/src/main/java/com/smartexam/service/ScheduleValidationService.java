
package com.smartexam.service;

import com.smartexam.entity.*;
import com.smartexam.repository.*;

import org.springframework.stereotype.Service;

import java.time.LocalTime;
import java.util.*;
import java.util.stream.Collectors;

@Service
public class ScheduleValidationService {

    private final ScheduleAssignmentRepository assignmentRepository;
    private final EnrollmentRepository enrollmentRepository;
    private final AccommodationRepository accommodationRepository;
    private final RoomAvailabilityRepository roomAvailabilityRepository;
    private final InvigilatorAvailabilityRepository invigilatorAvailabilityRepository;
    private final ExamRequirementRepository examRequirementRepository;
    private final RoomEquipmentRepository roomEquipmentRepository;

    public ScheduleValidationService(
            ScheduleAssignmentRepository assignmentRepository,
            EnrollmentRepository enrollmentRepository,
            AccommodationRepository accommodationRepository,
            RoomAvailabilityRepository roomAvailabilityRepository,
            InvigilatorAvailabilityRepository invigilatorAvailabilityRepository,
            ExamRequirementRepository examRequirementRepository,
            RoomEquipmentRepository roomEquipmentRepository) {

        this.assignmentRepository = assignmentRepository;
        this.enrollmentRepository = enrollmentRepository;
        this.accommodationRepository = accommodationRepository;
        this.roomAvailabilityRepository = roomAvailabilityRepository;
        this.invigilatorAvailabilityRepository =
                invigilatorAvailabilityRepository;
        this.examRequirementRepository = examRequirementRepository;
        this.roomEquipmentRepository = roomEquipmentRepository;
    }

    public Map<String, Object> validateSchedule() {

        List<ScheduleAssignment> assignments =
                assignmentRepository.findAll();

        List<Enrollment> enrollments =
                enrollmentRepository.findAll();

        List<Accommodation> accommodations =
                accommodationRepository.findAll();

        List<Map<String, Object>> results = new ArrayList<>();

        int scheduled = 0;
        int unscheduled = 0;
        int valid = 0;
        int invalid = 0;

        for (ScheduleAssignment assignment : assignments) {

            Map<String, Object> item = new LinkedHashMap<>();
            List<String> errors = new ArrayList<>();
            List<String> warnings = new ArrayList<>();

            String status = assignment.getStatus();

            item.put("assignmentId", assignment.getAssignmentId());
            item.put("examId", assignment.getExam() == null
                    ? null : assignment.getExam().getExamId());
            item.put("status", status);

            if (!"SCHEDULED".equalsIgnoreCase(status)) {

                unscheduled++;

                String reason = assignment.getDecisionReason();

                if (reason == null || reason.trim().isEmpty()) {
                    errors.add("UNSCHEDULED_REASON_MISSING");
                }

                item.put("validationStatus",
                        errors.isEmpty() ? "VALID_UNSCHEDULED" : "INVALID");

                item.put("decisionReason", reason);
                item.put("errors", errors);
                item.put("warnings", warnings);

                if (errors.isEmpty()) {
                    valid++;
                } else {
                    invalid++;
                }

                results.add(item);
                continue;
            }

            scheduled++;

            Exam exam = assignment.getExam();
            Room room = assignment.getRoom();
            Invigilator invigilator = assignment.getInvigilator();

            if (exam == null) {
                errors.add("EXAM_REFERENCE_MISSING");
            }

            if (room == null) {
                errors.add("ROOM_NOT_ASSIGNED");
            }

            if (invigilator == null) {
                errors.add("INVIGILATOR_NOT_ASSIGNED");
            }

            if (assignment.getExamDate() == null ||
                    assignment.getStartTime() == null ||
                    assignment.getEndTime() == null) {

                errors.add("SCHEDULE_DATE_OR_TIME_MISSING");

            } else if (!assignment.getEndTime()
                    .isAfter(assignment.getStartTime())) {

                errors.add("INVALID_TIME_INTERVAL");
            }

            if (exam != null && room != null &&
                    assignment.getStudentCount() != null) {

                // Verify active enrollment count.
                Set<String> studentIds = getStudentIds(
                        exam.getCourse() == null
                                ? null
                                : exam.getCourse().getCourseId(),
                        enrollments
                );

                if (studentIds.size() != assignment.getStudentCount()) {
                    errors.add("STUDENT_COUNT_MISMATCH: Expected "
                            + studentIds.size()
                            + ", recorded "
                            + assignment.getStudentCount());
                }

                // Verify room capacity.
                if (room.getCapacity() == null ||
                        room.getCapacity() < studentIds.size()) {

                    errors.add("ROOM_CAPACITY_EXCEEDED");
                }

                // Verify accessibility.
                boolean accessibilityRequired =
                        requiresAccessibility(studentIds, accommodations);

                if (accessibilityRequired &&
                        !Boolean.TRUE.equals(
                                room.getAccessibilityAvailable())) {

                    errors.add("ACCESSIBILITY_REQUIREMENT_NOT_MET");
                }

                // Verify accommodation duration.
                int expectedDuration =
                        calculateEffectiveDuration(
                                exam,
                                studentIds,
                                accommodations
                        );

                if (assignment.getEffectiveDurationMinutes() == null ||
                        assignment.getEffectiveDurationMinutes()
                                < expectedDuration) {

                    errors.add("ACCOMMODATION_DURATION_MISMATCH: Expected at least "
                            + expectedDuration + " minutes");
                }

                // Verify room availability.
                if (assignment.getExamDate() != null &&
                        assignment.getStartTime() != null &&
                        assignment.getEndTime() != null) {

                    boolean available =
                            roomAvailabilityRepository
                                    .findByAvailabilityDate(
                                            assignment.getExamDate())
                                    .stream()
                                    .anyMatch(w ->
                                            w.getRoom() != null &&
                                            room.getRoomId().equals(
                                                    w.getRoom().getRoomId()) &&
                                            w.isAvailable() &&
                                            w.getStartTime() != null &&
                                            w.getEndTime() != null &&
                                            !assignment.getStartTime()
                                                    .isBefore(w.getStartTime()) &&
                                            !assignment.getEndTime()
                                                    .isAfter(w.getEndTime())
                                    );

                    if (!available) {
                        errors.add("ROOM_AVAILABILITY_VIOLATION");
                    }
                }

                // Verify mandatory equipment.
                List<ExamRequirement> requirements =
                        examRequirementRepository.findByExamExamId(
                                exam.getExamId());

                List<RoomEquipment> equipment =
                        roomEquipmentRepository.findByRoomRoomId(
                                room.getRoomId());

                for (ExamRequirement requirement : requirements) {

                    if (!requirement.isMandatory() ||
                            !"EQUIPMENT".equalsIgnoreCase(
                                    requirement.getRequirementType())) {
                        continue;
                    }

                    String required = normalize(
                            requirement.getRequirementValue());

                    boolean found = equipment.stream().anyMatch(e ->
                            e.isOperational() &&
                            e.getQuantity() != null &&
                            e.getQuantity() > 0 &&
                            required.equals(
                                    normalize(e.getEquipmentName()))
                    );

                    if (!found) {
                        errors.add("MANDATORY_EQUIPMENT_MISSING: "
                                + requirement.getRequirementValue());
                    }
                }

                // Verify student timetable conflicts.
                for (ScheduleAssignment other : assignments) {

                    if (other == assignment ||
                            !"SCHEDULED".equalsIgnoreCase(other.getStatus()) ||
                            other.getExam() == null ||
                            other.getExam().getCourse() == null ||
                            other.getExamDate() == null ||
                            !other.getExamDate().equals(
                                    assignment.getExamDate()) ||
                            !overlaps(
                                    assignment.getStartTime(),
                                    assignment.getEndTime(),
                                    other.getStartTime(),
                                    other.getEndTime())) {
                        continue;
                    }

                    Set<String> otherStudents = getStudentIds(
                            other.getExam().getCourse().getCourseId(),
                            enrollments
                    );

                    if (!Collections.disjoint(studentIds, otherStudents)) {
                        errors.add("STUDENT_TIMETABLE_CONFLICT_WITH: "
                                + other.getAssignmentId());
                    }
                }
            }

            // Verify invigilator availability.
            if (invigilator != null &&
                    assignment.getExamDate() != null &&
                    assignment.getStartTime() != null &&
                    assignment.getEndTime() != null) {

                if (!invigilator.isAvailable()) {
                    errors.add("INVIGILATOR_NOT_GENERALLY_AVAILABLE");
                }

                if (invigilator.getUnavailableDates() != null &&
                        invigilator.getUnavailableDates().contains(
                                assignment.getExamDate())) {

                    errors.add("INVIGILATOR_UNAVAILABLE_ON_EXAM_DATE");
                }

                boolean windowAvailable =
                        invigilatorAvailabilityRepository
                                .findByAvailabilityDate(
                                        assignment.getExamDate())
                                .stream()
                                .anyMatch(w ->
                                        w.getInvigilator() != null &&
                                        invigilator.getInvigilatorId().equals(
                                                w.getInvigilator()
                                                        .getInvigilatorId()) &&
                                        w.isAvailable() &&
                                        w.getStartTime() != null &&
                                        w.getEndTime() != null &&
                                        !assignment.getStartTime()
                                                .isBefore(w.getStartTime()) &&
                                        !assignment.getEndTime()
                                                .isAfter(w.getEndTime())
                                );

                if (!windowAvailable) {
                    errors.add("INVIGILATOR_AVAILABILITY_VIOLATION");
                }
            }

            // Check room and invigilator double-booking.
            for (ScheduleAssignment other : assignments) {

                if (other == assignment ||
                        !"SCHEDULED".equalsIgnoreCase(other.getStatus()) ||
                        other.getExamDate() == null ||
                        !other.getExamDate().equals(
                                assignment.getExamDate()) ||
                        !overlaps(
                                assignment.getStartTime(),
                                assignment.getEndTime(),
                                other.getStartTime(),
                                other.getEndTime())) {
                    continue;
                }

                if (room != null && other.getRoom() != null &&
                        room.getRoomId().equals(
                                other.getRoom().getRoomId())) {

                    errors.add("ROOM_DOUBLE_BOOKING_WITH: "
                            + other.getAssignmentId());
                }

                if (invigilator != null &&
                        other.getInvigilator() != null &&
                        invigilator.getInvigilatorId().equals(
                                other.getInvigilator()
                                        .getInvigilatorId())) {

                    errors.add("INVIGILATOR_DOUBLE_BOOKING_WITH: "
                            + other.getAssignmentId());
                }
            }

            item.put("validationStatus",
                    errors.isEmpty() ? "VALID" : "INVALID");

            item.put("roomId", room == null ? null : room.getRoomId());
            item.put("invigilatorId",
                    invigilator == null
                            ? null : invigilator.getInvigilatorId());

            item.put("examDate", assignment.getExamDate());
            item.put("startTime", assignment.getStartTime());
            item.put("endTime", assignment.getEndTime());
            item.put("errors", errors);
            item.put("warnings", warnings);

            if (errors.isEmpty()) {
                valid++;
            } else {
                invalid++;
            }

            results.add(item);
        }

        Map<String, Object> summary = new LinkedHashMap<>();

        summary.put("totalAssignments", assignments.size());
        summary.put("scheduledAssignments", scheduled);
        summary.put("unscheduledAssignments", unscheduled);
        summary.put("validAssignments", valid);
        summary.put("invalidAssignments", invalid);
        summary.put("hardConstraintViolations", invalid);
        summary.put("overallStatus",
                invalid == 0 ? "PASSED" : "FAILED");
        summary.put("results", results);

        return summary;
    }

    private Set<String> getStudentIds(
            String courseId,
            List<Enrollment> enrollments) {

        if (courseId == null) {
            return Collections.emptySet();
        }

        return enrollments.stream()
                .filter(e -> e.getCourse() != null)
                .filter(e -> e.getStudent() != null)
                .filter(e -> courseId.equals(
                        e.getCourse().getCourseId()))
                .filter(e -> "ACTIVE".equalsIgnoreCase(e.getStatus()))
                .map(e -> e.getStudent().getStudentId())
                .filter(Objects::nonNull)
                .collect(Collectors.toSet());
    }

    private boolean requiresAccessibility(
            Set<String> studentIds,
            List<Accommodation> accommodations) {

        return accommodations.stream().anyMatch(a -> {

            if (!a.isActive() || a.getStudent() == null ||
                    !studentIds.contains(
                            a.getStudent().getStudentId())) {
                return false;
            }

            String type = a.getAccommodationType();

            if (type == null) {
                return false;
            }

            String value = type.toUpperCase(Locale.ROOT);

            return value.contains("ACCESS") ||
                    value.contains("WHEEL") ||
                    value.contains("MOBILITY");
        });
    }

    private int calculateEffectiveDuration(
            Exam exam,
            Set<String> studentIds,
            List<Accommodation> accommodations) {

        int extraTime = accommodations.stream()
                .filter(Accommodation::isActive)
                .filter(a -> a.getStudent() != null)
                .filter(a -> studentIds.contains(
                        a.getStudent().getStudentId()))
                .map(Accommodation::getExtraTimeMinutes)
                .filter(Objects::nonNull)
                .mapToInt(Integer::intValue)
                .max()
                .orElse(0);

        return exam.getDurationMinutes() + extraTime;
    }

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

    private String normalize(String value) {

        if (value == null) {
            return "";
        }

        return value.trim()
                .replaceAll("\\s+", "_")
                .toUpperCase(Locale.ROOT);
    }
}
