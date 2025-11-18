package com.bank.asset_management.repository;

import com.bank.asset_management.entity.InvalidatedToken;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.time.Instant;

public interface InvalidatedTokenRepository extends JpaRepository<InvalidatedToken, Long> {

    boolean existsByToken(String token);

    @Modifying
    @Query("DELETE FROM InvalidatedToken i WHERE i.expiryTime < :now")
    void deleteExpiredTokens(@Param("now") Instant now);
}
