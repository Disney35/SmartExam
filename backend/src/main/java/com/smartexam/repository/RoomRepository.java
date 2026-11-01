
package com.smartexam.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import com.smartexam.entity.Room;

public interface RoomRepository extends JpaRepository<Room, String> {

}
