package com.cntt.rentalmanagement.services;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;
import java.util.List;
import java.util.Set;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.cntt.rentalmanagement.domain.enums.BookingStatus;
import com.cntt.rentalmanagement.domain.enums.RoomStatus;
import com.cntt.rentalmanagement.domain.models.Booking;
import com.cntt.rentalmanagement.domain.models.Room;
import com.cntt.rentalmanagement.domain.models.User;
import com.cntt.rentalmanagement.domain.payload.request.HoldBookingRequest;
import com.cntt.rentalmanagement.domain.payload.response.BookingResponse;
import com.cntt.rentalmanagement.exception.ApiException;
import com.cntt.rentalmanagement.repository.BookingRepository;
import com.cntt.rentalmanagement.repository.RoomRepository;
import com.cntt.rentalmanagement.repository.UserRepository;

@Service
public class BookingService {

    private static final Set<BookingStatus> BLOCKING_STATUSES = Set.of(BookingStatus.HOLD, BookingStatus.CONFIRMED);

    private final BookingRepository bookingRepository;
    private final UserRepository userRepository;
    private final RoomRepository roomRepository;
    private final VipPolicyService vipPolicyService;
    private final BookingNotificationService bookingNotificationService;
    private final long holdMinutes;

    public BookingService(BookingRepository bookingRepository,
                          UserRepository userRepository,
                          RoomRepository roomRepository,
                          VipPolicyService vipPolicyService,
                          BookingNotificationService bookingNotificationService,
                          @Value("${app.booking.hold-minutes:10}") long holdMinutes) {
        this.bookingRepository = bookingRepository;
        this.userRepository = userRepository;
        this.roomRepository = roomRepository;
        this.vipPolicyService = vipPolicyService;
        this.bookingNotificationService = bookingNotificationService;
        this.holdMinutes = holdMinutes;
    }

    @Transactional
    public BookingResponse holdRoom(String email, HoldBookingRequest request) {
        LocalDate checkIn = request.checkInDate();
        LocalDate checkOut = request.checkOutDate();
        if (checkIn == null || checkOut == null || !checkIn.isBefore(checkOut)) {
            throw new ApiException("Ngày nhận phòng và ngày trả phòng không hợp lệ");
        }
        if (checkIn.isBefore(LocalDate.now())) {
            throw new ApiException("Không thể đặt phòng trong quá khứ");
        }

        User user = userRepository.findByEmail(email).orElseThrow(() -> new ApiException("Không tìm thấy người dùng"));
        Room room = roomRepository.findById(request.roomId()).orElseThrow(() -> new ApiException("Không tìm thấy phòng"));
        if (room.getStatus() != RoomStatus.AVAILABLE) {
            throw new ApiException("Phòng hiện không khả dụng");
        }

        long overlapping = bookingRepository.countOverlapping(room.getId(), BLOCKING_STATUSES, checkIn, checkOut);
        if (overlapping > 0) {
            throw new ApiException("Phòng đã được đặt hoặc đang được giữ trong khung thời gian này");
        }

        long nights = ChronoUnit.DAYS.between(checkIn, checkOut);
        BigDecimal rawTotal = room.getRoomType().getBasePrice().multiply(BigDecimal.valueOf(nights));
        BigDecimal total = vipPolicyService.applyDiscount(rawTotal, user.getVipLevel());

        Booking booking = new Booking(user, room, checkIn, checkOut, BookingStatus.HOLD, total);
        booking.setHoldExpiresAt(LocalDateTime.now().plusMinutes(holdMinutes));
        bookingRepository.save(booking);
        bookingNotificationService.sendHoldConfirmation(booking);
        return toResponse(booking);
    }

    public List<BookingResponse> myBookings(String email) {
        return bookingRepository.findByUserEmailOrderByCreatedAtDesc(email).stream().map(this::toResponse).toList();
    }

    public Booking getOwnedBooking(Long bookingId, String email) {
        return bookingRepository.findByIdAndUserEmail(bookingId, email)
            .orElseThrow(() -> new ApiException("Không tìm thấy đơn đặt phòng"));
    }

    @Transactional
    public BookingResponse cancel(Long bookingId, String email) {
        Booking booking = bookingRepository.findByIdAndUserEmail(bookingId, email)
            .orElseThrow(() -> new ApiException("Không tìm thấy đơn đặt phòng"));
        if (booking.getStatus() == BookingStatus.CANCELLED || booking.getStatus() == BookingStatus.EXPIRED) {
            throw new ApiException("Đơn đặt phòng đã ở trạng thái hủy hoặc hết hạn");
        }
        booking.setStatus(BookingStatus.CANCELLED);
        booking.setHoldExpiresAt(null);
        bookingRepository.save(booking);
        bookingNotificationService.sendBookingCancelled(booking);
        return toResponse(booking);
    }

    @Transactional
    public Booking confirmPaymentSuccess(Long bookingId, String email) {
        Booking booking = bookingRepository.findByIdAndUserEmail(bookingId, email)
            .orElseThrow(() -> new ApiException("Không tìm thấy đơn đặt phòng"));
        if (booking.getStatus() != BookingStatus.HOLD) {
            throw new ApiException("Đơn đặt phòng không ở trạng thái chờ thanh toán");
        }
        if (booking.getHoldExpiresAt() != null && booking.getHoldExpiresAt().isBefore(LocalDateTime.now())) {
            booking.setStatus(BookingStatus.EXPIRED);
            booking.setHoldExpiresAt(null);
            bookingRepository.save(booking);
            throw new ApiException("Đơn đặt phòng đã hết thời gian giữ phòng");
        }
        booking.setStatus(BookingStatus.CONFIRMED);
        booking.setHoldExpiresAt(null);
        User user = booking.getUser();
        int newCount = user.getBookingCount() + 1;
        user.setBookingCount(newCount);
        user.setVipLevel(vipPolicyService.calculateLevel(newCount));
        userRepository.save(user);
        bookingRepository.save(booking);
        bookingNotificationService.sendBookingConfirmed(booking);
        return booking;
    }

    @Transactional
    public int expireOldHolds() {
        List<Booking> expired = bookingRepository.findByStatusAndHoldExpiresAtBefore(BookingStatus.HOLD, LocalDateTime.now());
        expired.forEach(booking -> {
            booking.setStatus(BookingStatus.EXPIRED);
            booking.setHoldExpiresAt(null);
        });
        bookingRepository.saveAll(expired);
        return expired.size();
    }

    public BookingResponse toResponse(Booking booking) {
        return new BookingResponse(
            booking.getId(),
            booking.getRoom().getCode(),
            booking.getRoom().getRoomType().getName(),
            booking.getCheckInDate(),
            booking.getCheckOutDate(),
            booking.getStatus().name(),
            booking.getTotalAmount(),
            booking.getHoldExpiresAt(),
            booking.getCreatedAt()
        );
    }
}
