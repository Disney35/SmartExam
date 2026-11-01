
package com.smartexam.entity;

import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import jakarta.persistence.Column;

@Entity
@Table(name = "rooms")
public class Room {

    @Id
    @Column(name = "room_id")
    private String roomId;

    private String roomName;

    private String building;

    private Integer capacity;

    private String roomType;

    private Boolean accessibilityAvailable;

    public Room() {
    }

    public Room(String roomId, String roomName, String building,
                Integer capacity, String roomType,
                Boolean accessibilityAvailable) {
        this.roomId = roomId;
        this.roomName = roomName;
        this.building = building;
        this.capacity = capacity;
        this.roomType = roomType;
        this.accessibilityAvailable = accessibilityAvailable;
    }

    public String getRoomId() {
        return roomId;
    }

    public void setRoomId(String roomId) {
        this.roomId = roomId;
    }

    public String getRoomName() {
        return roomName;
    }

    public void setRoomName(String roomName) {
        this.roomName = roomName;
    }

    public String getBuilding() {
        return building;
    }

    public void setBuilding(String building) {
        this.building = building;
    }

    public Integer getCapacity() {
        return capacity;
    }

    public void setCapacity(Integer capacity) {
        this.capacity = capacity;
    }

    public String getRoomType() {
        return roomType;
    }

    public void setRoomType(String roomType) {
        this.roomType = roomType;
    }

    public Boolean getAccessibilityAvailable() {
        return accessibilityAvailable;
    }

    public void setAccessibilityAvailable(Boolean accessibilityAvailable) {
        this.accessibilityAvailable = accessibilityAvailable;
    }
}
