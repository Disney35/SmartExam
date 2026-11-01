
package com.smartexam.repository;

import com.smartexam.entity.Accommodation;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface AccommodationRepository
        extends JpaRepository<Accommodation, String> {

    List<Accommodation> findByStudentStudentId(String studentId);

    List<Accommodation> findByStudentStudentIdAndActiveTrue(String studentId);
}
