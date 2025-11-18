package com.bank.asset_management.dto.response;

import lombok.*;
import lombok.experimental.FieldDefaults;

import java.time.Instant;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@FieldDefaults(level = AccessLevel.PRIVATE)
public class AssetTypeResponse {
    Long id;
    String name;
    String description;

    Instant createdAt;
    Instant updatedAt;
}
