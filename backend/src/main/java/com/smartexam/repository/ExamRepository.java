
package com.smartexam.repository;

import com.smartexam.entity.Exam;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ExamRepository
        extends JpaRepository<Exam, String> {
}
