package controller;

import java.util.List;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import dto.AdminUserRequest;
import dto.RegisterRequest;
import dto.UserResponse;
import entity.User;
import entity.UserRole;
import jakarta.validation.Valid;
import service.UserService;

@RestController
@RequestMapping("/api/users")
@CrossOrigin(origins = "http://localhost:5173")
public class UserController {

    private final UserService userService;

    public UserController(UserService userService) {
        this.userService = userService;
    }

    // =========================================================
    // CONVERT USER ENTITY TO RESPONSE
    // =========================================================

    private UserResponse convertToResponse(User user) {

        return new UserResponse(
                user.getId(),
                user.getUsername(),
                user.getRole(),
                user.isEnabled()
        );
    }

    // =========================================================
    // PUBLIC PATIENT REGISTRATION
    // =========================================================

    @PostMapping("/register")
    public ResponseEntity<UserResponse> registerUser(
            @Valid @RequestBody RegisterRequest request) {

        User user = new User();

        user.setUsername(
                request.getUsername().trim()
        );

        user.setPassword(
                request.getPassword()
        );

        // Public registration always creates PATIENT
        user.setRole(
                UserRole.PATIENT.name()
        );

        user.setEnabled(true);

        User savedUser =
                userService.registerUser(user);

        return new ResponseEntity<>(
                convertToResponse(savedUser),
                HttpStatus.CREATED
        );
    }

    // =========================================================
    // GET ALL USERS
    // ADMIN ONLY THROUGH SecurityConfig
    // =========================================================

    @GetMapping
    public ResponseEntity<List<UserResponse>> getAllUsers() {

        List<UserResponse> users =
                userService.getAllUsers()
                        .stream()
                        .map(this::convertToResponse)
                        .toList();

        return ResponseEntity.ok(users);
    }

    // =========================================================
    // GET USER BY ID
    // =========================================================

    @GetMapping("/{id}")
    public ResponseEntity<UserResponse> getUserById(
            @PathVariable Long id) {

        User user =
                userService.getUserById(id);

        return ResponseEntity.ok(
                convertToResponse(user)
        );
    }

    // =========================================================
    // ADMIN CREATE USER
    // =========================================================

    @PostMapping("/admin")
    public ResponseEntity<UserResponse> createUserByAdmin(
            @Valid @RequestBody AdminUserRequest request) {

        User user = new User();

        user.setUsername(
                request.getUsername().trim()
        );

        user.setPassword(
                request.getPassword()
        );

        user.setRole(
                request.getRole().name()
        );

        user.setEnabled(
                request.getEnabled() == null
                        ? true
                        : request.getEnabled()
        );

        User savedUser =
                userService.registerUser(user);

        return new ResponseEntity<>(
                convertToResponse(savedUser),
                HttpStatus.CREATED
        );
    }

    // =========================================================
    // UPDATE USER
    // =========================================================

    @PutMapping("/{id}")
    public ResponseEntity<UserResponse> updateUser(
            @PathVariable Long id,
            @Valid @RequestBody AdminUserRequest request) {

        User updatedUser =
                userService.updateUser(
                        id,
                        request.getUsername().trim(),
                        request.getPassword(),
                        request.getRole() != null
                                ? request.getRole().name()
                                : null,
                        request.getEnabled()
                );

        return ResponseEntity.ok(
                convertToResponse(updatedUser)
        );
    }

    // =========================================================
    // ENABLE / DISABLE USER
    // =========================================================

    @PatchMapping("/{id}/status")
    public ResponseEntity<UserResponse> updateUserStatus(
            @PathVariable Long id,
            @RequestParam boolean enabled) {

        User updatedUser =
                userService.updateUserStatus(
                        id,
                        enabled
                );

        return ResponseEntity.ok(
                convertToResponse(updatedUser)
        );
    }

    // =========================================================
    // DELETE USER
    // =========================================================

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteUser(
            @PathVariable Long id) {

        userService.deleteUser(id);

        return ResponseEntity.noContent().build();
    }
}