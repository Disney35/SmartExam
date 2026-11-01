
package com.smartexam.service;

import java.util.List;

import org.springframework.stereotype.Service;

import com.smartexam.entity.Room;
import com.smartexam.repository.RoomRepository;

@Service
public class RoomService {

    private final RoomRepository roomRepository;

    public RoomService(RoomRepository roomRepository) {
        this.roomRepository = roomRepository;
    }

    public Room createRoom(Room room) {

        if (room.getCapacity() == null || room.getCapacity() <= 0) {
            throw new IllegalArgumentException(
                "Room capacity must be greater than zero"
            );
        }

        if (roomRepository.existsById(room.getRoomId())) {
            throw new IllegalArgumentException(
                "Room ID already exists"
            );
        }

        return roomRepository.save(room);
    }

    public List<Room> getAllRooms() {
        return roomRepository.findAll();
    }

    public Room getRoomById(String roomId) {
        return roomRepository.findById(roomId)
            .orElseThrow(() ->
                new RuntimeException("Room not found: " + roomId)
            );
    }

    public Room updateRoom(String roomId, Room updatedRoom) {

        Room existingRoom = getRoomById(roomId);

        existingRoom.setRoomName(updatedRoom.getRoomName());
        existingRoom.setBuilding(updatedRoom.getBuilding());
        existingRoom.setCapacity(updatedRoom.getCapacity());
        existingRoom.setRoomType(updatedRoom.getRoomType());
        existingRoom.setAccessibilityAvailable(
            updatedRoom.getAccessibilityAvailable()
        );

        return roomRepository.save(existingRoom);
    }

    public void deleteRoom(String roomId) {
        Room room = getRoomById(roomId);
        roomRepository.delete(room);
    }
}
