
package com.smartexam.service;

import com.smartexam.entity.Course;
import com.smartexam.repository.CourseRepository;

import org.springframework.stereotype.Service;
import java.util.List;

@Service
public class CourseService {

    private final CourseRepository courseRepository;

    public CourseService(CourseRepository courseRepository) {
        this.courseRepository = courseRepository;
    }

    public Course addCourse(Course course) {

        if (course.getCourseId() == null ||
                course.getCourseId().isBlank()) {
            throw new IllegalArgumentException(
                    "Course ID is required");
        }

        if (courseRepository.existsById(course.getCourseId())) {
            throw new IllegalArgumentException(
                    "Course ID already exists");
        }

        return courseRepository.save(course);
    }

    public List<Course> getAllCourses() {
        return courseRepository.findAll();
    }

    public Course getCourseById(String id) {
        return courseRepository.findById(id)
                .orElseThrow(() ->
                        new IllegalArgumentException(
                                "Course not found: " + id));
    }
}
