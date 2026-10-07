package service;

import java.util.List;

import org.springframework.stereotype.Service;

import entity.Room;
import repository.RoomRepository;
import exception.ResourceNotFoundException;

@Service
public class RoomService {

    private final RoomRepository roomRepository;

    public RoomService(RoomRepository roomRepository) {
        this.roomRepository = roomRepository;
    }

    // Add Room
    public Room addRoom(Room room) {

        if (roomRepository.existsByRoomNumber(room.getRoomNumber())) {
            throw new RuntimeException("Room number already exists");
        }

        room.setAvailable(true);

        return roomRepository.save(room);
    }

    // Get All Rooms
    public List<Room> getAllRooms() {
        return roomRepository.findAll();
    }

    // Get Room By ID
    public Room getRoomById(Long id) {
        return roomRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException(
                        "Room not found with ID: " + id
                ));
    }

    // Get Room By Number
    public Room getRoomByNumber(String roomNumber) {

        Room room = roomRepository.findByRoomNumber(roomNumber);

        if (room == null) {
        	throw new ResourceNotFoundException(
        	        "Room not found with ID: " + roomNumber
        	);
        }

        return room;
    }

    // Get Rooms By Type
    public List<Room> getRoomsByType(String roomType) {
        return roomRepository.findByRoomTypeIgnoreCase(roomType);
    }

    // Get Available Rooms
    public List<Room> getAvailableRooms() {
        return roomRepository.findByAvailable(true);
    }

    // Get Occupied Rooms
    public List<Room> getOccupiedRooms() {
        return roomRepository.findByAvailable(false);
    }

    // Update Room
    public Room updateRoom(Long id, Room updatedRoom) {

        Room existingRoom = getRoomById(id);

        if (!existingRoom.getRoomNumber().equals(updatedRoom.getRoomNumber())
                && roomRepository.existsByRoomNumber(updatedRoom.getRoomNumber())) {

            throw new RuntimeException("Room number already exists");
        }

        existingRoom.setRoomNumber(updatedRoom.getRoomNumber());
        existingRoom.setRoomType(updatedRoom.getRoomType());
        existingRoom.setDailyCharge(updatedRoom.getDailyCharge());
        existingRoom.setDescription(updatedRoom.getDescription());

        return roomRepository.save(existingRoom);
    }

    // Update Room Availability
    public Room updateRoomAvailability(Long id, Boolean available) {

        Room room = getRoomById(id);

        room.setAvailable(available);

        return roomRepository.save(room);
    }

    // Delete Room
    public void deleteRoom(Long id) {

        Room room = getRoomById(id);

        roomRepository.delete(room);
    }
}