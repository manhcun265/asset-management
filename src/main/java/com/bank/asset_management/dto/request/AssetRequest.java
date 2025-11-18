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
public class AssetRequest {

    @NotBlank(message = "Mã tài sản không được để trống!")
    @Size(min = 2, max = 50, message = "Mã tài sản phải từ 2-50 ký tự!")
    String assetCode;

    @NotBlank(message = "Tên tài sản không được để trống!")
    @Size(min = 2, max = 200, message = "Tên tài sản phải từ 2-200 ký tự!")
    String name;

    @NotNull(message = "Loại tài sản là bắt buộc!")
    Long typeId;

    @NotNull(message = "Phòng ban là bắt buộc!")
    Long departmentId;

    Long managerId;

    String status; // AVAILABLE, ASSIGNED, MAINTENANCE, RETIRED

    @JsonFormat(pattern = "dd-MM-yyyy")
    LocalDate purchaseDate;

    @DecimalMin(value = "0.0", message = "Giá trị phải lớn hơn 0!")
    Double value;

    @Size(max = 1000, message = "Mô tả không được quá 1000 ký tự!")
    String description;
}
