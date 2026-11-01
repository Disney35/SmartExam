
package com.smartexam.service;

import com.smartexam.entity.Exam;
import com.smartexam.repository.ExamRepository;
import org.springframework.stereotype.Service;
import java.util.List;

@Service
public class ExamService {

    private final ExamRepository repository;

    public ExamService(ExamRepository repository) {
        this.repository = repository;
    }

    public Exam addExam(Exam exam) {
        return repository.save(exam);
    }

    public List<Exam> getAllExams() {
        return repository.findAll();
    }
}
