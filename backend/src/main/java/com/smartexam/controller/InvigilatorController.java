package com.smartexam.controller;

import com.smartexam.entity.Invigilator;
import com.smartexam.service.InvigilatorService;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.util.List;

@RestController
@RequestMapping("/api/invigilators")
public class InvigilatorController {

    private final InvigilatorService service;

    public InvigilatorController(InvigilatorService service) {
        this.service = service;
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public Invigilator create(@RequestBody Invigilator invigilator) {
        return service.create(invigilator);
    }

    @GetMapping
    public List<Invigilator> getAll() {
        return service.getAll();
    }

    @GetMapping("/{id}")
    public Invigilator getById(@PathVariable String id) {
        return service.getById(id);
    }

    @PutMapping("/{id}")
    public Invigilator update(
            @PathVariable String id,
            @RequestBody Invigilator invigilator) {
        return service.update(id, invigilator);
    }

    @PatchMapping("/{id}/availability")
    public Invigilator updateAvailability(
            @PathVariable String id,
            @RequestBody AvailabilityRequest request) {
        return service.updateAvailability(id, request.isAvailable());
    }

    @GetMapping("/{id}/availability")
    public boolean isAvailableOnDate(
            @PathVariable String id,
            @RequestParam LocalDate date) {
        return service.isAvailableOnDate(id, date);
    }

    public static class AvailabilityRequest {
        private boolean available;

        public boolean isAvailable() {
            return available;
        }

        public void setAvailable(boolean available) {
            this.available = available;
        }
    }
}