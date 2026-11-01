
package com.smartexam.controller;

import com.smartexam.entity.ScheduleAssignment;
import com.smartexam.repository.ScheduleAssignmentRepository;

import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/export")
public class ScheduleExportController {

    private final ScheduleAssignmentRepository assignmentRepository;

    public ScheduleExportController(
            ScheduleAssignmentRepository assignmentRepository) {
        this.assignmentRepository = assignmentRepository;
    }

    // ==========================================
    // JSON EXPORT
    // ==========================================

    @GetMapping(
        value = "/schedules/json",
        produces = MediaType.APPLICATION_JSON_VALUE
    )
    public ResponseEntity<List<Map<String, Object>>> exportSchedulesJson() {

        List<ScheduleAssignment> assignments =
                assignmentRepository.findAll();

        List<Map<String, Object>> result = new ArrayList<>();

        for (ScheduleAssignment assignment : assignments) {

            Map<String, Object> row = new LinkedHashMap<>();

            row.put("assignmentId", assignment.getAssignmentId());

            // Exam details
            row.put("examId",
                    assignment.getExam() != null
                    ? assignment.getExam().getExamId() : null);

            row.put("examName",
                    assignment.getExam() != null
                    ? assignment.getExam().getExamName() : null);

            row.put("examDate", assignment.getExamDate());

            row.put("originalDurationMinutes",
                    assignment.getExam() != null
                    ? assignment.getExam().getDurationMinutes() : null);

            // Room details
            row.put("roomId",
                    assignment.getRoom() != null
                    ? assignment.getRoom().getRoomId() : null);

            row.put("roomName",
                    assignment.getRoom() != null
                    ? assignment.getRoom().getRoomName() : null);

            row.put("building",
                    assignment.getRoom() != null
                    ? assignment.getRoom().getBuilding() : null);

            row.put("roomCapacity",
                    assignment.getRoom() != null
                    ? assignment.getRoom().getCapacity() : null);

            row.put("accessibilityAvailable",
                    assignment.getRoom() != null
                    ? assignment.getRoom().getAccessibilityAvailable() : null);

            // Invigilator details
            row.put("invigilatorId",
                    assignment.getInvigilator() != null
                    ? assignment.getInvigilator().getInvigilatorId() : null);

            row.put("invigilatorName",
                    assignment.getInvigilator() != null
                    ? assignment.getInvigilator().getFullName() : null);

            // Schedule details
            row.put("startTime", assignment.getStartTime());
            row.put("endTime", assignment.getEndTime());
            row.put("studentCount", assignment.getStudentCount());

            row.put("effectiveDurationMinutes",
                    assignment.getEffectiveDurationMinutes());

            row.put("status", assignment.getStatus());

            row.put("decisionReason",
                    assignment.getDecisionReason());

            result.add(row);
        }

        return ResponseEntity.ok()
                .header(
                    HttpHeaders.CONTENT_DISPOSITION,
                    "attachment; filename=\"schedule_assignments.json\""
                )
                .contentType(MediaType.APPLICATION_JSON)
                .body(result);
    }

    // ==========================================
    // CSV EXPORT
    // ==========================================

    @GetMapping(
        value = "/schedules/csv",
        produces = "text/csv"
    )
    public ResponseEntity<byte[]> exportSchedulesCsv() {

        StringBuilder csv = new StringBuilder();

        // UTF-8 BOM for Excel compatibility
        csv.append('\uFEFF');

        csv.append(
            "Assignment ID," +
            "Exam ID," +
            "Exam Name," +
            "Exam Date," +
            "Original Duration Minutes," +
            "Room ID," +
            "Room Name," +
            "Building," +
            "Room Capacity," +
            "Accessibility Available," +
            "Invigilator ID," +
            "Invigilator Name," +
            "Start Time," +
            "End Time," +
            "Student Count," +
            "Effective Duration Minutes," +
            "Status," +
            "Decision Reason\n"
        );

        List<ScheduleAssignment> assignments =
                assignmentRepository.findAll();

        for (ScheduleAssignment assignment : assignments) {

            csv.append(escape(assignment.getAssignmentId())).append(",");

            csv.append(escape(
                    assignment.getExam() != null
                    ? assignment.getExam().getExamId() : null)).append(",");

            csv.append(escape(
                    assignment.getExam() != null
                    ? assignment.getExam().getExamName() : null)).append(",");

            csv.append(escape(assignment.getExamDate())).append(",");

            csv.append(escape(
                    assignment.getExam() != null
                    ? assignment.getExam().getDurationMinutes() : null)).append(",");

            csv.append(escape(
                    assignment.getRoom() != null
                    ? assignment.getRoom().getRoomId() : null)).append(",");

            csv.append(escape(
                    assignment.getRoom() != null
                    ? assignment.getRoom().getRoomName() : null)).append(",");

            csv.append(escape(
                    assignment.getRoom() != null
                    ? assignment.getRoom().getBuilding() : null)).append(",");

            csv.append(escape(
                    assignment.getRoom() != null
                    ? assignment.getRoom().getCapacity() : null)).append(",");

            csv.append(escape(
                    assignment.getRoom() != null
                    ? assignment.getRoom().getAccessibilityAvailable() : null)).append(",");

            csv.append(escape(
                    assignment.getInvigilator() != null
                    ? assignment.getInvigilator().getInvigilatorId() : null)).append(",");

            csv.append(escape(
                    assignment.getInvigilator() != null
                    ? assignment.getInvigilator().getFullName() : null)).append(",");

            csv.append(escape(assignment.getStartTime())).append(",");
            csv.append(escape(assignment.getEndTime())).append(",");
            csv.append(escape(assignment.getStudentCount())).append(",");
            csv.append(escape(assignment.getEffectiveDurationMinutes())).append(",");
            csv.append(escape(assignment.getStatus())).append(",");
            csv.append(escape(assignment.getDecisionReason())).append("\n");
        }

        byte[] csvBytes = csv.toString()
                .getBytes(StandardCharsets.UTF_8);

        return ResponseEntity.ok()
                .header(
                    HttpHeaders.CONTENT_DISPOSITION,
                    "attachment; filename=\"schedule_assignments.csv\""
                )
                .contentType(
                    MediaType.parseMediaType("text/csv;charset=UTF-8")
                )
                .contentLength(csvBytes.length)
                .body(csvBytes);
    }

    // ==========================================
    // CSV VALUE ESCAPING
    // ==========================================

    private String escape(Object value) {

        if (value == null) {
            return "\"\"";
        }

        String text = String.valueOf(value);

        // Escape double quotes according to CSV rules
        text = text.replace("\"", "\"\"");

        return "\"" + text + "\"";
    }
}
