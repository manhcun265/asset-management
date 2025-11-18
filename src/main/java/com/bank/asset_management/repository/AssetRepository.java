package com.bank.asset_management.repository;

import com.bank.asset_management.entity.Asset;
import org.springframework.data.jpa.repository.JpaRepository;

public interface AssetRepository extends JpaRepository<Asset, Long> {
    boolean existsByAssetCode(String assetCode);
}
