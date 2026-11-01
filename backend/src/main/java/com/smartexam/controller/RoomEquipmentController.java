
package com.smartexam.controller;

import com.smartexam.entity.RoomEquipment;
import com.smartexam.service.RoomEquipmentService;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/room-equipment")
public class RoomEquipmentController {

    private final RoomEquipmentService service;

    public RoomEquipmentController(RoomEquipmentService service) {
        this.service = service;
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public RoomEquipment create(@RequestBody RoomEquipment equipment) {
        return service.create(equipment);
    }

    @GetMapping
    public List<RoomEquipment> getAll() {
        return service.getAll();
    }

    @GetMapping("/room/{roomId}")
    public List<RoomEquipment> getByRoom(@PathVariable String roomId) {
        return service.getByRoom(roomId);
    }
}
