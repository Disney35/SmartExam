
package com.smartexam.service;

import com.smartexam.entity.Invigilator;
import com.smartexam.entity.InvigilatorAvailability;
import com.smartexam.repository.InvigilatorAvailabilityRepository;
import com.smartexam.repository.InvigilatorRepository;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.List;

@Service
public class InvigilatorAvailabilityService {

    private final InvigilatorAvailabilityRepository repository;
    private final InvigilatorRepository invigilatorRepository;

    public InvigilatorAvailabilityService(
            InvigilatorAvailabilityRepository repository,
            InvigilatorRepository invigilatorRepository) {
        this.repository = repository;
        this.invigilatorRepository = invigilatorRepository;
    }

    public InvigilatorAvailability create(
            InvigilatorAvailability availability) {

        if (availability.getAvailabilityId() == null ||
                availability.getAvailabilityId().isBlank()) {
            throw new IllegalArgumentException(
                    "Availability ID is required");
        }

        if (repository.existsById(availability.getAvailabilityId())) {
            throw new IllegalArgumentException(
                    "Availability ID already exists");
        }

        if (availability.getInvigilator() == null ||
                availability.getInvigilator().getInvigilatorId() == null) {
            throw new IllegalArgumentException(
                    "Invigilator ID is required");
        }

        String invigilatorId =
                availability.getInvigilator().getInvigilatorId();

        Invigilator invigilator =
                invigilatorRepository.findById(invigilatorId)
                        .orElseThrow(() ->
                                new IllegalArgumentException(
                                        "Invigilator not found: " +
                                                invigilatorId));

        if (availability.getAvailabilityDate() == null ||
                availability.getStartTime() == null ||
                availability.getEndTime() == null) {
            throw new IllegalArgumentException(
                    "Date, start time and end time are required");
        }

        if (!availability.getStartTime().isBefore(
                availability.getEndTime())) {
            throw new IllegalArgumentException(
                    "End time must be after start time");
        }

        List<InvigilatorAvailability> existing =
                repository
                        .findByInvigilatorInvigilatorIdAndAvailabilityDate(
                                invigilatorId,
                                availability.getAvailabilityDate());

        for (InvigilatorAvailability record : existing) {

            boolean overlaps =
                    availability.getStartTime().isBefore(
                            record.getEndTime())
                    && availability.getEndTime().isAfter(
                            record.getStartTime());

            if (overlaps) {
                throw new IllegalArgumentException(
                        "Overlapping availability record exists " +
                                "for this invigilator and date");
            }
        }

        availability.setInvigilator(invigilator);

        return repository.save(availability);
    }

    public List<InvigilatorAvailability> getAll() {
        return repository.findAll();
    }

    public InvigilatorAvailability getById(String id) {
        return repository.findById(id)
                .orElseThrow(() ->
                        new IllegalArgumentException(
                                "Availability record not found: " + id));
    }

    public List<InvigilatorAvailability> getByInvigilatorAndDate(
            String invigilatorId, LocalDate date) {

        if (!invigilatorRepository.existsById(invigilatorId)) {
            throw new IllegalArgumentException(
                    "Invigilator not found: " + invigilatorId);
        }

        return repository
                .findByInvigilatorInvigilatorIdAndAvailabilityDate(
                        invigilatorId, date);
    }

    public List<InvigilatorAvailability> getByDate(LocalDate date) {
        return repository.findByAvailabilityDate(date);
    }
}
