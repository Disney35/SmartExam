
package com.smartexam.service;

import com.smartexam.entity.Room;
import com.smartexam.entity.RoomAvailability;
import com.smartexam.repository.RoomAvailabilityRepository;
import com.smartexam.repository.RoomRepository;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.List;

@Service
public class RoomAvailabilityService {

    private final RoomAvailabilityRepository repository;
    private final RoomRepository roomRepository;

    public RoomAvailabilityService(
            RoomAvailabilityRepository repository,
            RoomRepository roomRepository) {
        this.repository = repository;
        this.roomRepository = roomRepository;
    }

    public RoomAvailability create(RoomAvailability availability) {

        if (availability.getAvailabilityId() == null ||
                availability.getAvailabilityId().isBlank()) {
            throw new IllegalArgumentException("Availability ID is required");
        }

        if (repository.existsById(availability.getAvailabilityId())) {
            throw new IllegalArgumentException("Availability ID already exists");
        }

        if (availability.getRoom() == null ||
                availability.getRoom().getRoomId() == null) {
            throw new IllegalArgumentException("Room ID is required");
        }

        String roomId = availability.getRoom().getRoomId();

        Room room = roomRepository.findById(roomId)
                .orElseThrow(() ->
                        new IllegalArgumentException("Room not found: " + roomId));

        if (availability.getAvailabilityDate() == null ||
                availability.getStartTime() == null ||
                availability.getEndTime() == null) {
            throw new IllegalArgumentException(
                    "Date, start time and end time are required");
        }

        if (!availability.getStartTime().isBefore(
                availability.getEndTime())) {
            throw new IllegalArgumentException(
                    "End time must be after start time");
        }

        List<RoomAvailability> existing =
                repository.findByRoomRoomIdAndAvailabilityDate(
                        roomId, availability.getAvailabilityDate());

        for (RoomAvailability record : existing) {
            boolean overlaps =
                    availability.getStartTime().isBefore(record.getEndTime())
                    && availability.getEndTime().isAfter(record.getStartTime());

            if (overlaps) {
                throw new IllegalArgumentException(
                        "Overlapping availability record exists for this room and date");
            }
        }

        availability.setRoom(room);

        return repository.save(availability);
    }

    public List<RoomAvailability> getAll() {
        return repository.findAll();
    }

    public RoomAvailability getById(String id) {
        return repository.findById(id)
                .orElseThrow(() ->
                        new IllegalArgumentException(
                                "Room availability not found: " + id));
    }

    public List<RoomAvailability> getByRoomAndDate(
            String roomId, LocalDate date) {

        if (!roomRepository.existsById(roomId)) {
            throw new IllegalArgumentException("Room not found: " + roomId);
        }

        return repository.findByRoomRoomIdAndAvailabilityDate(roomId, date);
    }

    public List<RoomAvailability> getByDate(LocalDate date) {
        return repository.findByAvailabilityDate(date);
    }
}
