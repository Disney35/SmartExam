
package com.smartexam.repository;

import com.smartexam.entity.ExamRequirement;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface ExamRequirementRepository
        extends JpaRepository<ExamRequirement, String> {

    List<ExamRequirement> findByExamExamId(String examId);
}
