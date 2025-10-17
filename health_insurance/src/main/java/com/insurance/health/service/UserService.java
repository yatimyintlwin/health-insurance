package com.insurance.health.service;

import com.insurance.health.dto.LoginRequest;
import com.insurance.health.dto.RegisterRequest;
import com.insurance.health.dto.RegisterResponse;
import com.insurance.health.model.AppUser;
import org.springframework.security.core.Authentication;

import java.util.List;

public interface UserService {
    RegisterResponse register(RegisterRequest request);
    String login(LoginRequest request);

    AppUser getUserById(String id, Authentication auth);

    AppUser updateUser(String id, AppUser updatedUser, Authentication auth);

    List<AppUser> getAllUsers();

    AppUser deleteUser(String id);
}
