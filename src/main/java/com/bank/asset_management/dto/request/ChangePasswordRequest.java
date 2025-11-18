package com.bank.asset_management.dto.request;

import jakarta.validation.constraints.NotBlank;
import lombok.*;
import lombok.experimental.FieldDefaults;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@FieldDefaults(level = AccessLevel.PRIVATE)
public class ChangePasswordRequest {
    @NotBlank(message = "Mật khẩu hiện tại là bắt buộc")
    String currentPassword;

    @NotBlank(message = "Mật khẩu mới là bắt buộc")
    String newPassword;
}

