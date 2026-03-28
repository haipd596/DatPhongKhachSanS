package com.cntt.rentalmanagement.domain.payload.request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

public record ResetPasswordRequest(
    @NotBlank(message = "Mật khẩu mới không được để trống")
    @Size(min = 6, max = 72, message = "Mật khẩu mới phải có từ 6 đến 72 ký tự")
    String newPassword,
    @NotBlank(message = "Mã xác nhận không được để trống")
    @Size(min = 6, max = 8, message = "Mã xác nhận phải có từ 6 đến 8 ký tự")
    String code
) {
}
