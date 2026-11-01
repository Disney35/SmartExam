
package com.smartexam.controller;

import com.smartexam.entity.Course;
import com.smartexam.service.CourseService;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/courses")
public class CourseController {

    private final CourseService courseService;

    public CourseController(CourseService courseService) {
        this.courseService = courseService;
    }

    // Add Course
    @PostMapping
    public ResponseEntity<Course> addCourse(
            @RequestBody Course course) {

        return ResponseEntity.status(HttpStatus.CREATED)
                .body(courseService.addCourse(course));
    }

    // Get All Courses
    @GetMapping
    public ResponseEntity<List<Course>> getAllCourses() {

        return ResponseEntity.ok(
                courseService.getAllCourses());
    }

    // Get Course By ID
    @GetMapping("/{courseId}")
    public ResponseEntity<Course> getCourseById(
            @PathVariable String courseId) {

        return ResponseEntity.ok(
                courseService.getCourseById(courseId));
    }
}
