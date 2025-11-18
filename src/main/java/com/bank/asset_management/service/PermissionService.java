package com.bank.asset_management.service;

import com.bank.asset_management.dto.request.PermissionRequest;
import com.bank.asset_management.dto.response.PermissionResponse;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

public interface PermissionService {
    PermissionResponse handleCreatePermission(PermissionRequest request);

    PermissionResponse handleUpdatePermission(Long id, PermissionRequest request);

    PermissionResponse handleGetPermissionById(Long id);

    Page<PermissionResponse> handleGetAllPermissions(Pageable pageable);

    void handleDeletePermission(Long id);
}

