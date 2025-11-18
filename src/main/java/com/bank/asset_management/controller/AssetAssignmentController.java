package com.bank.asset_management.controller;

import com.bank.asset_management.dto.request.AssetAssignmentRequest;
import com.bank.asset_management.dto.response.AssetAssignmentResponse;
import com.bank.asset_management.entity.AssetAssignment;
import com.bank.asset_management.repository.AssetAssignmentRepository;
import com.bank.asset_management.repository.AssetRepository;
import com.bank.asset_management.repository.UserRepository;
import com.bank.asset_management.service.AssetAssignmentService;
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
@RequestMapping("/api/asset-assignments")
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class AssetAssignmentController {

    AssetAssignmentService assetAssignmentService;
    AssetAssignmentRepository assetAssignmentRepository;
    AssetRepository assetRepository;
    UserRepository userRepository;

    @PostMapping
    @ApiMessage("Tạo bàn giao tài sản mới thành công!")
    public ResponseEntity<AssetAssignmentResponse> create(@Valid @RequestBody AssetAssignmentRequest request) throws IdInvalidException {
        if (!assetRepository.existsById(request.getAssetId())) {
            throw new IdInvalidException("Tài sản không tồn tại! Vui lòng kiểm tra lại.");
        }

        if (!userRepository.existsById(request.getUserId())) {
            throw new IdInvalidException("Người dùng không tồn tại! Vui lòng kiểm tra lại.");
        }

        if (request.getReturnedDate() != null &&
                request.getReturnedDate().isBefore(request.getAssignedDate())) {
            throw new IdInvalidException("Ngày trả không được sớm hơn ngày bàn giao!");
        }

        return ResponseEntity.status(HttpStatus.CREATED).body(assetAssignmentService.handleCreateAssetAssignment(request));
    }

    @PutMapping("/{id}")
    @ApiMessage("Cập nhật thông tin bàn giao tài sản thành công!")
    public ResponseEntity<AssetAssignmentResponse> update(@PathVariable Long id,
                                                          @Valid @RequestBody AssetAssignmentRequest request) throws IdInvalidException {
        AssetAssignment existingAssignment = assetAssignmentRepository.findById(id).orElse(null);
        if (existingAssignment == null) {
            throw new IdInvalidException("Bàn giao tài sản không tồn tại! Vui lòng kiểm tra lại ID.");
        }

        if (!assetRepository.existsById(request.getAssetId())) {
            throw new IdInvalidException("Tài sản không tồn tại! Vui lòng kiểm tra lại.");
        }

        if (!userRepository.existsById(request.getUserId())) {
            throw new IdInvalidException("Người dùng không tồn tại! Vui lòng kiểm tra lại.");
        }

        if (request.getReturnedDate() != null &&
                request.getReturnedDate().isBefore(request.getAssignedDate())) {
            throw new IdInvalidException("Ngày trả không được sớm hơn ngày bàn giao!");
        }

        return ResponseEntity.ok(assetAssignmentService.handleUpdateAssetAssignment(id, request));
    }

    @GetMapping("/{id}")
    @ApiMessage("Hiển thị thông tin chi tiết bàn giao tài sản thành công!")
    public ResponseEntity<AssetAssignmentResponse> get(@PathVariable Long id) throws IdInvalidException {
        AssetAssignment assignment = assetAssignmentRepository.findById(id).orElse(null);
        if (assignment == null) {
            throw new IdInvalidException("Bàn giao tài sản không tồn tại! Vui lòng kiểm tra lại ID.");
        }
        return ResponseEntity.ok(assetAssignmentService.handleGetAssetAssignmentById(id));
    }

    @GetMapping
    @ApiMessage("Hiển thị danh sách bàn giao tài sản thành công!")
    public ResponseEntity<Page<AssetAssignmentResponse>> list(@PageableDefault(size = 20) Pageable pageable) {
        return ResponseEntity.ok(assetAssignmentService.handleGetAllAssetAssignments(pageable));
    }

    @DeleteMapping("/{id}")
    @ApiMessage("Xóa bàn giao tài sản thành công!")
    public ResponseEntity<Void> delete(@PathVariable Long id) throws IdInvalidException {
        if (!assetAssignmentRepository.existsById(id)) {
            throw new IdInvalidException("Bàn giao tài sản không tồn tại! Vui lòng kiểm tra lại ID.");
        }
        assetAssignmentService.handleDeleteAssetAssignment(id);
        return ResponseEntity.ok().build();
    }
}

