package com.bank.asset_management.service.impl;

import com.bank.asset_management.dto.request.PermissionRequest;
import com.bank.asset_management.dto.response.PermissionResponse;
import com.bank.asset_management.entity.Permission;
import com.bank.asset_management.repository.PermissionRepository;
import com.bank.asset_management.service.PermissionService;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
@Transactional
public class PermissionServiceImpl implements PermissionService {

    PermissionRepository permissionRepository;

    @Override
    public PermissionResponse handleCreatePermission(PermissionRequest request) {
        // Kiểm tra tên quyền trùng
        permissionRepository.findByName(request.getName())
                .ifPresent(existing -> {
                    throw new IllegalArgumentException("Tên quyền đã tồn tại: " + request.getName());
                });

        Permission newPermission = new Permission();
        newPermission.setName(request.getName());
        newPermission.setDescription(request.getDescription());
        Permission savedPermission = permissionRepository.save(newPermission);
        return toResponse(savedPermission);
    }

    @Override
    public PermissionResponse handleUpdatePermission(Long id, PermissionRequest request) {
        Permission currentPermission = permissionRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Không tìm thấy quyền với ID: " + id));

        // Kiểm tra tên quyền trùng
        Permission duplicatePermission = permissionRepository.findByName(request.getName()).orElse(null);
        if (duplicatePermission != null && !duplicatePermission.getId().equals(id)) {
            throw new IllegalArgumentException("Tên quyền đã tồn tại: " + request.getName());
        }

        currentPermission.setName(request.getName());
        currentPermission.setDescription(request.getDescription());
        Permission savedPermission = permissionRepository.save(currentPermission);
        return toResponse(savedPermission);
    }

    @Override
    @Transactional(readOnly = true)
    public PermissionResponse handleGetPermissionById(Long id) {
        Permission permission = permissionRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Không tìm thấy quyền với ID: " + id));
        return toResponse(permission);
    }

    @Override
    @Transactional(readOnly = true)
    public Page<PermissionResponse> handleGetAllPermissions(Pageable pageable) {
        return permissionRepository.findAll(pageable).map(this::toResponse);
    }

    @Override
    public void handleDeletePermission(Long id) {
        if (!permissionRepository.existsById(id)) {
            throw new IllegalArgumentException("Không tìm thấy quyền với ID: " + id);
        }
        permissionRepository.deleteById(id);
    }

    private PermissionResponse toResponse(Permission permission) {
        return PermissionResponse.builder()
                .id(permission.getId())
                .name(permission.getName())
                .description(permission.getDescription())
                .createdAt(permission.getCreatedAt())
                .updatedAt(permission.getUpdatedAt())
                .build();
    }
}
