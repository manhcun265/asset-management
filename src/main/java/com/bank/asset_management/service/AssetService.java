package com.bank.asset_management.service;

import com.bank.asset_management.dto.request.AssetRequest;
import com.bank.asset_management.dto.response.AssetResponse;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

public interface AssetService {
    AssetResponse handleCreateAsset(AssetRequest request);

    AssetResponse handleUpdateAsset(Long id, AssetRequest request);

    AssetResponse handleGetAssetById(Long id);

    Page<AssetResponse> handleGetAllAssets(Pageable pageable);

    void handleDeleteAsset(Long id);
}

