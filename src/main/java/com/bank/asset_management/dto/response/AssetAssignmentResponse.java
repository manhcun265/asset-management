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
public class AssetAssignmentResponse {
    Long id;
    Long assetId;
    String assetName;
    Long userId;
    String username;

    @JsonFormat(pattern = "dd-MM-yyyy")
    LocalDate assignedDate;

    @JsonFormat(pattern = "dd-MM-yyyy")
    LocalDate returnedDate;

    String status;

    Instant createdAt;
    Instant updatedAt;
}
