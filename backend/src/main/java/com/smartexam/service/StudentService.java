
package com.smartexam.service;

import com.smartexam.entity.Student;
import com.smartexam.repository.StudentRepository;

import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class StudentService {

    private final StudentRepository studentRepository;

    public StudentService(StudentRepository studentRepository) {
        this.studentRepository = studentRepository;
    }

    // Add a new student
    public Student addStudent(Student student) {
        if (student.getStudentId() == null ||
                student.getStudentId().isBlank()) {
            throw new IllegalArgumentException(
                    "Student ID is required");
        }

        if (studentRepository.existsById(student.getStudentId())) {
            throw new IllegalArgumentException(
                    "Student ID already exists");
        }

        return studentRepository.save(student);
    }

    // Get all students
    public List<Student> getAllStudents() {
        return studentRepository.findAll();
    }

    // Get student by ID
    public Student getStudentById(String studentId) {
        return studentRepository.findById(studentId)
                .orElseThrow(() ->
                        new IllegalArgumentException(
                                "Student not found: " + studentId));
    }

    // Update student
    public Student updateStudent(
            String studentId, Student updatedStudent) {

        Student existingStudent = getStudentById(studentId);

        existingStudent.setFullName(updatedStudent.getFullName());
        existingStudent.setEmail(updatedStudent.getEmail());
        existingStudent.setDepartment(updatedStudent.getDepartment());

        return studentRepository.save(existingStudent);
    }

    // Delete student
    public void deleteStudent(String studentId) {
        Student student = getStudentById(studentId);
        studentRepository.delete(student);
    }
}
