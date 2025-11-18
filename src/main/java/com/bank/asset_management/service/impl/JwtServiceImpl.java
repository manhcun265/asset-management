package com.bank.asset_management.service.impl;

import com.bank.asset_management.entity.InvalidatedToken;
import com.bank.asset_management.repository.InvalidatedTokenRepository;
import com.bank.asset_management.service.JwtService;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.security.Keys;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Service;

import javax.crypto.SecretKey;
import java.nio.charset.StandardCharsets;
import java.time.Instant;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.function.Function;

@Service
@RequiredArgsConstructor
@Slf4j
public class JwtServiceImpl implements JwtService {

    private final InvalidatedTokenRepository invalidatedTokenRepository;

    @Value("${jwt.secretKey}")
    private String secretKey;

    @Value("${jwt.expiration}")
    private long jwtExpiration;

    private SecretKey getSigningKey() {
        byte[] keyBytes = secretKey.getBytes(StandardCharsets.UTF_8);
        return Keys.hmacShaKeyFor(keyBytes);
    }

    @Override
    public String generateToken(UserDetails userDetails) {
        return generateToken(new HashMap<>(), userDetails);
    }

    public String generateToken(Map<String, Object> extraClaims, UserDetails userDetails) {
        long now = System.currentTimeMillis();
        String token = Jwts.builder()
                .claims(extraClaims)
                .subject(userDetails.getUsername())
                .issuedAt(new Date(now))
                .expiration(new Date(now + jwtExpiration))
                .signWith(getSigningKey())
                .compact();

        log.debug("[JWT] Generated token for user: {}", userDetails.getUsername());
        return token;
    }

    @Override
    public void invalidateToken(String token) {
        try {
            Date exp = extractExpiration(token);
            InvalidatedToken entity = InvalidatedToken.builder()
                    .token(token)
                    .expiryTime(exp != null ? exp.toInstant() : null)
                    .build();

            if (!invalidatedTokenRepository.existsByToken(token)) {
                invalidatedTokenRepository.save(entity);
                log.info("[JWT] Token invalidated");
            }
        } catch (Exception e) {
            log.warn("[JWT] Error invalidating token: {}", e.getMessage());
            if (!invalidatedTokenRepository.existsByToken(token)) {
                invalidatedTokenRepository.save(InvalidatedToken.builder().token(token).build());
            }
        }
    }

    public boolean isTokenInvalidated(String token) {
        return invalidatedTokenRepository.existsByToken(token);
    }

    public String extractUsername(String token) {
        return extractClaim(token, Claims::getSubject);
    }

    public Date extractExpiration(String token) {
        return extractClaim(token, Claims::getExpiration);
    }

    public boolean isTokenExpired(String token) {
        try {
            return extractExpiration(token).before(new Date());
        } catch (Exception e) {
            log.warn("[JWT] Token validation failed: {}", e.getMessage());
            return true;
        }
    }

    private <T> T extractClaim(String token, Function<Claims, T> claimsResolver) {
        final Claims claims = extractAllClaims(token);
        return claimsResolver.apply(claims);
    }

    private Claims extractAllClaims(String token) {
        return Jwts.parser()
                .verifyWith(getSigningKey())
                .build()
                .parseSignedClaims(token)
                .getPayload();
    }
}

