package com.bank.asset_management.service.impl;

import com.bank.asset_management.dto.request.AssetRequest;
import com.bank.asset_management.dto.response.AssetResponse;
import com.bank.asset_management.entity.Asset;
import com.bank.asset_management.entity.AssetType;
import com.bank.asset_management.entity.Department;
import com.bank.asset_management.entity.User;
import com.bank.asset_management.repository.AssetRepository;
import com.bank.asset_management.repository.AssetTypeRepository;
import com.bank.asset_management.repository.DepartmentRepository;
import com.bank.asset_management.repository.UserRepository;
import com.bank.asset_management.service.AssetService;
import com.bank.asset_management.util.constant.AssetStatus;
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
public class AssetServiceImpl implements AssetService {

    AssetRepository assetRepository;
    DepartmentRepository departmentRepository;
    AssetTypeRepository assetTypeRepository;
    UserRepository userRepository;

    @Override
    public AssetResponse handleCreateAsset(AssetRequest request) {
        // Kiểm tra mã tài sản trùng
        if (assetRepository.existsByAssetCode(request.getAssetCode())) {
            throw new IllegalArgumentException("Mã tài sản đã tồn tại: " + request.getAssetCode());
        }

        Asset newAsset = new Asset();
        applyRequestToAsset(request, newAsset);
        Asset savedAsset = assetRepository.save(newAsset);
        return toResponse(savedAsset);
    }

    @Override
    public AssetResponse handleUpdateAsset(Long id, AssetRequest request) {
        Asset currentAsset = assetRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Không tìm thấy tài sản với ID: " + id));

        // Kiểm tra mã tài sản trùng (nếu thay đổi)
        if (!currentAsset.getAssetCode().equals(request.getAssetCode()) &&
                assetRepository.existsByAssetCode(request.getAssetCode())) {
            throw new IllegalArgumentException("Mã tài sản đã tồn tại: " + request.getAssetCode());
        }

        applyRequestToAsset(request, currentAsset);
        Asset savedAsset = assetRepository.save(currentAsset);
        return toResponse(savedAsset);
    }

    @Override
    @Transactional(readOnly = true)
    public AssetResponse handleGetAssetById(Long id) {
        Asset asset = assetRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Không tìm thấy tài sản với ID: " + id));
        return toResponse(asset);
    }

    @Override
    @Transactional(readOnly = true)
    public Page<AssetResponse> handleGetAllAssets(Pageable pageable) {
        return assetRepository.findAll(pageable).map(this::toResponse);
    }

    @Override
    public void handleDeleteAsset(Long id) {
        if (!assetRepository.existsById(id)) {
            throw new IllegalArgumentException("Không tìm thấy tài sản với ID: " + id);
        }
        assetRepository.deleteById(id);
    }

    private void applyRequestToAsset(AssetRequest request, Asset asset) {
        asset.setAssetCode(request.getAssetCode());
        asset.setName(request.getName());
        asset.setDescription(request.getDescription());
        asset.setPurchaseDate(request.getPurchaseDate());
        asset.setValue(request.getValue());

        // Set department
        Department department = departmentRepository.findById(request.getDepartmentId())
                .orElseThrow(() -> new IllegalArgumentException("Không tìm thấy phòng ban với ID: " + request.getDepartmentId()));
        asset.setDepartment(department);

        // Set asset type
        AssetType assetType = assetTypeRepository.findById(request.getTypeId())
                .orElseThrow(() -> new IllegalArgumentException("Không tìm thấy loại tài sản với ID: " + request.getTypeId()));
        asset.setType(assetType);

        // Set manager (optional)
        if (request.getManagerId() != null) {
            User manager = userRepository.findById(request.getManagerId())
                    .orElseThrow(() -> new IllegalArgumentException("Không tìm thấy người dùng với ID: " + request.getManagerId()));
            asset.setManager(manager);
        } else {
            asset.setManager(null);
        }

        // Set status
        asset.setStatus(resolveStatus(request.getStatus(), asset.getStatus()));
    }

    private AssetStatus resolveStatus(String requestedStatus, AssetStatus currentStatus) {
        if (requestedStatus == null || requestedStatus.isBlank()) {
            return currentStatus != null ? currentStatus : AssetStatus.AVAILABLE;
        }

        try {
            return AssetStatus.valueOf(requestedStatus.toUpperCase());
        } catch (IllegalArgumentException e) {
            throw new IllegalArgumentException(
                    "Trạng thái không hợp lệ: " + requestedStatus +
                            ". Các trạng thái hợp lệ: AVAILABLE, ASSIGNED, MAINTENANCE, RETIRED");
        }
    }

    private AssetResponse toResponse(Asset asset) {
        return AssetResponse.builder()
                .id(asset.getId())
                .assetCode(asset.getAssetCode())
                .name(asset.getName())
                .description(asset.getDescription())
                .purchaseDate(asset.getPurchaseDate())
                .value(asset.getValue())
                .status(asset.getStatus() != null ? asset.getStatus().name() : null)
                .typeId(asset.getType() != null ? asset.getType().getId() : null)
                .typeName(asset.getType() != null ? asset.getType().getName() : null)
                .departmentId(asset.getDepartment() != null ? asset.getDepartment().getId() : null)
                .departmentName(asset.getDepartment() != null ? asset.getDepartment().getName() : null)
                .managerId(asset.getManager() != null ? asset.getManager().getId() : null)
                .managerName(asset.getManager() != null ? asset.getManager().getFullName() : null)
                .createdAt(asset.getCreatedAt())
                .updatedAt(asset.getUpdatedAt())
                .build();
    }
}
