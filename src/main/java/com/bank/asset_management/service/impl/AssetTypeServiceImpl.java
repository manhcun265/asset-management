package com.bank.asset_management.service.impl;

import com.bank.asset_management.dto.request.AssetTypeRequest;
import com.bank.asset_management.dto.response.AssetTypeResponse;
import com.bank.asset_management.entity.AssetType;
import com.bank.asset_management.repository.AssetTypeRepository;
import com.bank.asset_management.service.AssetTypeService;
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
public class AssetTypeServiceImpl implements AssetTypeService {

    AssetTypeRepository assetTypeRepository;

    @Override
    public AssetTypeResponse handleCreateAssetType(AssetTypeRequest request) {
        // Kiểm tra tên loại tài sản trùng
        assetTypeRepository.findByNameIgnoreCase(request.getName())
                .ifPresent(existing -> {
                    throw new IllegalArgumentException("Tên loại tài sản đã tồn tại: " + request.getName());
                });

        AssetType newAssetType = new AssetType();
        newAssetType.setName(request.getName());
        newAssetType.setDescription(request.getDescription());
        AssetType savedAssetType = assetTypeRepository.save(newAssetType);
        return toResponse(savedAssetType);
    }

    @Override
    public AssetTypeResponse handleUpdateAssetType(Long id, AssetTypeRequest request) {
        AssetType currentAssetType = assetTypeRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Không tìm thấy loại tài sản với ID: " + id));

        // Kiểm tra tên loại tài sản trùng
        AssetType duplicateType = assetTypeRepository.findByNameIgnoreCase(request.getName()).orElse(null);
        if (duplicateType != null && !duplicateType.getId().equals(id)) {
            throw new IllegalArgumentException("Tên loại tài sản đã tồn tại: " + request.getName());
        }

        currentAssetType.setName(request.getName());
        currentAssetType.setDescription(request.getDescription());
        AssetType savedAssetType = assetTypeRepository.save(currentAssetType);
        return toResponse(savedAssetType);
    }

    @Override
    @Transactional(readOnly = true)
    public AssetTypeResponse handleGetAssetTypeById(Long id) {
        AssetType assetType = assetTypeRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Không tìm thấy loại tài sản với ID: " + id));
        return toResponse(assetType);
    }

    @Override
    @Transactional(readOnly = true)
    public Page<AssetTypeResponse> handleGetAllAssetTypes(Pageable pageable) {
        return assetTypeRepository.findAll(pageable).map(this::toResponse);
    }

    @Override
    public void handleDeleteAssetType(Long id) {
        if (!assetTypeRepository.existsById(id)) {
            throw new IllegalArgumentException("Không tìm thấy loại tài sản với ID: " + id);
        }
        assetTypeRepository.deleteById(id);
    }

    private AssetTypeResponse toResponse(AssetType assetType) {
        return AssetTypeResponse.builder()
                .id(assetType.getId())
                .name(assetType.getName())
                .description(assetType.getDescription())
                .createdAt(assetType.getCreatedAt())
                .updatedAt(assetType.getUpdatedAt())
                .build();
    }
}
