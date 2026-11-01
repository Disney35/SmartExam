
package com.smartexam.controller;

import com.smartexam.entity.Exam;
import com.smartexam.service.ExamService;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/exams")
public class ExamController {

    private final ExamService service;

    public ExamController(ExamService service) {
        this.service = service;
    }

    @PostMapping
    public ResponseEntity<Exam> addExam(
            @RequestBody Exam exam) {

        return ResponseEntity.status(HttpStatus.CREATED)
                .body(service.addExam(exam));
    }

    @GetMapping
    public ResponseEntity<List<Exam>> getAllExams() {

        return ResponseEntity.ok(service.getAllExams());
    }
}
