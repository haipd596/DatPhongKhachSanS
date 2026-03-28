package com.cntt.rentalmanagement.domain.payload.request;

import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

public record ReviewRequest(
    @NotNull(message = "Điểm đánh giá không được để trống")
    @Min(value = 1, message = "Điểm đánh giá tối thiểu là 1")
    @Max(value = 5, message = "Điểm đánh giá tối đa là 5")
    Integer rating,
    @NotBlank(message = "Nội dung đánh giá không được để trống")
    @Size(max = 500, message = "Nội dung đánh giá không được vượt quá 500 ký tự")
    String comment
) {
}
