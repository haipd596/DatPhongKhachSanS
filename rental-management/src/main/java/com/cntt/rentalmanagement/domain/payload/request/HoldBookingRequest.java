package com.cntt.rentalmanagement.domain.payload.request;

import java.time.LocalDate;

import org.springframework.format.annotation.DateTimeFormat;

import jakarta.validation.constraints.NotNull;

public record HoldBookingRequest(
    @NotNull(message = "Mã phòng không được để trống")
    Long roomId,
    @NotNull(message = "Ngày nhận phòng không được để trống")
    @DateTimeFormat(iso = DateTimeFormat.ISO.DATE)
    LocalDate checkInDate,
    @NotNull(message = "Ngày trả phòng không được để trống")
    @DateTimeFormat(iso = DateTimeFormat.ISO.DATE)
    LocalDate checkOutDate
) {
}
