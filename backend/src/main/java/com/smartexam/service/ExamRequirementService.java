
package com.smartexam.service;

import com.smartexam.entity.Exam;
import com.smartexam.entity.ExamRequirement;
import com.smartexam.repository.ExamRepository;
import com.smartexam.repository.ExamRequirementRepository;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class ExamRequirementService {

    private final ExamRequirementRepository repository;
    private final ExamRepository examRepository;

    public ExamRequirementService(
            ExamRequirementRepository repository,
            ExamRepository examRepository) {
        this.repository = repository;
        this.examRepository = examRepository;
    }

    public ExamRequirement create(ExamRequirement requirement) {

        if (requirement.getRequirementId() == null ||
                requirement.getRequirementId().isBlank()) {
            throw new IllegalArgumentException("Requirement ID is required");
        }

        if (repository.existsById(requirement.getRequirementId())) {
            throw new IllegalArgumentException("Requirement ID already exists");
        }

        if (requirement.getExam() == null ||
                requirement.getExam().getExamId() == null) {
            throw new IllegalArgumentException("Exam ID is required");
        }

        String examId = requirement.getExam().getExamId();

        Exam exam = examRepository.findById(examId)
                .orElseThrow(() ->
                        new IllegalArgumentException("Exam not found: " + examId));

        if (requirement.getRequirementType() == null ||
                requirement.getRequirementType().isBlank() ||
                requirement.getRequirementValue() == null ||
                requirement.getRequirementValue().isBlank()) {
            throw new IllegalArgumentException(
                    "Requirement type and value are required");
        }

        requirement.setExam(exam);
        return repository.save(requirement);
    }

    public List<ExamRequirement> getAll() {
        return repository.findAll();
    }

    public List<ExamRequirement> getByExam(String examId) {
        if (!examRepository.existsById(examId)) {
            throw new IllegalArgumentException("Exam not found: " + examId);
        }
        return repository.findByExamExamId(examId);
    }
}
