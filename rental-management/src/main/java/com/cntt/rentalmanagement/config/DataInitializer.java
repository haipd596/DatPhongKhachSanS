package com.cntt.rentalmanagement.config;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Profile;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

import com.cntt.rentalmanagement.domain.enums.BookingStatus;
import com.cntt.rentalmanagement.domain.enums.PaymentStatus;
import com.cntt.rentalmanagement.domain.enums.RoomStatus;
import com.cntt.rentalmanagement.domain.enums.UserRole;
import com.cntt.rentalmanagement.domain.models.Booking;
import com.cntt.rentalmanagement.domain.models.PaymentTransaction;
import com.cntt.rentalmanagement.domain.models.Review;
import com.cntt.rentalmanagement.domain.models.Room;
import com.cntt.rentalmanagement.domain.models.RoomType;
import com.cntt.rentalmanagement.domain.models.User;
import com.cntt.rentalmanagement.repository.BookingRepository;
import com.cntt.rentalmanagement.repository.PaymentTransactionRepository;
import com.cntt.rentalmanagement.repository.ReviewRepository;
import com.cntt.rentalmanagement.repository.RoomRepository;
import com.cntt.rentalmanagement.repository.RoomTypeRepository;
import com.cntt.rentalmanagement.repository.UserRepository;
import com.cntt.rentalmanagement.services.VipPolicyService;

@Component
@Profile("!prod")
public class DataInitializer implements CommandLineRunner {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final RoomTypeRepository roomTypeRepository;
    private final RoomRepository roomRepository;
    private final BookingRepository bookingRepository;
    private final PaymentTransactionRepository paymentTransactionRepository;
    private final ReviewRepository reviewRepository;
    private final VipPolicyService vipPolicyService;

    public DataInitializer(UserRepository userRepository,
                           PasswordEncoder passwordEncoder,
                           RoomTypeRepository roomTypeRepository,
                           RoomRepository roomRepository,
                           BookingRepository bookingRepository,
                           PaymentTransactionRepository paymentTransactionRepository,
                           ReviewRepository reviewRepository,
                           VipPolicyService vipPolicyService) {
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
        this.roomTypeRepository = roomTypeRepository;
        this.roomRepository = roomRepository;
        this.bookingRepository = bookingRepository;
        this.paymentTransactionRepository = paymentTransactionRepository;
        this.reviewRepository = reviewRepository;
        this.vipPolicyService = vipPolicyService;
    }

    @Override
    public void run(String... args) {
        seedUsers();
        seedRooms();
        seedBookingsAndPayments();
        seedReviews();
    }

    private void seedUsers() {
        upsertUser("manager@rex.local", "Manager@123", "Nguyen Huu Khang", UserRole.MANAGER, 0);
        upsertUser("ops.lead@rex.local", "Manager@123", "Tran Minh Chau", UserRole.MANAGER, 0);

        upsertUser("customer1@rex.local", "Customer@123", "Nguyen Hoang An", UserRole.CUSTOMER, 2);
        upsertUser("customer2@rex.local", "Customer@123", "Tran Bao Ngoc", UserRole.CUSTOMER, 4);
        upsertUser("customer3@rex.local", "Customer@123", "Le Quoc Viet", UserRole.CUSTOMER, 1);
        upsertUser("pham.linh@rex.local", "Customer@123", "Pham Thu Linh", UserRole.CUSTOMER, 6);
        upsertUser("doan.khoa@rex.local", "Customer@123", "Doan Quoc Khoa", UserRole.CUSTOMER, 3);
        upsertUser("mai.nhi@rex.local", "Customer@123", "Mai Gia Nhi", UserRole.CUSTOMER, 5);
        upsertUser("vu.long@rex.local", "Customer@123", "Vu Duc Long", UserRole.CUSTOMER, 8);
        upsertUser("trang.nguyen@rex.local", "Customer@123", "Nguyen Minh Trang", UserRole.CUSTOMER, 7);
        upsertUser("anh.thu@rex.local", "Customer@123", "Nguyen Anh Thu", UserRole.CUSTOMER, 2);
        upsertUser("quang.huy@rex.local", "Customer@123", "Pham Quang Huy", UserRole.CUSTOMER, 4);
    }

    private User upsertUser(String email, String password, String fullName, UserRole role, int bookingCount) {
        User user = userRepository.findByEmail(email).orElseGet(() -> userRepository.save(
            new User(email, passwordEncoder.encode(password), fullName, role)
        ));
        user.setFullName(fullName);
        user.setRole(role);
        user.setBookingCount(bookingCount);
        user.setVipLevel(vipPolicyService.calculateLevel(bookingCount));
        if (!user.getPasswordHash().startsWith("$2a$") && !user.getPasswordHash().startsWith("$2b$")) {
            user.setPasswordHash(passwordEncoder.encode(password));
        }
        return userRepository.save(user);
    }

