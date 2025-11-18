package com.bank.asset_management.service;

import org.springframework.security.core.userdetails.UserDetails;

public interface JwtService {
    String generateToken(UserDetails userDetails);

    void invalidateToken(String token);
}

