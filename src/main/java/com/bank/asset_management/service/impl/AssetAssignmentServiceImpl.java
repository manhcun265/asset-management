package com.bank.asset_management.service.impl;

import com.bank.asset_management.dto.request.AssetAssignmentRequest;
import com.bank.asset_management.dto.response.AssetAssignmentResponse;
import com.bank.asset_management.entity.Asset;
import com.bank.asset_management.entity.AssetAssignment;
import com.bank.asset_management.entity.User;
import com.bank.asset_management.repository.AssetAssignmentRepository;
import com.bank.asset_management.repository.AssetRepository;
import com.bank.asset_management.repository.UserRepository;
import com.bank.asset_management.service.AssetAssignmentService;
import com.bank.asset_management.util.constant.AssetStatus;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;

@Service
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
@Transactional
public class AssetAssignmentServiceImpl implements AssetAssignmentService {

    AssetAssignmentRepository assetAssignmentRepository;
    AssetRepository assetRepository;
    UserRepository userRepository;

    @Override
    public AssetAssignmentResponse handleCreateAssetAssignment(AssetAssignmentRequest request) {
        AssetAssignment newAssignment = new AssetAssignment();
        applyRequestToAssignment(request, newAssignment);
        AssetAssignment savedAssignment = assetAssignmentRepository.save(newAssignment);
        return toResponse(savedAssignment);
    }

    @Override
    public AssetAssignmentResponse handleUpdateAssetAssignment(Long id, AssetAssignmentRequest request) {
        AssetAssignment currentAssignment = assetAssignmentRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Không tìm thấy bàn giao tài sản với ID: " + id));
        applyRequestToAssignment(request, currentAssignment);
        AssetAssignment savedAssignment = assetAssignmentRepository.save(currentAssignment);
        return toResponse(savedAssignment);
    }

    @Override
    @Transactional(readOnly = true)
    public AssetAssignmentResponse handleGetAssetAssignmentById(Long id) {
        AssetAssignment assignment = assetAssignmentRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Không tìm thấy bàn giao tài sản với ID: " + id));
        return toResponse(assignment);
    }

    @Override
    @Transactional(readOnly = true)
    public Page<AssetAssignmentResponse> handleGetAllAssetAssignments(Pageable pageable) {
        return assetAssignmentRepository.findAll(pageable).map(this::toResponse);
    }

    @Override
    public void handleDeleteAssetAssignment(Long id) {
        if (!assetAssignmentRepository.existsById(id)) {
            throw new IllegalArgumentException("Không tìm thấy bàn giao tài sản với ID: " + id);
        }
        assetAssignmentRepository.deleteById(id);
    }

    private void applyRequestToAssignment(AssetAssignmentRequest request, AssetAssignment assignment) {
        // Set asset
        Asset asset = assetRepository.findById(request.getAssetId())
                .orElseThrow(() -> new IllegalArgumentException("Không tìm thấy tài sản với ID: " + request.getAssetId()));
        assignment.setAsset(asset);

        // Set user
        User user = userRepository.findById(request.getUserId())
                .orElseThrow(() -> new IllegalArgumentException("Không tìm thấy người dùng với ID: " + request.getUserId()));
        assignment.setUser(user);

        // Validate dates
        validateDates(request.getAssignedDate(), request.getReturnedDate());
        assignment.setAssignedDate(request.getAssignedDate());
        assignment.setReturnedDate(request.getReturnedDate());

        // Set status
        assignment.setStatus(resolveStatus(request.getStatus(), assignment.getStatus()));
    }

    private void validateDates(LocalDate assignedDate, LocalDate returnedDate) {
        if (assignedDate == null) {
            throw new IllegalArgumentException("Ngày bàn giao là bắt buộc");
        }
        if (returnedDate != null && returnedDate.isBefore(assignedDate)) {
            throw new IllegalArgumentException("Ngày trả không được sớm hơn ngày bàn giao");
        }
    }

    private AssetStatus resolveStatus(String requestedStatus, AssetStatus currentStatus) {
        if (requestedStatus == null || requestedStatus.isBlank()) {
            return currentStatus != null ? currentStatus : AssetStatus.ASSIGNED;
        }

        try {
            return AssetStatus.valueOf(requestedStatus.toUpperCase());
        } catch (IllegalArgumentException e) {
            throw new IllegalArgumentException(
                    "Trạng thái không hợp lệ: " + requestedStatus +
                            ". Các trạng thái hợp lệ: AVAILABLE, ASSIGNED, MAINTENANCE, RETIRED");
        }
    }

    private AssetAssignmentResponse toResponse(AssetAssignment assignment) {
        return AssetAssignmentResponse.builder()
                .id(assignment.getId())
                .assetId(assignment.getAsset() != null ? assignment.getAsset().getId() : null)
                .assetName(assignment.getAsset() != null ? assignment.getAsset().getName() : null)
                .userId(assignment.getUser() != null ? assignment.getUser().getId() : null)
                .username(assignment.getUser() != null ? assignment.getUser().getUsername() : null)
                .assignedDate(assignment.getAssignedDate())
                .returnedDate(assignment.getReturnedDate())
                .status(assignment.getStatus() != null ? assignment.getStatus().name() : null)
                .createdAt(assignment.getCreatedAt())
                .updatedAt(assignment.getUpdatedAt())
                .build();
    }
}
