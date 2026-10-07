package repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;

import entity.Room;

public interface RoomRepository extends JpaRepository<Room, Long> {

    // Find room by room number
    Room findByRoomNumber(String roomNumber);

    // Find rooms by type
    List<Room> findByRoomTypeIgnoreCase(String roomType);

    // Find available or occupied rooms
    List<Room> findByAvailable(Boolean available);

    // Check whether room number already exists
    boolean existsByRoomNumber(String roomNumber);
}