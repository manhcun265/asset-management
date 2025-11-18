package com.bank.asset_management.repository;

import com.bank.asset_management.entity.AssetType;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface AssetTypeRepository extends JpaRepository<AssetType, Long> {
    Optional<AssetType> findByNameIgnoreCase(String name);
}
