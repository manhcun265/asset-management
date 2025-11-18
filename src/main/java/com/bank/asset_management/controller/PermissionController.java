package com.bank.asset_management.controller;

import com.bank.asset_management.dto.request.PermissionRequest;
import com.bank.asset_management.dto.response.PermissionResponse;
import com.bank.asset_management.entity.Permission;
import com.bank.asset_management.repository.PermissionRepository;
import com.bank.asset_management.service.PermissionService;
import com.bank.asset_management.util.annotation.ApiMessage;
import jakarta.validation.Valid;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/permissions")
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class PermissionController {

    PermissionService permissionService;
    PermissionRepository permissionRepository;

    @PostMapping
    @ApiMessage("Tạo quyền mới thành công!")
    public ResponseEntity<PermissionResponse> create(@Valid @RequestBody PermissionRequest request) {
        // Kiểm tra tên quyền đã tồn tại
        if (permissionRepository.findByName(request.getName()).isPresent()) {
            throw new IllegalArgumentException("Tên quyền đã tồn tại! Vui lòng chọn tên khác.");
        }
        return ResponseEntity.status(HttpStatus.CREATED).body(permissionService.handleCreatePermission(request));
    }

    @PutMapping("/{id}")
    @ApiMessage("Cập nhật thông tin quyền thành công!")
    public ResponseEntity<PermissionResponse> update(@PathVariable Long id,
                                                     @Valid @RequestBody PermissionRequest request) {
        // Kiểm tra quyền có tồn tại không
        Permission existingPermission = permissionRepository.findById(id).orElse(null);
        if (existingPermission == null) {
            throw new IllegalArgumentException("Quyền không tồn tại! Vui lòng kiểm tra lại ID.");
        }

        // Kiểm tra tên quyền trùng (nếu thay đổi tên)
        Permission duplicatePermission = permissionRepository.findByName(request.getName()).orElse(null);
        if (duplicatePermission != null && !duplicatePermission.getId().equals(id)) {
            throw new IllegalArgumentException("Tên quyền đã tồn tại! Vui lòng chọn tên khác.");
        }

        return ResponseEntity.ok(permissionService.handleUpdatePermission(id, request));
    }

    @GetMapping("/{id}")
    @ApiMessage("Hiển thị thông tin chi tiết quyền thành công!")
    public ResponseEntity<PermissionResponse> get(@PathVariable Long id) {
        PermissionResponse permission = permissionService.handleGetPermissionById(id);
        if (permission == null) {
            throw new IllegalArgumentException("Quyền không tồn tại! Vui lòng kiểm tra lại ID.");
        }
        return ResponseEntity.ok(permission);
    }

    @GetMapping
    @ApiMessage("Hiển thị danh sách quyền thành công!")
    public ResponseEntity<Page<PermissionResponse>> list(@PageableDefault(size = 20) Pageable pageable) {
        return ResponseEntity.ok(permissionService.handleGetAllPermissions(pageable));
    }

    @DeleteMapping("/{id}")
    @ApiMessage("Xóa quyền thành công!")
    public ResponseEntity<Void> delete(@PathVariable Long id) {
        // Kiểm tra quyền có tồn tại không
        if (!permissionRepository.existsById(id)) {
            throw new IllegalArgumentException("Quyền không tồn tại! Vui lòng kiểm tra lại ID.");
        }
        permissionService.handleDeletePermission(id);
        return ResponseEntity.ok().build();
    }
}

