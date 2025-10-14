package com.insurance.health.service;

import com.insurance.health.dto.LoginRequest;
import com.insurance.health.dto.RegisterRequest;
import com.insurance.health.model.AppUser;

public interface UserService {
    AppUser register(RegisterRequest request);
    String login(LoginRequest request);
}
