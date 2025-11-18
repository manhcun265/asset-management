package com.bank.asset_management.controller;

import com.bank.asset_management.dto.request.UserRequest;
import com.bank.asset_management.dto.response.UserResponse;
import com.bank.asset_management.entity.User;
import com.bank.asset_management.repository.DepartmentRepository;
import com.bank.asset_management.repository.UserRepository;
import com.bank.asset_management.service.UserService;
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
@RequestMapping("/api/users")
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class UserController {

    UserService userService;
    UserRepository userRepository;
    DepartmentRepository departmentRepository;

    @PostMapping
    @ApiMessage("Tạo người dùng mới thành công!")
    public ResponseEntity<UserResponse> create(@Valid @RequestBody UserRequest request) throws IdInvalidException {
        // Kiểm tra username đã tồn tại
        if (userRepository.existsByUsername(request.getUsername())) {
            throw new IdInvalidException("Tên đăng nhập đã tồn tại! Vui lòng chọn tên khác.");
        }

        // Kiểm tra email đã tồn tại
        if (request.getEmail() != null && userRepository.existsByEmail(request.getEmail())) {
            throw new IdInvalidException("Email đã tồn tại! Vui lòng sử dụng email khác.");
        }

        // Kiểm tra phòng ban có tồn tại không
        if (!departmentRepository.existsById(request.getDepartmentId())) {
            throw new IdInvalidException("Phòng ban không tồn tại! Vui lòng kiểm tra lại.");
        }

        return ResponseEntity.status(HttpStatus.CREATED).body(userService.handleCreateUser(request));
    }

    @PutMapping("/{id}")
    @ApiMessage("Cập nhật thông tin người dùng thành công!")
    public ResponseEntity<UserResponse> update(@PathVariable Long id,
                                               @Valid @RequestBody UserRequest request) throws IdInvalidException {
        // Kiểm tra người dùng có tồn tại không
        User existingUser = userRepository.findById(id).orElse(null);
        if (existingUser == null) {
            throw new IdInvalidException("Người dùng không tồn tại! Vui lòng kiểm tra lại ID.");
        }

        // Kiểm tra username trùng (nếu thay đổi username)
        if (!existingUser.getUsername().equals(request.getUsername()) &&
                userRepository.existsByUsername(request.getUsername())) {
            throw new IdInvalidException("Tên đăng nhập đã tồn tại! Vui lòng chọn tên khác.");
        }

        // Kiểm tra email trùng (nếu thay đổi email)
        if (request.getEmail() != null &&
                !request.getEmail().equals(existingUser.getEmail()) &&
                userRepository.existsByEmail(request.getEmail())) {
            throw new IdInvalidException("Email đã tồn tại! Vui lòng sử dụng email khác.");
        }

        // Kiểm tra phòng ban có tồn tại không
        if (!departmentRepository.existsById(request.getDepartmentId())) {
            throw new IdInvalidException("Phòng ban không tồn tại! Vui lòng kiểm tra lại.");
        }

        return ResponseEntity.ok(userService.handleUpdateUser(id, request));
    }

    @GetMapping("/{id}")
    @ApiMessage("Hiển thị thông tin chi tiết người dùng thành công!")
    public ResponseEntity<UserResponse> get(@PathVariable Long id) throws IdInvalidException {
        User user = userRepository.findById(id).orElse(null);
        if (user == null) {
            throw new IdInvalidException("Người dùng không tồn tại! Vui lòng kiểm tra lại ID.");
        }
        return ResponseEntity.ok(userService.handleGetUserById(id));
    }

    @GetMapping
    @ApiMessage("Hiển thị danh sách người dùng thành công!")
    public ResponseEntity<Page<UserResponse>> list(@PageableDefault(size = 20) Pageable pageable) {
        return ResponseEntity.ok(userService.handleGetAllUsers(pageable));
    }

    @DeleteMapping("/{id}")
    @ApiMessage("Xóa người dùng thành công!")
    public ResponseEntity<Void> delete(@PathVariable Long id) throws IdInvalidException {
        // Kiểm tra người dùng có tồn tại không
        if (!userRepository.existsById(id)) {
            throw new IdInvalidException("Người dùng không tồn tại! Vui lòng kiểm tra lại ID.");
        }
        userService.handleDeleteUser(id);
        return ResponseEntity.ok().build();
    }
}

