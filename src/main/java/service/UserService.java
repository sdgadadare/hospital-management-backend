package service;

import java.util.List;

import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import entity.User;
import exception.ResourceNotFoundException;
import repository.UserRepository;

@Service
public class UserService {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;

    public UserService(
            UserRepository userRepository,
            PasswordEncoder passwordEncoder) {

        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
    }

    // =========================================================
    // REGISTER / CREATE USER
    // =========================================================

    public User registerUser(User user) {

        if (userRepository.existsByUsername(user.getUsername())) {
            throw new RuntimeException("Username already exists");
        }

        if (user.getPassword() == null
                || user.getPassword().trim().isEmpty()) {

            throw new RuntimeException("Password is required");
        }

        user.setPassword(
                passwordEncoder.encode(user.getPassword())
        );

        return userRepository.save(user);
    }

    // =========================================================
    // GET ALL USERS
    // =========================================================

    public List<User> getAllUsers() {

        return userRepository.findAll();
    }

    // =========================================================
    // GET USER BY ID
    // =========================================================

    public User getUserById(Long id) {

        return userRepository.findById(id)
                .orElseThrow(() ->
                        new ResourceNotFoundException(
                                "User not found with ID: " + id
                        )
                );
    }

    // =========================================================
    // GET USER BY USERNAME
    // =========================================================

    public User getUserByUsername(String username) {

        return userRepository.findByUsername(username)
                .orElseThrow(() ->
                        new ResourceNotFoundException(
                                "User not found with username: " + username
                        )
                );
    }

    // =========================================================
    // UPDATE USER
    // =========================================================

    public User updateUser(
            Long id,
            String username,
            String password,
            String role,
            Boolean enabled) {

        User user = getUserById(id);

        // Check username uniqueness
        if (!user.getUsername().equals(username)
                && userRepository.existsByUsername(username)) {

            throw new RuntimeException("Username already exists");
        }

        user.setUsername(username);

        // Password is optional during edit.
        // Only encode when a new password is supplied.
        if (password != null
                && !password.trim().isEmpty()) {

            user.setPassword(
                    passwordEncoder.encode(password)
            );
        }

        if (role != null
                && !role.trim().isEmpty()) {

            user.setRole(role);
        }

        if (enabled != null) {
            user.setEnabled(enabled);
        }

        return userRepository.save(user);
    }

    // =========================================================
    // ENABLE / DISABLE USER
    // =========================================================

    public User updateUserStatus(
            Long id,
            boolean enabled) {

        User user = getUserById(id);

        user.setEnabled(enabled);

        return userRepository.save(user);
    }

    // =========================================================
    // DELETE USER
    // =========================================================

    public void deleteUser(Long id) {

        User user = getUserById(id);

        userRepository.delete(user);
    }
}