
package com.smartexam.controller;

import com.smartexam.entity.Accommodation;
import com.smartexam.service.AccommodationService;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/accommodations")
public class AccommodationController {

    private final AccommodationService service;

    public AccommodationController(AccommodationService service) {
        this.service = service;
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public Accommodation create(@RequestBody Accommodation accommodation) {
        return service.create(accommodation);
    }

    @GetMapping
    public List<Accommodation> getAll() {
        return service.getAll();
    }

    @GetMapping("/{id}")
    public Accommodation getById(@PathVariable String id) {
        return service.getById(id);
    }

    @GetMapping("/student/{studentId}")
    public List<Accommodation> getByStudent(
            @PathVariable String studentId) {
        return service.getByStudent(studentId);
    }

    @PutMapping("/{id}")
    public Accommodation update(
            @PathVariable String id,
            @RequestBody Accommodation accommodation) {
        return service.update(id, accommodation);
    }
}
