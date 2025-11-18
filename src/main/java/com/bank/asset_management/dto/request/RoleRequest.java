package com.bank.asset_management.dto.request;

import jakarta.validation.constraints.*;
import lombok.*;
import lombok.experimental.FieldDefaults;

import java.util.Set;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@FieldDefaults(level = AccessLevel.PRIVATE)
public class RoleRequest {

    @NotBlank(message = "Tên vai trò không được để trống!")
    @Size(min = 2, max = 50, message = "Tên vai trò phải từ 2-50 ký tự!")
    String name;

    @Size(max = 500, message = "Mô tả không được quá 500 ký tự!")
    String description;

    // Danh sách quyền gắn với vai trò
    Set<Long> permissionIds;
}
