
package com.smartexam.controller;

import com.smartexam.entity.ExamRequirement;
import com.smartexam.service.ExamRequirementService;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/exam-requirements")
public class ExamRequirementController {

    private final ExamRequirementService service;

    public ExamRequirementController(ExamRequirementService service) {
        this.service = service;
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public ExamRequirement create(
            @RequestBody ExamRequirement requirement) {
        return service.create(requirement);
    }

    @GetMapping
    public List<ExamRequirement> getAll() {
        return service.getAll();
    }

    @GetMapping("/exam/{examId}")
    public List<ExamRequirement> getByExam(
            @PathVariable String examId) {
        return service.getByExam(examId);
    }
}
