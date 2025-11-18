package com.bank.asset_management.service;

import com.bank.asset_management.dto.request.UserRequest;
import com.bank.asset_management.dto.response.UserResponse;
import com.bank.asset_management.entity.User;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

public interface UserService {
    UserResponse handleCreateUser(UserRequest request);

    UserResponse handleUpdateUser(Long id, UserRequest request);

    UserResponse handleGetUserById(Long id);

    Page<UserResponse> handleGetAllUsers(Pageable pageable);

    void handleDeleteUser(Long id);

    User handleGetUserByUsername(String username);
}
