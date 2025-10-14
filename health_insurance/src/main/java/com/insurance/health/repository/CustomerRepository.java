package com.insurance.health.repository;

import com.insurance.health.model.AppUser;

import java.util.Optional;

public interface CustomerRepository {
    AppUser save(AppUser appUser);

    Optional<AppUser> findByEmail(String email);
}
