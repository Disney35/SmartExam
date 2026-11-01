
package com.smartexam.repository;

import com.smartexam.entity.RoomAvailability;
import org.springframework.data.jpa.repository.JpaRepository;

import java.time.LocalDate;
import java.util.List;

public interface RoomAvailabilityRepository
        extends JpaRepository<RoomAvailability, String> {

    List<RoomAvailability> findByRoomRoomIdAndAvailabilityDate(
            String roomId, LocalDate date);

    List<RoomAvailability> findByAvailabilityDate(LocalDate date);
}
