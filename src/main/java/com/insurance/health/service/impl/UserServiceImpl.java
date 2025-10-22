package com.insurance.health.service.impl;

import com.insurance.health.dto.LoginRequest;
import com.insurance.health.dto.RegisterRequest;
import com.insurance.health.dto.RegisterResponse;
import com.insurance.health.model.AppUser;
import com.insurance.health.repository.UserRepository;
import com.insurance.health.service.UserService;
import com.insurance.health.utils.JwtUtil;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.UUID;

@Service
public class UserServiceImpl implements UserService {

    private final UserRepository userRepository;
    private final JwtUtil jwtUtil;
    private final BCryptPasswordEncoder passwordEncoder;
    private final AuthenticationManager authenticationManager;

    public UserServiceImpl(UserRepository userRepository,
                           JwtUtil jwtUtil,
                           BCryptPasswordEncoder passwordEncoder,
                           AuthenticationManager authenticationManager) {
        this.userRepository = userRepository;
        this.jwtUtil = jwtUtil;
        this.passwordEncoder = passwordEncoder;
        this.authenticationManager = authenticationManager;
    }

    @Override
    public RegisterResponse register(RegisterRequest request) {
        AppUser appUser = new AppUser();
        appUser.setId(UUID.randomUUID().toString());
        appUser.setName(request.getName());
        appUser.setEmail(request.getEmail());
        appUser.setGender(request.getGender());
        appUser.setRole(request.getRole().toUpperCase());
        appUser.setPassword(passwordEncoder.encode(request.getPassword()));
        AppUser savedAppUser = userRepository.save(appUser);

        RegisterResponse response = new RegisterResponse();
        response.setId(savedAppUser.getId());
        response.setEmail(savedAppUser.getEmail());
        response.setName(savedAppUser.getName());
        response.setGender(savedAppUser.getGender());
        return response;
    }

    @Override
    public String login(LoginRequest request) {
        Authentication authentication = authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(
                        request.getEmail(), request.getPassword()
                )
        );

        AppUser appUser = userRepository.findByEmail(request.getEmail())
                .orElseThrow(() -> new RuntimeException("User not found"));

        return jwtUtil.generateToken(appUser);
    }

    @Override
    public AppUser getUserById(String id) {
        return userRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("User not found"));
    }

    @Override
    public List<AppUser> getAllUsers() {
        return userRepository.findAll();
    }

    @Override
    public AppUser updateUser(String id, AppUser updatedUser) {
        AppUser existing = userRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("User not found"));
        existing.setName(updatedUser.getName());
        existing.setGender(updatedUser.getGender());
        existing.setPassword(updatedUser.getPassword());
        if (updatedUser.getPassword() != null && !updatedUser.getPassword().isEmpty()) {
            existing.setPassword(passwordEncoder.encode(updatedUser.getPassword()));
        }
        return userRepository.update(existing);
    }

    @Override
    public void deleteUser(String id) {
        AppUser existingUser = userRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("User not found"));

        userRepository.deleteById(existingUser.getId(), existingUser.getEmail());
    }
}
