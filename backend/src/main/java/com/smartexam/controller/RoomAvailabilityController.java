
package com.smartexam.controller;

import com.smartexam.entity.RoomAvailability;
import com.smartexam.service.RoomAvailabilityService;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.util.List;

@RestController
@RequestMapping("/api/room-availability")
public class RoomAvailabilityController {

    private final RoomAvailabilityService service;

    public RoomAvailabilityController(RoomAvailabilityService service) {
        this.service = service;
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public RoomAvailability create(
            @RequestBody RoomAvailability availability) {
        return service.create(availability);
    }

    @GetMapping
    public List<RoomAvailability> getAll() {
        return service.getAll();
    }

    @GetMapping("/{id}")
    public RoomAvailability getById(@PathVariable String id) {
        return service.getById(id);
    }

    @GetMapping("/room/{roomId}")
    public List<RoomAvailability> getByRoomAndDate(
            @PathVariable String roomId,
            @RequestParam LocalDate date) {
        return service.getByRoomAndDate(roomId, date);
    }

    @GetMapping("/date")
    public List<RoomAvailability> getByDate(
            @RequestParam LocalDate date) {
        return service.getByDate(date);
    }
}
