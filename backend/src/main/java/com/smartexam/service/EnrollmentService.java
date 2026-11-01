
package com.smartexam.service;

import com.smartexam.entity.Enrollment;
import com.smartexam.repository.EnrollmentRepository;
import org.springframework.stereotype.Service;
import java.util.List;

@Service
public class EnrollmentService {

    private final EnrollmentRepository repository;

    public EnrollmentService(EnrollmentRepository repository) {
        this.repository = repository;
    }

    public Enrollment addEnrollment(Enrollment enrollment) {
        return repository.save(enrollment);
    }

    public List<Enrollment> getAllEnrollments() {
        return repository.findAll();
    }
}
