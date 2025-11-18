package com.bank.asset_management.dto.response;

import lombok.*;
import lombok.Builder.Default;
import lombok.experimental.FieldDefaults;

import java.util.Set;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@FieldDefaults(level = AccessLevel.PRIVATE)
public class LoginResponse {
    String token;
    @Default
    String tokenType = "Bearer";
    Long userId;
    String username;
    String fullName;
    String email;
    Set<String> roles;
}

