package com.bank.asset_management.controller;

import com.bank.asset_management.dto.request.RoleRequest;
import com.bank.asset_management.dto.response.RoleResponse;
import com.bank.asset_management.entity.Role;
import com.bank.asset_management.repository.RoleRepository;
import com.bank.asset_management.service.RoleService;
import com.bank.asset_management.util.annotation.ApiMessage;
import com.bank.asset_management.util.exception.IdInvalidException;
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
@RequestMapping("/api/roles")
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class RoleController {

    RoleService roleService;
    RoleRepository roleRepository;

    @PostMapping
    @ApiMessage("Tạo vai trò mới thành công!")
    public ResponseEntity<RoleResponse> create(@Valid @RequestBody RoleRequest request) throws IdInvalidException {
        if (roleRepository.existsByName(request.getName())) {
            throw new IdInvalidException("Tên vai trò đã tồn tại! Vui lòng chọn tên khác.");
        }
        return ResponseEntity.status(HttpStatus.CREATED).body(roleService.handleCreateRole(request));
    }

    @PutMapping("/{id}")
    @ApiMessage("Cập nhật thông tin vai trò thành công!")
    public ResponseEntity<RoleResponse> update(@PathVariable Long id,
                                               @Valid @RequestBody RoleRequest request) throws IdInvalidException {
        Role existingRole = roleRepository.findById(id).orElse(null);
        if (existingRole == null) {
            throw new IdInvalidException("Vai trò không tồn tại! Vui lòng kiểm tra lại ID.");
        }

        if (!existingRole.getName().equals(request.getName()) && roleRepository.existsByName(request.getName())) {
            throw new IdInvalidException("Tên vai trò đã tồn tại! Vui lòng chọn tên khác.");
        }

        return ResponseEntity.ok(roleService.handleUpdateRole(id, request));
    }

    @GetMapping("/{id}")
    @ApiMessage("Hiển thị thông tin chi tiết vai trò thành công!")
    public ResponseEntity<RoleResponse> get(@PathVariable Long id) throws IdInvalidException {
        Role role = roleRepository.findById(id).orElse(null);
        if (role == null) {
            throw new IdInvalidException("Vai trò không tồn tại! Vui lòng kiểm tra lại ID.");
        }
        return ResponseEntity.ok(roleService.handleGetRoleById(id));
    }

    @GetMapping
    @ApiMessage("Hiển thị danh sách vai trò thành công!")
    public ResponseEntity<Page<RoleResponse>> list(@PageableDefault(size = 20) Pageable pageable) {
        return ResponseEntity.ok(roleService.handleGetAllRoles(pageable));
    }

    @DeleteMapping("/{id}")
    @ApiMessage("Xóa vai trò thành công!")
    public ResponseEntity<Void> delete(@PathVariable Long id) throws IdInvalidException {
        if (!roleRepository.existsById(id)) {
            throw new IdInvalidException("Vai trò không tồn tại! Vui lòng kiểm tra lại ID.");
        }
        roleService.handleDeleteRole(id);
        return ResponseEntity.ok().build();
    }
}

