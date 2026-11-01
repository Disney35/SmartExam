
package com.smartexam.repository;

import com.smartexam.entity.RoomEquipment;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface RoomEquipmentRepository
        extends JpaRepository<RoomEquipment, String> {

    List<RoomEquipment> findByRoomRoomId(String roomId);
}
