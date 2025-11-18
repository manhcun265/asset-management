package com.bank.asset_management.controller;

import com.bank.asset_management.dto.request.AssetRequest;
import com.bank.asset_management.dto.response.AssetResponse;
import com.bank.asset_management.entity.Asset;
import com.bank.asset_management.repository.AssetRepository;
import com.bank.asset_management.repository.AssetTypeRepository;
import com.bank.asset_management.repository.DepartmentRepository;
import com.bank.asset_management.service.AssetService;
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
@RequestMapping("/api/assets")
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class AssetController {

    AssetService assetService;
    AssetRepository assetRepository;
    AssetTypeRepository assetTypeRepository;
    DepartmentRepository departmentRepository;

    @PostMapping
    @ApiMessage("Tạo tài sản mới thành công!")
    public ResponseEntity<AssetResponse> create(@Valid @RequestBody AssetRequest request) throws IdInvalidException {
        if (assetRepository.existsByAssetCode(request.getAssetCode())) {
            throw new IdInvalidException("Mã tài sản đã tồn tại! Vui lòng chọn mã khác.");
        }

        if (!assetTypeRepository.existsById(request.getTypeId())) {
            throw new IdInvalidException("Loại tài sản không tồn tại! Vui lòng kiểm tra lại.");
        }

        if (!departmentRepository.existsById(request.getDepartmentId())) {
            throw new IdInvalidException("Phòng ban không tồn tại! Vui lòng kiểm tra lại.");
        }

        return ResponseEntity.status(HttpStatus.CREATED).body(assetService.handleCreateAsset(request));
    }

    @PutMapping("/{id}")
    @ApiMessage("Cập nhật thông tin tài sản thành công!")
    public ResponseEntity<AssetResponse> update(@PathVariable Long id,
                                                @Valid @RequestBody AssetRequest request) throws IdInvalidException {
        Asset existingAsset = assetRepository.findById(id).orElse(null);
        if (existingAsset == null) {
            throw new IdInvalidException("Tài sản không tồn tại! Vui lòng kiểm tra lại ID.");
        }

        if (!existingAsset.getAssetCode().equals(request.getAssetCode()) &&
            assetRepository.existsByAssetCode(request.getAssetCode())) {
            throw new IdInvalidException("Mã tài sản đã tồn tại! Vui lòng chọn mã khác.");
        }

        if (!assetTypeRepository.existsById(request.getTypeId())) {
            throw new IdInvalidException("Loại tài sản không tồn tại! Vui lòng kiểm tra lại.");
        }

        if (!departmentRepository.existsById(request.getDepartmentId())) {
            throw new IdInvalidException("Phòng ban không tồn tại! Vui lòng kiểm tra lại.");
        }

        return ResponseEntity.ok(assetService.handleUpdateAsset(id, request));
    }

    @GetMapping("/{id}")
    @ApiMessage("Hiển thị thông tin chi tiết tài sản thành công!")
    public ResponseEntity<AssetResponse> get(@PathVariable Long id) throws IdInvalidException {
        Asset asset = assetRepository.findById(id).orElse(null);
        if (asset == null) {
            throw new IdInvalidException("Tài sản không tồn tại! Vui lòng kiểm tra lại ID.");
        }
        return ResponseEntity.ok(assetService.handleGetAssetById(id));
    }

    @GetMapping
    @ApiMessage("Hiển thị danh sách tài sản thành công!")
    public ResponseEntity<Page<AssetResponse>> list(@PageableDefault(size = 20) Pageable pageable) {
        return ResponseEntity.ok(assetService.handleGetAllAssets(pageable));
    }

    @DeleteMapping("/{id}")
    @ApiMessage("Xóa tài sản thành công!")
    public ResponseEntity<Void> delete(@PathVariable Long id) throws IdInvalidException {
        if (!assetRepository.existsById(id)) {
            throw new IdInvalidException("Tài sản không tồn tại! Vui lòng kiểm tra lại ID.");
        }
        assetService.handleDeleteAsset(id);
        return ResponseEntity.ok().build();
    }
}

