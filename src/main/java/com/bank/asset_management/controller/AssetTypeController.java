package com.bank.asset_management.controller;

import com.bank.asset_management.dto.request.AssetTypeRequest;
import com.bank.asset_management.dto.response.AssetTypeResponse;
import com.bank.asset_management.entity.AssetType;
import com.bank.asset_management.repository.AssetTypeRepository;
import com.bank.asset_management.service.AssetTypeService;
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
@RequestMapping("/api/asset-types")
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class AssetTypeController {

    AssetTypeService assetTypeService;
    AssetTypeRepository assetTypeRepository;

    @PostMapping
    @ApiMessage("Tạo loại tài sản mới thành công!")
    public ResponseEntity<AssetTypeResponse> create(@Valid @RequestBody AssetTypeRequest request) throws IdInvalidException {
        if (assetTypeRepository.findByNameIgnoreCase(request.getName()).isPresent()) {
            throw new IdInvalidException("Tên loại tài sản đã tồn tại! Vui lòng chọn tên khác.");
        }
        return ResponseEntity.status(HttpStatus.CREATED).body(assetTypeService.handleCreateAssetType(request));
    }

    @PutMapping("/{id}")
    @ApiMessage("Cập nhật thông tin loại tài sản thành công!")
    public ResponseEntity<AssetTypeResponse> update(@PathVariable Long id,
                                                    @Valid @RequestBody AssetTypeRequest request) throws IdInvalidException {
        AssetType existingType = assetTypeRepository.findById(id).orElse(null);
        if (existingType == null) {
            throw new IdInvalidException("Loại tài sản không tồn tại! Vui lòng kiểm tra lại ID.");
        }

        AssetType duplicateType = assetTypeRepository.findByNameIgnoreCase(request.getName()).orElse(null);
        if (duplicateType != null && !duplicateType.getId().equals(id)) {
            throw new IdInvalidException("Tên loại tài sản đã tồn tại! Vui lòng chọn tên khác.");
        }

        return ResponseEntity.ok(assetTypeService.handleUpdateAssetType(id, request));
    }

    @GetMapping("/{id}")
    @ApiMessage("Hiển thị thông tin chi tiết loại tài sản thành công!")
    public ResponseEntity<AssetTypeResponse> get(@PathVariable Long id) throws IdInvalidException {
        AssetType assetType = assetTypeRepository.findById(id).orElse(null);
        if (assetType == null) {
            throw new IdInvalidException("Loại tài sản không tồn tại! Vui lòng kiểm tra lại ID.");
        }
        return ResponseEntity.ok(assetTypeService.handleGetAssetTypeById(id));
    }

    @GetMapping
    @ApiMessage("Hiển thị danh sách loại tài sản thành công!")
    public ResponseEntity<Page<AssetTypeResponse>> list(@PageableDefault(size = 20) Pageable pageable) {
        return ResponseEntity.ok(assetTypeService.handleGetAllAssetTypes(pageable));
    }

    @DeleteMapping("/{id}")
    @ApiMessage("Xóa loại tài sản thành công!")
    public ResponseEntity<Void> delete(@PathVariable Long id) throws IdInvalidException {
        if (!assetTypeRepository.existsById(id)) {
            throw new IdInvalidException("Loại tài sản không tồn tại! Vui lòng kiểm tra lại ID.");
        }
        assetTypeService.handleDeleteAssetType(id);
        return ResponseEntity.ok().build();
    }
}

