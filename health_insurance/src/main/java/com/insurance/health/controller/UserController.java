package com.insurance.health.controller;

import com.insurance.health.model.AppUser;
import com.insurance.health.service.UserService;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
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
    public ResponseEntity<AppUser> getUserById(@PathVariable String id, Authentication auth) {
        AppUser user = userService.getUserById(id, auth);
        return ResponseEntity.ok(user);
    }

    @GetMapping
    public ResponseEntity<List<AppUser>> listAllUsers() {
        List<AppUser> users = userService.getAllUsers();
        return ResponseEntity.ok(users);
    }

    @PutMapping("/{id}")
    public ResponseEntity<AppUser> updateUser(@PathVariable String id,
                                              @RequestBody AppUser updatedUser,
                                              Authentication auth) {
        AppUser user = userService.updateUser(id, updatedUser, auth);
        return ResponseEntity.ok(user);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteUser(@PathVariable String id) {
        userService.deleteUser(id);
        return ResponseEntity.noContent().build();
    }
}
