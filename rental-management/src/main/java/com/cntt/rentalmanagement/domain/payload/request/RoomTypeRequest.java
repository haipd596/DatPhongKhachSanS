package com.cntt.rentalmanagement.domain.payload.request;

import java.math.BigDecimal;

import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

public record RoomTypeRequest(
    @NotBlank(message = "Tên loại phòng không được để trống")
    @Size(max = 80, message = "Tên loại phòng không được vượt quá 80 ký tự")
    String name,
    @NotNull(message = "Giá cơ bản không được để trống")
    @DecimalMin(value = "1", message = "Giá cơ bản phải lớn hơn hoặc bằng 1")
    BigDecimal basePrice,
    @NotNull(message = "Số khách tối đa không được để trống")
    @Min(value = 1, message = "Số khách tối đa phải lớn hơn hoặc bằng 1")
    Integer maxGuests,
    @Size(max = 300, message = "Mô tả không được vượt quá 300 ký tự")
    String description
) {
}
