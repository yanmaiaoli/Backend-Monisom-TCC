package com.monisom.repository;

import com.monisom.entity.Room;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.UUID;

public interface RoomRepository extends JpaRepository<Room, UUID> {

    List<Room> findBySchoolIdOrderByNameAsc(UUID schoolId);
}
