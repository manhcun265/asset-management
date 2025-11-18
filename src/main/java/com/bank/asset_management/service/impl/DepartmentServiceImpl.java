package com.bank.asset_management.service.impl;

import com.bank.asset_management.dto.request.DepartmentRequest;
import com.bank.asset_management.dto.response.DepartmentResponse;
import com.bank.asset_management.entity.Department;
import com.bank.asset_management.repository.DepartmentRepository;
import com.bank.asset_management.service.DepartmentService;
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
public class DepartmentServiceImpl implements DepartmentService {

    DepartmentRepository departmentRepository;

    @Override
    public DepartmentResponse handleCreateDepartment(DepartmentRequest request) {
        Department newDepartment = new Department();
        newDepartment.setName(request.getName());
        newDepartment.setDescription(request.getDescription());
        Department savedDepartment = departmentRepository.save(newDepartment);
        return toResponse(savedDepartment);
    }

    @Override
    public DepartmentResponse handleUpdateDepartment(Long id, DepartmentRequest request) {
        Department currentDepartment = departmentRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Không tìm thấy phòng ban với ID: " + id));
        currentDepartment.setName(request.getName());
        currentDepartment.setDescription(request.getDescription());
        Department savedDepartment = departmentRepository.save(currentDepartment);
        return toResponse(savedDepartment);
    }

    @Override
    @Transactional(readOnly = true)
    public DepartmentResponse handleGetDepartmentById(Long id) {
        Department department = departmentRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Không tìm thấy phòng ban với ID: " + id));
        return toResponse(department);
    }

    @Override
    @Transactional(readOnly = true)
    public Page<DepartmentResponse> handleGetAllDepartments(Pageable pageable) {
        return departmentRepository.findAll(pageable).map(this::toResponse);
    }

    @Override
    public void handleDeleteDepartment(Long id) {
        if (!departmentRepository.existsById(id)) {
            throw new IllegalArgumentException("Không tìm thấy phòng ban với ID: " + id);
        }
        departmentRepository.deleteById(id);
    }

    private DepartmentResponse toResponse(Department department) {
        return DepartmentResponse.builder()
                .id(department.getId())
                .name(department.getName())
                .description(department.getDescription())
                .createdAt(department.getCreatedAt())
                .updatedAt(department.getUpdatedAt())
                .build();
    }
}
