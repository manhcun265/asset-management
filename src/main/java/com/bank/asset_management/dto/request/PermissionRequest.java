package com.bank.asset_management.dto.request;

import jakarta.validation.constraints.*;
import lombok.*;
import lombok.experimental.FieldDefaults;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@FieldDefaults(level = AccessLevel.PRIVATE)
public class PermissionRequest {

    @NotBlank(message = "Tên quyền không được để trống!")
    @Size(min = 2, max = 100, message = "Tên quyền phải từ 2-100 ký tự!")
    String name;

    @Size(max = 500, message = "Mô tả không được quá 500 ký tự!")
    String description;
}