    private void seedRooms() {
        RoomType superior = upsertRoomType("Superior City", bd(1250000), 2,
            "Phong superior view pho, hop voi cap doi va khach cong tac ngan ngay.");
        RoomType deluxeTwin = upsertRoomType("Deluxe Twin", bd(1550000), 2,
            "Phong 2 giuong don, phu hop nhom ban hoac dong nghiep.");
        RoomType deluxeKing = upsertRoomType("Deluxe King", bd(1750000), 2,
            "Phong king bed, phong tam rong, tang trung tam.");
        RoomType family = upsertRoomType("Family Suite", bd(2450000), 4,
            "Suite gia dinh co khu vuc tiep khach rieng va bon tam.");
        RoomType executive = upsertRoomType("Executive Suite", bd(3150000), 3,
            "Phong cao cap view song, uu dai lounge va check-in rieng.");

        upsertRoom("S101", 1, superior, RoomStatus.AVAILABLE);
        upsertRoom("S102", 1, superior, RoomStatus.AVAILABLE);
        upsertRoom("S103", 1, superior, RoomStatus.AVAILABLE);
        upsertRoom("S104", 1, superior, RoomStatus.MAINTENANCE);

        upsertRoom("T201", 2, deluxeTwin, RoomStatus.AVAILABLE);
        upsertRoom("T202", 2, deluxeTwin, RoomStatus.AVAILABLE);
        upsertRoom("T203", 2, deluxeTwin, RoomStatus.AVAILABLE);

        upsertRoom("K301", 3, deluxeKing, RoomStatus.AVAILABLE);
        upsertRoom("K302", 3, deluxeKing, RoomStatus.AVAILABLE);
        upsertRoom("K303", 3, deluxeKing, RoomStatus.AVAILABLE);
        upsertRoom("K304", 3, deluxeKing, RoomStatus.AVAILABLE);

        upsertRoom("F401", 4, family, RoomStatus.AVAILABLE);
        upsertRoom("F402", 4, family, RoomStatus.AVAILABLE);
        upsertRoom("F403", 4, family, RoomStatus.MAINTENANCE);

        upsertRoom("E501", 5, executive, RoomStatus.AVAILABLE);
        upsertRoom("E502", 5, executive, RoomStatus.AVAILABLE);
    }

    private RoomType upsertRoomType(String name, BigDecimal basePrice, int maxGuests, String description) {
        RoomType roomType = roomTypeRepository.findByNameIgnoreCase(name).orElseGet(() ->
            roomTypeRepository.save(new RoomType(name, basePrice, maxGuests, description))
        );
        roomType.setBasePrice(basePrice);
        roomType.setMaxGuests(maxGuests);
        roomType.setDescription(description);
        return roomTypeRepository.save(roomType);
    }

    private Room upsertRoom(String code, int floor, RoomType roomType, RoomStatus status) {
        Room room = roomRepository.findByCode(code).orElseGet(() -> roomRepository.save(new Room(code, floor, roomType)));
        room.setFloorNumber(floor);
        room.setRoomType(roomType);
        room.setStatus(status);
        return roomRepository.save(room);
    }

