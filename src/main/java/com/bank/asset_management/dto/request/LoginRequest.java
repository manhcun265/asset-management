package com.bank.asset_management.dto.request;

import jakarta.validation.constraints.NotBlank;
import lombok.*;
import lombok.experimental.FieldDefaults;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@FieldDefaults(level = AccessLevel.PRIVATE)
public class LoginRequest {

    @NotBlank(message = "Tên đăng nhập là bắt buộc")
    String username;

    @NotBlank(message = "Mật khẩu là bắt buộc")
    String password;
}

