package com.bank.asset_management.service.impl;

import com.bank.asset_management.dto.request.RoleRequest;
import com.bank.asset_management.dto.response.RoleResponse;
import com.bank.asset_management.entity.Permission;
import com.bank.asset_management.entity.Role;
import com.bank.asset_management.repository.PermissionRepository;
import com.bank.asset_management.repository.RoleRepository;
import com.bank.asset_management.service.RoleService;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Collections;
import java.util.HashSet;
import java.util.Set;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
@Transactional
public class RoleServiceImpl implements RoleService {

    RoleRepository roleRepository;
    PermissionRepository permissionRepository;

    @Override
    public RoleResponse handleCreateRole(RoleRequest request) {
        Role newRole = new Role();
        newRole.setName(request.getName());
        newRole.setDescription(request.getDescription());

        // Xử lý permissions
        if (request.getPermissionIds() != null && !request.getPermissionIds().isEmpty()) {
            Set<Permission> permissions = resolvePermissions(request.getPermissionIds());
            newRole.setPermissions(permissions);
        }

        Role savedRole = roleRepository.save(newRole);
        return toResponse(savedRole);
    }

    @Override
    public RoleResponse handleUpdateRole(Long id, RoleRequest request) {
        Role currentRole = roleRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Không tìm thấy vai trò với ID: " + id));

        currentRole.setName(request.getName());
        currentRole.setDescription(request.getDescription());

        // Xử lý permissions
        if (request.getPermissionIds() != null) {
            if (request.getPermissionIds().isEmpty()) {
                currentRole.setPermissions(new HashSet<>());
            } else {
                Set<Permission> permissions = resolvePermissions(request.getPermissionIds());
                currentRole.setPermissions(permissions);
            }
        }

        Role savedRole = roleRepository.save(currentRole);
        return toResponse(savedRole);
    }

    @Override
    @Transactional(readOnly = true)
    public RoleResponse handleGetRoleById(Long id) {
        Role role = roleRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Không tìm thấy vai trò với ID: " + id));
        return toResponse(role);
    }

    @Override
    @Transactional(readOnly = true)
    public Page<RoleResponse> handleGetAllRoles(Pageable pageable) {
        return roleRepository.findAll(pageable).map(this::toResponse);
    }

    @Override
    public void handleDeleteRole(Long id) {
        if (!roleRepository.existsById(id)) {
            throw new IllegalArgumentException("Không tìm thấy vai trò với ID: " + id);
        }
        roleRepository.deleteById(id);
    }

    private Set<Permission> resolvePermissions(Set<Long> permissionIds) {
        if (permissionIds == null || permissionIds.isEmpty()) {
            return Collections.emptySet();
        }

        return permissionIds.stream()
                .map(permissionId -> permissionRepository.findById(permissionId)
                        .orElseThrow(() -> new IllegalArgumentException("Không tìm thấy quyền với ID: " + permissionId)))
                .collect(Collectors.toSet());
    }

    private RoleResponse toResponse(Role role) {
        return RoleResponse.builder()
                .id(role.getId())
                .name(role.getName())
                .description(role.getDescription())
                .permissions(role.getPermissions() != null
                        ? role.getPermissions().stream().map(Permission::getName).collect(Collectors.toSet())
                        : Collections.emptySet())
                .createdAt(role.getCreatedAt())
                .updatedAt(role.getUpdatedAt())
                .build();
    }
}
