package com.bank.asset_management.dto.response;

import lombok.*;
import lombok.experimental.FieldDefaults;

import java.time.Instant;
import java.util.Set;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@FieldDefaults(level = AccessLevel.PRIVATE)
public class UserResponse {
    Long id;
    String username;
    String fullName;
    String email;
    boolean active;
    Long departmentId;
    String departmentName;
    Set<String> roles;

    // Instant sẽ được format bởi InstantSerializer (Asia/Ho_Chi_Minh)
    Instant createdAt;
    Instant updatedAt;
}
