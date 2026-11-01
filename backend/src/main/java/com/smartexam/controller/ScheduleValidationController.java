
package com.smartexam.controller;

import com.smartexam.service.ScheduleValidationService;

import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@RequestMapping("/api/validation")
public class ScheduleValidationController {

    private final ScheduleValidationService validationService;

    public ScheduleValidationController(
            ScheduleValidationService validationService) {
        this.validationService = validationService;
    }

    @GetMapping("/schedule")
    public Map<String, Object> validateSchedule() {
        return validationService.validateSchedule();
    }
}
