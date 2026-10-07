package controller;

import java.util.List;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import entity.Room;
import service.RoomService;

@RestController
@RequestMapping("/api/rooms")
@CrossOrigin(origins = "http://localhost:5173")
public class RoomController {

    private final RoomService roomService;

    public RoomController(RoomService roomService) {
        this.roomService = roomService;
    }

    // Add Room
    @PostMapping
    public ResponseEntity<Room> addRoom(@RequestBody Room room) {

        Room savedRoom = roomService.addRoom(room);

        return new ResponseEntity<>(savedRoom, HttpStatus.CREATED);
    }

    // Get All Rooms
    @GetMapping
    public ResponseEntity<List<Room>> getAllRooms() {

        return ResponseEntity.ok(roomService.getAllRooms());
    }

    // Get Room By ID
    @GetMapping("/{id}")
    public ResponseEntity<Room> getRoomById(@PathVariable Long id) {

        return ResponseEntity.ok(roomService.getRoomById(id));
    }

    // Get Room By Number
    @GetMapping("/number/{roomNumber}")
    public ResponseEntity<Room> getRoomByNumber(
            @PathVariable String roomNumber) {

        return ResponseEntity.ok(
                roomService.getRoomByNumber(roomNumber));
    }

    // Get Rooms By Type
    @GetMapping("/type/{roomType}")
    public ResponseEntity<List<Room>> getRoomsByType(
            @PathVariable String roomType) {

        return ResponseEntity.ok(
                roomService.getRoomsByType(roomType));
    }

    // Get Available Rooms
    @GetMapping("/available")
    public ResponseEntity<List<Room>> getAvailableRooms() {

        return ResponseEntity.ok(
                roomService.getAvailableRooms());
    }

    // Get Occupied Rooms
    @GetMapping("/occupied")
    public ResponseEntity<List<Room>> getOccupiedRooms() {

        return ResponseEntity.ok(
                roomService.getOccupiedRooms());
    }

    // Update Room
    @PutMapping("/{id}")
    public ResponseEntity<Room> updateRoom(
            @PathVariable Long id,
            @RequestBody Room room) {

        return ResponseEntity.ok(
                roomService.updateRoom(id, room));
    }

    // Update Room Availability
    @PatchMapping("/{id}/availability")
    public ResponseEntity<Room> updateRoomAvailability(
            @PathVariable Long id,
            @RequestParam Boolean available) {

        return ResponseEntity.ok(
                roomService.updateRoomAvailability(id, available));
    }

    // Delete Room
    @DeleteMapping("/{id}")
    public ResponseEntity<String> deleteRoom(@PathVariable Long id) {

        roomService.deleteRoom(id);

        return ResponseEntity.ok("Room deleted successfully");
    }
}