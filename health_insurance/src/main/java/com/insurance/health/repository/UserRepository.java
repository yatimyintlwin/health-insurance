package com.insurance.health.repository;

import com.insurance.health.model.AppUser;

import java.util.List;
import java.util.Optional;

public interface UserRepository {
    AppUser save(AppUser appUser);
    Optional<AppUser> findByEmail(String email);
    Optional<AppUser> findById(String id);
    List<AppUser> findAll();
    AppUser deleteById(String id);

}
