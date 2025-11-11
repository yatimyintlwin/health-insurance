package com.insurance.health.controller;

import com.insurance.health.model.AppUser;
import com.insurance.health.service.UserService;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/users")
public class UserController {

    private final UserService userService;

    public UserController(UserService userService) {
        this.userService = userService;
    }

    @GetMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN') or (hasRole('USER') and #id == authentication.name)")
    public ResponseEntity<AppUser> getUserById(@PathVariable String id) {
        AppUser user = userService.getUserById(id);
        return ResponseEntity.ok(user);
    }

    @GetMapping
    public ResponseEntity<List<AppUser>> listAllUsers() {
        List<AppUser> users = userService.getAllUsers();
        return ResponseEntity.ok(users);
    }

    @PutMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN') or #id == authentication.name")
    public ResponseEntity<AppUser> updateUser(
            @PathVariable String id,
            @Valid @RequestBody AppUser updatedUser
    ) {
        AppUser user = userService.updateUser(id, updatedUser);
        return ResponseEntity.ok(user);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<String> deleteUser(@PathVariable String id) {
        userService.deleteUser(id);
        return ResponseEntity.ok("Successfully deleted user");
    }
}
