package com.bank.asset_management.service.impl;

import com.bank.asset_management.dto.request.UserRequest;
import com.bank.asset_management.dto.response.UserResponse;
import com.bank.asset_management.entity.Department;
import com.bank.asset_management.entity.Role;
import com.bank.asset_management.entity.User;
import com.bank.asset_management.repository.DepartmentRepository;
import com.bank.asset_management.repository.RoleRepository;
import com.bank.asset_management.repository.UserRepository;
import com.bank.asset_management.service.UserService;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Collections;
import java.util.Set;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
@Transactional
public class UserServiceImpl implements UserService {

    UserRepository userRepository;
    DepartmentRepository departmentRepository;
    RoleRepository roleRepository;
    PasswordEncoder passwordEncoder;

    @Override
    public UserResponse handleCreateUser(UserRequest request) {
        // Kiểm tra username trùng
        if (userRepository.existsByUsername(request.getUsername())) {
            throw new IllegalArgumentException("Tên đăng nhập đã tồn tại: " + request.getUsername());
        }

        // Kiểm tra email trùng
        if (request.getEmail() != null && userRepository.existsByEmail(request.getEmail())) {
            throw new IllegalArgumentException("Email đã tồn tại: " + request.getEmail());
        }

        User newUser = new User();
        newUser.setUsername(request.getUsername());
        newUser.setPassword(passwordEncoder.encode(request.getPassword()));
        newUser.setFullName(request.getFullName());
        newUser.setEmail(request.getEmail());

        // Set department
        Department department = departmentRepository.findById(request.getDepartmentId())
                .orElseThrow(() -> new IllegalArgumentException("Không tìm thấy phòng ban với ID: " + request.getDepartmentId()));
        newUser.setDepartment(department);

        // Set roles
        Set<Role> roles = resolveRoles(request.getRoleIds());
        newUser.setRoles(roles);

        User savedUser = userRepository.save(newUser);
        return toResponse(savedUser);
    }

    @Override
    public UserResponse handleUpdateUser(Long id, UserRequest request) {
        User currentUser = userRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Không tìm thấy người dùng với ID: " + id));

        // Kiểm tra username trùng (nếu thay đổi)
        if (!currentUser.getUsername().equals(request.getUsername()) &&
                userRepository.existsByUsername(request.getUsername())) {
            throw new IllegalArgumentException("Tên đăng nhập đã tồn tại: " + request.getUsername());
        }

        // Kiểm tra email trùng (nếu thay đổi)
        if (request.getEmail() != null &&
                !request.getEmail().equals(currentUser.getEmail()) &&
                userRepository.existsByEmail(request.getEmail())) {
            throw new IllegalArgumentException("Email đã tồn tại: " + request.getEmail());
        }

        currentUser.setUsername(request.getUsername());
        if (request.getPassword() != null && !request.getPassword().isEmpty()) {
            currentUser.setPassword(passwordEncoder.encode(request.getPassword()));
        }
        currentUser.setFullName(request.getFullName());
        currentUser.setEmail(request.getEmail());

        // Set department
        Department department = departmentRepository.findById(request.getDepartmentId())
                .orElseThrow(() -> new IllegalArgumentException("Không tìm thấy phòng ban với ID: " + request.getDepartmentId()));
        currentUser.setDepartment(department);

        // Set roles
        Set<Role> roles = resolveRoles(request.getRoleIds());
        currentUser.setRoles(roles);

        User savedUser = userRepository.save(currentUser);
        return toResponse(savedUser);
    }

    @Override
    @Transactional(readOnly = true)
    public UserResponse handleGetUserById(Long id) {
        User user = userRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Không tìm thấy người dùng với ID: " + id));
        return toResponse(user);
    }

    @Override
    @Transactional(readOnly = true)
    public Page<UserResponse> handleGetAllUsers(Pageable pageable) {
        return userRepository.findAll(pageable).map(this::toResponse);
    }

    @Override
    public void handleDeleteUser(Long id) {
        if (!userRepository.existsById(id)) {
            throw new IllegalArgumentException("Không tìm thấy người dùng với ID: " + id);
        }
        userRepository.deleteById(id);
    }

    @Override
    public User handleGetUserByUsername(String username) {
        return userRepository.findByUsernameWithRolesAndPermissions(username).orElse(null);
    }

    private Set<Role> resolveRoles(Set<Long> roleIds) {
        if (roleIds == null || roleIds.isEmpty()) {
            return Collections.emptySet();
        }

        return roleIds.stream()
                .map(roleId -> roleRepository.findById(roleId)
                        .orElseThrow(() -> new IllegalArgumentException("Không tìm thấy vai trò với ID: " + roleId)))
                .collect(Collectors.toSet());
    }

    private UserResponse toResponse(User user) {
        return UserResponse.builder()
                .id(user.getId())
                .username(user.getUsername())
                .email(user.getEmail())
                .fullName(user.getFullName())
                .active(user.isActive())
                .departmentId(user.getDepartment() != null ? user.getDepartment().getId() : null)
                .departmentName(user.getDepartment() != null ? user.getDepartment().getName() : null)
                .roles(user.getRoles() != null
                        ? user.getRoles().stream().map(Role::getName).collect(Collectors.toSet())
                        : Collections.emptySet())
                .createdAt(user.getCreatedAt())
                .updatedAt(user.getUpdatedAt())
                .build();
    }
}
