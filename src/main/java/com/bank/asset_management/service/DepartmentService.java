package com.bank.asset_management.service;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Page;
import com.bank.asset_management.dto.response.DepartmentResponse;
import com.bank.asset_management.dto.request.DepartmentRequest;

public interface DepartmentService {


    void handleDeleteDepartment(Long id);

    Page<DepartmentResponse> handleGetAllDepartments(Pageable pageable);

    DepartmentResponse handleGetDepartmentById(Long id);

    DepartmentResponse handleUpdateDepartment(Long id, DepartmentRequest request);

    DepartmentResponse handleCreateDepartment(DepartmentRequest request);


}