package com.bank.asset_management.controller;

import com.bank.asset_management.dto.request.DepartmentRequest;
import com.bank.asset_management.dto.response.DepartmentResponse;
import com.bank.asset_management.repository.DepartmentRepository;
import com.bank.asset_management.service.DepartmentService;
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
@RequestMapping("/api/departments")
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class DepartmentController {

    DepartmentService departmentService;
    DepartmentRepository departmentRepository;

    @PostMapping
    @ApiMessage("Tạo phòng ban mới thành công!")
    public ResponseEntity<DepartmentResponse> create(@Valid @RequestBody DepartmentRequest request) {
        return ResponseEntity.status(HttpStatus.CREATED).body(departmentService.handleCreateDepartment(request));
    }

    @PutMapping("/{id}")
    @ApiMessage("Cập nhật thông tin phòng ban thành công!")
    public ResponseEntity<DepartmentResponse> update(@PathVariable Long id,
                                                     @Valid @RequestBody DepartmentRequest request) {
        // Kiểm tra phòng ban có tồn tại không
        if (!departmentRepository.existsById(id)) {
            throw new IllegalArgumentException("Phòng ban không tồn tại! Vui lòng kiểm tra lại ID.");
        }
        return ResponseEntity.ok(departmentService.handleUpdateDepartment(id, request));
    }

    @GetMapping("/{id}")
    @ApiMessage("Hiển thị thông tin chi tiết phòng ban thành công!")
    public ResponseEntity<DepartmentResponse> get(@PathVariable Long id) {
        DepartmentResponse department = departmentService.handleGetDepartmentById(id);
        if (department == null) {
            throw new IllegalArgumentException("Phòng ban không tồn tại! Vui lòng kiểm tra lại ID.");
        }
        return ResponseEntity.ok(department);
    }

    @GetMapping
    @ApiMessage("Hiển thị danh sách phòng ban thành công!")
    public ResponseEntity<Page<DepartmentResponse>> list(@PageableDefault(size = 20) Pageable pageable) {
        return ResponseEntity.ok(departmentService.handleGetAllDepartments(pageable));
    }

    @DeleteMapping("/{id}")
    @ApiMessage("Xóa phòng ban thành công!")
    public ResponseEntity<Void> delete(@PathVariable Long id) {
        // Kiểm tra phòng ban có tồn tại không
        if (!departmentRepository.existsById(id)) {
            throw new IllegalArgumentException("Phòng ban không tồn tại! Vui lòng kiểm tra lại ID.");
        }
        departmentService.handleDeleteDepartment(id);
        return ResponseEntity.ok().build();
    }
}

