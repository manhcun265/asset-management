package com.bank.asset_management.service;

import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Page;
import com.bank.asset_management.dto.response.AssetTypeResponse;
import com.bank.asset_management.dto.request.AssetTypeRequest;

public interface AssetTypeService {


    void handleDeleteAssetType(Long id);

    Page<AssetTypeResponse> handleGetAllAssetTypes(Pageable pageable);

    AssetTypeResponse handleGetAssetTypeById(Long id);

    AssetTypeResponse handleUpdateAssetType(Long id, AssetTypeRequest request);

    AssetTypeResponse handleCreateAssetType(AssetTypeRequest request);
}


