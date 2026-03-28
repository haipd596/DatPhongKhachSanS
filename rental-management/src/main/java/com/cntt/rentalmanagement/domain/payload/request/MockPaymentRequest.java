package com.cntt.rentalmanagement.domain.payload.request;

import jakarta.validation.constraints.NotNull;

public record MockPaymentRequest(
    @NotNull(message = "Mã đơn đặt phòng không được để trống")
    Long bookingId
) {
}