    private void seedBookingsAndPayments() {
        if (bookingRepository.count() > 0) {
            return;
        }

        Map<String, User> users = usersByEmail(
            "customer1@rex.local", "customer2@rex.local", "customer3@rex.local", "pham.linh@rex.local",
            "doan.khoa@rex.local", "mai.nhi@rex.local", "vu.long@rex.local", "trang.nguyen@rex.local",
            "anh.thu@rex.local", "quang.huy@rex.local"
        );
        Map<String, Room> rooms = roomsByCode(
            "S101", "S102", "S103", "T201", "T202", "T203", "K301", "K302", "K303", "K304", "F401", "F402", "E501", "E502"
        );

        createBookingWithPayment(
            users.get("pham.linh@rex.local"), rooms.get("E501"),
            LocalDate.now().minusDays(48), LocalDate.now().minusDays(45),
            BookingStatus.CONFIRMED, bd(9450000),
            LocalDateTime.now().minusDays(55), PaymentStatus.SUCCESS, "PAY-240201-001", LocalDateTime.now().minusDays(55)
        );
        createBookingWithPayment(
            users.get("vu.long@rex.local"), rooms.get("F401"),
            LocalDate.now().minusDays(35), LocalDate.now().minusDays(31),
            BookingStatus.CONFIRMED, bd(9800000),
            LocalDateTime.now().minusDays(42), PaymentStatus.SUCCESS, "PAY-240214-001", LocalDateTime.now().minusDays(42)
        );
        createBookingWithPayment(
            users.get("trang.nguyen@rex.local"), rooms.get("K301"),
            LocalDate.now().minusDays(26), LocalDate.now().minusDays(23),
            BookingStatus.CONFIRMED, bd(5250000),
            LocalDateTime.now().minusDays(30), PaymentStatus.SUCCESS, "PAY-240222-001", LocalDateTime.now().minusDays(30)
        );
        createBookingWithPayment(
            users.get("customer2@rex.local"), rooms.get("T202"),
            LocalDate.now().minusDays(22), LocalDate.now().minusDays(20),
            BookingStatus.CONFIRMED, bd(3100000),
            LocalDateTime.now().minusDays(27), PaymentStatus.SUCCESS, "PAY-240226-001", LocalDateTime.now().minusDays(27)
        );
        createBookingWithPayment(
            users.get("mai.nhi@rex.local"), rooms.get("E502"),
            LocalDate.now().minusDays(14), LocalDate.now().minusDays(10),
            BookingStatus.CONFIRMED, bd(12600000),
            LocalDateTime.now().minusDays(20), PaymentStatus.SUCCESS, "PAY-240305-001", LocalDateTime.now().minusDays(20)
        );
        createBookingWithPayment(
            users.get("doan.khoa@rex.local"), rooms.get("S103"),
            LocalDate.now().minusDays(8), LocalDate.now().minusDays(6),
            BookingStatus.CONFIRMED, bd(2500000),
            LocalDateTime.now().minusDays(11), PaymentStatus.SUCCESS, "PAY-240313-001", LocalDateTime.now().minusDays(11)
        );
        createBookingWithPayment(
            users.get("customer1@rex.local"), rooms.get("K304"),
            LocalDate.now().plusDays(1), LocalDate.now().plusDays(4),
            BookingStatus.CONFIRMED, bd(5250000),
            LocalDateTime.now().minusDays(2), PaymentStatus.SUCCESS, "PAY-240327-001", LocalDateTime.now().minusDays(2)
        );
        createBookingWithPayment(
            users.get("quang.huy@rex.local"), rooms.get("F402"),
            LocalDate.now().plusDays(5), LocalDate.now().plusDays(8),
            BookingStatus.CONFIRMED, bd(7350000),
            LocalDateTime.now().minusDays(1), PaymentStatus.SUCCESS, "PAY-240328-001", LocalDateTime.now().minusDays(1)
        );

        createBookingWithPayment(
            users.get("customer3@rex.local"), rooms.get("T203"),
            LocalDate.now().plusDays(3), LocalDate.now().plusDays(5),
            BookingStatus.CANCELLED, bd(3100000),
            LocalDateTime.now().minusHours(18), PaymentStatus.FAILED, "PAY-240329-FAIL1", LocalDateTime.now().minusHours(18)
        );
        createBookingWithPayment(
            users.get("anh.thu@rex.local"), rooms.get("S102"),
            LocalDate.now().plusDays(10), LocalDate.now().plusDays(12),
            BookingStatus.CANCELLED, bd(2500000),
            LocalDateTime.now().minusDays(3), PaymentStatus.FAILED, "PAY-240326-FAIL1", LocalDateTime.now().minusDays(3)
        );

        createHoldBooking(
            users.get("customer3@rex.local"), rooms.get("K302"),
            LocalDate.now().plusDays(2), LocalDate.now().plusDays(4),
            bd(3500000), LocalDateTime.now().minusMinutes(9), LocalDateTime.now().plusMinutes(11)
        );
        createHoldBooking(
            users.get("customer1@rex.local"), rooms.get("S101"),
            LocalDate.now().plusDays(12), LocalDate.now().plusDays(13),
            bd(1250000), LocalDateTime.now().minusMinutes(4), LocalDateTime.now().plusMinutes(16)
        );

        createExpiredBooking(
            users.get("anh.thu@rex.local"), rooms.get("T201"),
            LocalDate.now().plusDays(15), LocalDate.now().plusDays(17),
            bd(3100000), LocalDateTime.now().minusDays(2), LocalDateTime.now().minusDays(1).minusMinutes(15)
        );
        createExpiredBooking(
            users.get("customer2@rex.local"), rooms.get("K303"),
            LocalDate.now().plusDays(18), LocalDate.now().plusDays(20),
            bd(3500000), LocalDateTime.now().minusDays(4), LocalDateTime.now().minusDays(3).minusMinutes(40)
        );
    }

