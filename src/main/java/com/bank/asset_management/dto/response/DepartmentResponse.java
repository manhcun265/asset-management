package com.bank.asset_management.dto.response;

import lombok.*;
import lombok.experimental.FieldDefaults;

import java.time.Instant;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@FieldDefaults(level = AccessLevel.PRIVATE)
public class DepartmentResponse {
    Long id;
    String name;
    String description;

    // Instant sẽ được format bởi InstantSerializer global (Asia/Ho_Chi_Minh)
    Instant createdAt;
    Instant updatedAt;
}
