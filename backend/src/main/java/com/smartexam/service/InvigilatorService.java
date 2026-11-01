
package com.smartexam.service;

import com.smartexam.entity.Invigilator;
import com.smartexam.repository.InvigilatorRepository;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.List;

@Service
public class InvigilatorService {

    private final InvigilatorRepository repository;

    public InvigilatorService(InvigilatorRepository repository) {
        this.repository = repository;
    }

    public Invigilator create(Invigilator invigilator) {

        if (invigilator.getInvigilatorId() == null ||
                invigilator.getInvigilatorId().isBlank()) {
            throw new IllegalArgumentException("Invigilator ID is required");
        }

        if (repository.existsById(invigilator.getInvigilatorId())) {
            throw new IllegalArgumentException("Invigilator ID already exists");
        }

        if (invigilator.getEmail() == null ||
                invigilator.getEmail().isBlank()) {
            throw new IllegalArgumentException("Email is required");
        }

        if (repository.existsByEmail(invigilator.getEmail())) {
            throw new IllegalArgumentException("Email already exists");
        }

        if (invigilator.getFullName() == null ||
                invigilator.getFullName().isBlank()) {
            throw new IllegalArgumentException("Full name is required");
        }

        if (invigilator.getDepartment() == null ||
                invigilator.getDepartment().isBlank()) {
            throw new IllegalArgumentException("Department is required");
        }

        return repository.save(invigilator);
    }

    public List<Invigilator> getAll() {
        return repository.findAll();
    }

    public Invigilator getById(String id) {
        return repository.findById(id)
                .orElseThrow(() ->
                        new IllegalArgumentException(
                                "Invigilator not found: " + id));
    }

    public Invigilator update(String id, Invigilator updated) {

        Invigilator existing = getById(id);

        if (updated.getEmail() == null ||
                updated.getEmail().isBlank()) {
            throw new IllegalArgumentException("Email is required");
        }

        repository.findByEmail(updated.getEmail())
                .ifPresent(found -> {
                    if (!found.getInvigilatorId().equals(id)) {
                        throw new IllegalArgumentException(
                                "Email already belongs to another invigilator");
                    }
                });

        existing.setFullName(updated.getFullName());
        existing.setEmail(updated.getEmail());
        existing.setDepartment(updated.getDepartment());
        existing.setAvailable(updated.isAvailable());
        existing.setUnavailableDates(updated.getUnavailableDates());

        return repository.save(existing);
    }

    public Invigilator updateAvailability(
            String id, boolean available) {

        Invigilator existing = getById(id);
        existing.setAvailable(available);

        return repository.save(existing);
    }

    public boolean isAvailableOnDate(String id, LocalDate date) {

        Invigilator invigilator = getById(id);

        return invigilator.isAvailable()
                && !invigilator.getUnavailableDates().contains(date);
    }
}
