package com.cntt.rentalmanagement.services;

import org.springframework.stereotype.Service;

import com.cntt.rentalmanagement.domain.enums.BookingStatus;
import com.cntt.rentalmanagement.domain.enums.RoomStatus;
import com.cntt.rentalmanagement.domain.payload.response.DashboardResponse;
import com.cntt.rentalmanagement.repository.BookingRepository;
import com.cntt.rentalmanagement.repository.RoomRepository;

@Service
public class DashboardService {

    private final RoomRepository roomRepository;
    private final BookingRepository bookingRepository;
    private final ReviewService reviewService;

    public DashboardService(RoomRepository roomRepository, BookingRepository bookingRepository, ReviewService reviewService) {
        this.roomRepository = roomRepository;
        this.bookingRepository = bookingRepository;
        this.reviewService = reviewService;
    }

    public DashboardResponse getManagerDashboard() {
        long totalRooms = roomRepository.count();
        long availableRooms = roomRepository.findByStatus(RoomStatus.AVAILABLE).size();
        long confirmedBookings = bookingRepository.countByStatus(BookingStatus.CONFIRMED);
        return new DashboardResponse(
            totalRooms,
            availableRooms,
            confirmedBookings,
            bookingRepository.sumTotalByStatus(BookingStatus.CONFIRMED),
            reviewService.avgRating()
        );
    }
}
