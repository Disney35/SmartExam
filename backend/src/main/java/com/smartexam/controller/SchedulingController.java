
package com.smartexam.controller;

import com.smartexam.entity.ScheduleAssignment;
import com.smartexam.service.SchedulingService;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/scheduling")
public class SchedulingController {

    private final SchedulingService service;

    public SchedulingController(SchedulingService service) {
        this.service = service;
    }

    // Schedule one exam
    @PostMapping("/exam/{examId}")
    public ScheduleAssignment scheduleExam(
            @PathVariable String examId) {
        return service.scheduleExam(examId);
    }

    // Schedule all exams
    @PostMapping("/run")
    public List<ScheduleAssignment> scheduleAll() {
        return service.scheduleAllExams();
    }

    // Get all scheduling decisions
    @GetMapping("/assignments")
    public List<ScheduleAssignment> getAll() {
        return service.getAllAssignments();
    }

    // Get one scheduling decision
    @GetMapping("/assignments/{id}")
    public ScheduleAssignment getById(@PathVariable String id) {
        return service.getAssignment(id);
    }
}
