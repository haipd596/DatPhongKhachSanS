package com.cntt.rentalmanagement.domain.payload.response;

import java.math.BigDecimal;

public record DashboardResponse(
    long totalRooms,
    long availableRooms,
    long confirmedBookings,
    BigDecimal revenue,
    double avgRating
) {
}
