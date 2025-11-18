package com.bank.asset_management.service;

import com.bank.asset_management.dto.request.RoleRequest;
import com.bank.asset_management.dto.response.RoleResponse;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

public interface RoleService {
    RoleResponse handleCreateRole(RoleRequest request);

    RoleResponse handleUpdateRole(Long id, RoleRequest request);

    RoleResponse handleGetRoleById(Long id);

    Page<RoleResponse> handleGetAllRoles(Pageable pageable);

    void handleDeleteRole(Long id);
}

