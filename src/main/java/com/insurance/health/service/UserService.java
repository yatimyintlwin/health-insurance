package com.insurance.health.service;

import com.insurance.health.dto.LoginRequest;
import com.insurance.health.dto.RegisterRequest;
import com.insurance.health.dto.RegisterResponse;
import com.insurance.health.model.AppUser;

import java.util.List;

public interface UserService {
    RegisterResponse register(RegisterRequest request);
    String login(LoginRequest request);

    AppUser getUserById(String id);

    List<AppUser> getAllUsers();

    AppUser updateUser(String id, AppUser updatedUser);

    void deleteUser(String id);
}
