package com.insurance.health.service.impl;

import com.insurance.health.dto.LoginRequest;
import com.insurance.health.dto.RegisterRequest;
import com.insurance.health.model.AppUser;
import com.insurance.health.repository.UserRepository;
import com.insurance.health.service.UserService;
import com.insurance.health.utils.JwtUtil;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.userdetails.UserDetails;
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
    public AppUser register(RegisterRequest request) {
        AppUser appUser = new AppUser();
        appUser.setId(UUID.randomUUID().toString());
        appUser.setName(request.getName());
        appUser.setEmail(request.getEmail());
        appUser.setGender(request.getGender());
        appUser.setRole(request.getRole().toUpperCase());
        appUser.setPassword(passwordEncoder.encode(request.getPassword()));

        return userRepository.save(appUser);
    }

    @Override
    public String login(LoginRequest request) {
        Authentication authentication = authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(
                        request.getEmail(), request.getPassword()
                )
        );

        UserDetails user = (UserDetails) authentication.getPrincipal();
        return jwtUtil.generateToken(user);
    }

    @Override
    public AppUser getUserById(String id, Authentication auth) {
    ///
        return null;
    }

    @Override
    public AppUser updateUser(String id, AppUser updatedUser, Authentication auth) {
    ///
        return null;
    }

    @Override
    public List<AppUser> getAllUsers() {
        return userRepository.findAll();
    }

    @Override
    public AppUser deleteUser(String id) {
        return userRepository.deleteById(id);
    }
}

