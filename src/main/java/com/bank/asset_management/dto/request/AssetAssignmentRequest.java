package com.bank.asset_management.dto.request;

import com.fasterxml.jackson.annotation.JsonFormat;
import jakarta.validation.constraints.*;
import lombok.*;
import lombok.experimental.FieldDefaults;

import java.time.LocalDate;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@FieldDefaults(level = AccessLevel.PRIVATE)
public class AssetAssignmentRequest {

    @NotNull(message = "Tài sản là bắt buộc!")
    Long assetId;

    @NotNull(message = "Người dùng là bắt buộc!")
    Long userId;

    @NotNull(message = "Ngày bàn giao là bắt buộc!")
    @JsonFormat(pattern = "dd-MM-yyyy")
    LocalDate assignedDate;

    @JsonFormat(pattern = "dd-MM-yyyy")
    LocalDate returnedDate;

    String status; // AVAILABLE, ASSIGNED, MAINTENANCE, RETIRED
}
