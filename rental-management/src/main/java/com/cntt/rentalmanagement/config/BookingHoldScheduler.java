package com.cntt.rentalmanagement.config;

import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import com.cntt.rentalmanagement.services.BookingService;

@Component
public class BookingHoldScheduler {

    private final BookingService bookingService;

    public BookingHoldScheduler(BookingService bookingService) {
        this.bookingService = bookingService;
    }

    @Scheduled(fixedDelayString = "${app.booking.expire-delay-ms:60000}")
    public void expireHolds() {
        bookingService.expireOldHolds();
    }
}
