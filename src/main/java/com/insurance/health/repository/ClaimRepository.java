package com.insurance.health.repository;

import com.insurance.health.model.Claim;

public interface ClaimRepository {
    Claim save(Claim claim);
}
