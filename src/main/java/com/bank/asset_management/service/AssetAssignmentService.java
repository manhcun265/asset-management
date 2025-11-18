package com.bank.asset_management.service;

import com.bank.asset_management.dto.request.AssetAssignmentRequest;
import com.bank.asset_management.dto.response.AssetAssignmentResponse;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

public interface AssetAssignmentService {
    AssetAssignmentResponse handleCreateAssetAssignment(AssetAssignmentRequest request);

    AssetAssignmentResponse handleUpdateAssetAssignment(Long id, AssetAssignmentRequest request);

    AssetAssignmentResponse handleGetAssetAssignmentById(Long id);

    Page<AssetAssignmentResponse> handleGetAllAssetAssignments(Pageable pageable);

    void handleDeleteAssetAssignment(Long id);
}

