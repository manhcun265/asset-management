package com.bank.asset_management.dto.response;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.*;
import lombok.experimental.FieldDefaults;

import java.time.Instant;
import java.time.LocalDate;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@FieldDefaults(level = AccessLevel.PRIVATE)
public class AssetResponse {
    Long id;
    String assetCode;
    String name;
    String description;

    @JsonFormat(pattern = "dd-MM-yyyy")
    LocalDate purchaseDate;

    Double value;
    String status;
    Long typeId;
    String typeName;
    Long departmentId;
    String departmentName;
    Long managerId;
    String managerName;

    Instant createdAt;
    Instant updatedAt;
}
