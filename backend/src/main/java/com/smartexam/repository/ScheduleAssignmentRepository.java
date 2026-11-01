
package com.smartexam.repository;

import com.smartexam.entity.ScheduleAssignment;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface ScheduleAssignmentRepository
        extends JpaRepository<ScheduleAssignment, String> {

    Optional<ScheduleAssignment> findByExamExamId(String examId);

    List<ScheduleAssignment> findByStatus(String status);
}