    private void seedReviews() {
        if (reviewRepository.count() > 0) {
            return;
        }

        createReview("pham.linh@rex.local", 5,
            "Phong executive rat rong, buffet sang tot va nhan vien ho tro check-in nhanh.",
            LocalDateTime.now().minusDays(44));
        createReview("vu.long@rex.local", 5,
            "Di cung gia dinh rat hop, phong sach va co view dep vao buoi toi.",
            LocalDateTime.now().minusDays(30));
        createReview("trang.nguyen@rex.local", 4,
            "Vi tri trung tam, di lai tien. Cach am kha on voi tam gia nay.",
            LocalDateTime.now().minusDays(22));
        createReview("customer2@rex.local", 4,
            "Phong twin gon gang, phu hop chuyen cong tac ngan ngay.",
            LocalDateTime.now().minusDays(19));
        createReview("mai.nhi@rex.local", 5,
            "Dat phong online nhanh, nhan vien le tan xu ly rat chuyen nghiep.",
            LocalDateTime.now().minusDays(9));
        createReview("doan.khoa@rex.local", 4,
            "Phong dep hon hinh, nha tam sach va nuoc nong on dinh.",
            LocalDateTime.now().minusDays(5));
        createReview("customer1@rex.local", 5,
            "Se quay lai vao dip sau, trai nghiem tong the rat hai long.",
            LocalDateTime.now().minusHours(12));
    }

    private Map<String, User> usersByEmail(String... emails) {
        Map<String, User> users = new LinkedHashMap<>();
        for (String email : emails) {
            users.put(email, userRepository.findByEmail(email).orElseThrow());
        }
        return users;
    }

    private Map<String, Room> roomsByCode(String... codes) {
        Map<String, Room> rooms = new LinkedHashMap<>();
        for (String code : codes) {
            rooms.put(code, roomRepository.findByCode(code).orElseThrow());
        }
        return rooms;
    }

    private Booking createBookingWithPayment(User user,
                                             Room room,
                                             LocalDate checkIn,
                                             LocalDate checkOut,
                                             BookingStatus status,
                                             BigDecimal totalAmount,
                                             LocalDateTime createdAt,
                                             PaymentStatus paymentStatus,
                                             String transactionCode,
                                             LocalDateTime paymentCreatedAt) {
        Booking booking = createBooking(user, room, checkIn, checkOut, status, totalAmount, createdAt, null);
        PaymentTransaction paymentTransaction = new PaymentTransaction(booking, totalAmount, paymentStatus, transactionCode);
        paymentTransaction.setCreatedAt(paymentCreatedAt);
        paymentTransactionRepository.save(paymentTransaction);
        return booking;
    }

    private Booking createHoldBooking(User user,
                                      Room room,
                                      LocalDate checkIn,
                                      LocalDate checkOut,
                                      BigDecimal totalAmount,
                                      LocalDateTime createdAt,
                                      LocalDateTime holdExpiresAt) {
        return createBooking(user, room, checkIn, checkOut, BookingStatus.HOLD, totalAmount, createdAt, holdExpiresAt);
    }

    private Booking createExpiredBooking(User user,
                                         Room room,
                                         LocalDate checkIn,
                                         LocalDate checkOut,
                                         BigDecimal totalAmount,
                                         LocalDateTime createdAt,
                                         LocalDateTime holdExpiresAt) {
        return createBooking(user, room, checkIn, checkOut, BookingStatus.EXPIRED, totalAmount, createdAt, holdExpiresAt);
    }

    private Booking createBooking(User user,
                                  Room room,
                                  LocalDate checkIn,
                                  LocalDate checkOut,
                                  BookingStatus status,
                                  BigDecimal totalAmount,
                                  LocalDateTime createdAt,
                                  LocalDateTime holdExpiresAt) {
        Booking booking = new Booking(user, room, checkIn, checkOut, status, totalAmount);
        booking.setCreatedAt(createdAt);
        booking.setHoldExpiresAt(holdExpiresAt);
        return bookingRepository.save(booking);
    }

    private void createReview(String email, int rating, String comment, LocalDateTime createdAt) {
        User user = userRepository.findByEmail(email).orElseThrow();
        Review review = new Review(user, rating, comment);
        review.setCreatedAt(createdAt);
        reviewRepository.save(review);
    }

    private BigDecimal bd(long value) {
        return BigDecimal.valueOf(value);
    }
}
