
package com.smartexam.repository;

import com.smartexam.entity.InvigilatorAvailability;
import org.springframework.data.jpa.repository.JpaRepository;

import java.time.LocalDate;
import java.util.List;

public interface InvigilatorAvailabilityRepository
        extends JpaRepository<InvigilatorAvailability, String> {

    List<InvigilatorAvailability>
    findByInvigilatorInvigilatorIdAndAvailabilityDate(
            String invigilatorId, LocalDate date);

    List<InvigilatorAvailability> findByAvailabilityDate(
            LocalDate date);
}
