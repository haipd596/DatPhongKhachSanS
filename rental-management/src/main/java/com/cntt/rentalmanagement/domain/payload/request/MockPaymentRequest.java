package com.cntt.rentalmanagement.domain.payload.request;

import jakarta.validation.constraints.NotNull;

public record MockPaymentRequest(
    @NotNull Long bookingId
) {
}
