
package com.smartexam.controller;

import com.smartexam.entity.InvigilatorAvailability;
import com.smartexam.service.InvigilatorAvailabilityService;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.util.List;

@RestController
@RequestMapping("/api/invigilator-availability")
public class InvigilatorAvailabilityController {

    private final InvigilatorAvailabilityService service;

    public InvigilatorAvailabilityController(
            InvigilatorAvailabilityService service) {
        this.service = service;
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public InvigilatorAvailability create(
            @RequestBody InvigilatorAvailability availability) {
        return service.create(availability);
    }

    @GetMapping
    public List<InvigilatorAvailability> getAll() {
        return service.getAll();
    }

    @GetMapping("/{id}")
    public InvigilatorAvailability getById(
            @PathVariable String id) {
        return service.getById(id);
    }

    @GetMapping("/invigilator/{invigilatorId}")
    public List<InvigilatorAvailability> getByInvigilatorAndDate(
            @PathVariable String invigilatorId,
            @RequestParam LocalDate date) {
        return service.getByInvigilatorAndDate(invigilatorId, date);
    }

    @GetMapping("/date")
    public List<InvigilatorAvailability> getByDate(
            @RequestParam LocalDate date) {
        return service.getByDate(date);
    }
}
