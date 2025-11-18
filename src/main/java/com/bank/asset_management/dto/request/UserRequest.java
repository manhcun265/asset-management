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
public class UserRequest {

    @NotBlank(message = "Tên đăng nhập không được để trống!")
    @Size(min = 3, max = 50, message = "Tên đăng nhập phải từ 3-50 ký tự!")
    String username;

    @NotBlank(message = "Mật khẩu không được để trống!")
    @Size(min = 6, max = 100, message = "Mật khẩu phải từ 6-100 ký tự!")
    String password;

    @NotBlank(message = "Họ tên không được để trống!")
    @Size(min = 2, max = 100, message = "Họ tên phải từ 2-100 ký tự!")
    String fullName;

    @Email(message = "Email không đúng định dạng!")
    @Size(max = 100, message = "Email không được quá 100 ký tự!")
    String email;

    @NotNull(message = "Phòng ban là bắt buộc!")
    Long departmentId;

    Set<Long> roleIds;
}
