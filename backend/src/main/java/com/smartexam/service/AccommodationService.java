
package com.smartexam.service;

import com.smartexam.entity.Accommodation;
import com.smartexam.entity.Student;
import com.smartexam.repository.AccommodationRepository;
import com.smartexam.repository.StudentRepository;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class AccommodationService {

    private final AccommodationRepository repository;
    private final StudentRepository studentRepository;

    public AccommodationService(
            AccommodationRepository repository,
            StudentRepository studentRepository) {
        this.repository = repository;
        this.studentRepository = studentRepository;
    }

    public Accommodation create(Accommodation accommodation) {

        if (accommodation.getAccommodationId() == null ||
                accommodation.getAccommodationId().isBlank()) {
            throw new IllegalArgumentException("Accommodation ID is required");
        }

        if (repository.existsById(accommodation.getAccommodationId())) {
            throw new IllegalArgumentException("Accommodation ID already exists");
        }

        if (accommodation.getStudent() == null ||
                accommodation.getStudent().getStudentId() == null) {
            throw new IllegalArgumentException("Student ID is required");
        }

        String studentId = accommodation.getStudent().getStudentId();

        Student student = studentRepository.findById(studentId)
                .orElseThrow(() ->
                        new IllegalArgumentException("Student not found: " + studentId));

        if (accommodation.getAccommodationType() == null ||
                accommodation.getAccommodationType().isBlank()) {
            throw new IllegalArgumentException("Accommodation type is required");
        }

        if (accommodation.getExtraTimeMinutes() == null ||
                accommodation.getExtraTimeMinutes() < 0) {
            throw new IllegalArgumentException("Extra time cannot be negative");
        }

        accommodation.setStudent(student);

        return repository.save(accommodation);
    }

    public List<Accommodation> getAll() {
        return repository.findAll();
    }

    public Accommodation getById(String id) {
        return repository.findById(id)
                .orElseThrow(() ->
                        new IllegalArgumentException("Accommodation not found: " + id));
    }

    public List<Accommodation> getByStudent(String studentId) {

        if (!studentRepository.existsById(studentId)) {
            throw new IllegalArgumentException("Student not found: " + studentId);
        }

        return repository.findByStudentStudentId(studentId);
    }

    public Accommodation update(String id, Accommodation updated) {

        Accommodation existing = getById(id);

        if (updated.getAccommodationType() == null ||
                updated.getAccommodationType().isBlank()) {
            throw new IllegalArgumentException("Accommodation type is required");
        }

        if (updated.getExtraTimeMinutes() == null ||
                updated.getExtraTimeMinutes() < 0) {
            throw new IllegalArgumentException("Extra time cannot be negative");
        }

        existing.setAccommodationType(updated.getAccommodationType());
        existing.setExtraTimeMinutes(updated.getExtraTimeMinutes());
        existing.setDetails(updated.getDetails());
        existing.setActive(updated.isActive());

        return repository.save(existing);
    }
}
