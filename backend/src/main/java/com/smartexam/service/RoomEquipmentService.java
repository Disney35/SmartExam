
package com.smartexam.service;

import com.smartexam.entity.Room;
import com.smartexam.entity.RoomEquipment;
import com.smartexam.repository.RoomEquipmentRepository;
import com.smartexam.repository.RoomRepository;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class RoomEquipmentService {

    private final RoomEquipmentRepository repository;
    private final RoomRepository roomRepository;

    public RoomEquipmentService(
            RoomEquipmentRepository repository,
            RoomRepository roomRepository) {
        this.repository = repository;
        this.roomRepository = roomRepository;
    }

    public RoomEquipment create(RoomEquipment equipment) {

        if (equipment.getEquipmentId() == null ||
                equipment.getEquipmentId().isBlank()) {
            throw new IllegalArgumentException("Equipment ID is required");
        }

        if (repository.existsById(equipment.getEquipmentId())) {
            throw new IllegalArgumentException("Equipment ID already exists");
        }

        if (equipment.getRoom() == null ||
                equipment.getRoom().getRoomId() == null) {
            throw new IllegalArgumentException("Room ID is required");
        }

        String roomId = equipment.getRoom().getRoomId();

        Room room = roomRepository.findById(roomId)
                .orElseThrow(() ->
                        new IllegalArgumentException("Room not found: " + roomId));

        if (equipment.getEquipmentName() == null ||
                equipment.getEquipmentName().isBlank()) {
            throw new IllegalArgumentException("Equipment name is required");
        }

        if (equipment.getQuantity() == null ||
                equipment.getQuantity() < 1) {
            throw new IllegalArgumentException(
                    "Equipment quantity must be at least 1");
        }

        equipment.setRoom(room);
        return repository.save(equipment);
    }

    public List<RoomEquipment> getAll() {
        return repository.findAll();
    }

    public List<RoomEquipment> getByRoom(String roomId) {

        if (!roomRepository.existsById(roomId)) {
            throw new IllegalArgumentException("Room not found: " + roomId);
        }

        return repository.findByRoomRoomId(roomId);
    }
}
