package com.hotelbooking.config.seeder;

import com.hotelbooking.enums.*;
import com.hotelbooking.model.*;
import com.hotelbooking.repository.*;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.jspecify.annotations.NonNull;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;
import java.time.Instant;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.temporal.ChronoUnit;
import java.util.*;

@Slf4j
@Component
@RequiredArgsConstructor
public class DataSeeder implements ApplicationRunner {

    private final UserRepository userRepository;
    private final RoleRepository roleRepository;
    private final PermissionRepository permissionRepository;

    private final RoomTypeRepository roomTypeRepository;
    private final RoomRepository roomRepository;

    private final CartRepository cartRepository;
    private final CartItemRepository cartItemRepository;

    private final BookingRepository bookingRepository;
    private final BookingItemRepository bookingItemRepository;
    private final RoomAssignmentRepository roomAssignmentRepository;
    private final RoomBookingSlotRepository roomBookingSlotRepository;

    private final PaymentRepository paymentRepository;

    private final PasswordEncoder passwordEncoder;

    @Override
    public void run(@NonNull ApplicationArguments args) {

        log.warn("DemoDataSeeder: RESETTING demo collections before seeding...");
        resetDemoData();

        // 1. RBAC + users (> 10 users for pagination)
        seedRbacData();

        // 2. Room types (> 10 room types for pagination)
        seedRoomTypeData();

        // 3. Rooms (> 10 rooms for pagination)
        seedRoomData();

        // 4. Bookings + booking items + room assignments
        //    - Dynamic demo bookings based on today
        //    - Historical/statistics data for 2024, 2025, 2026
        seedBookingData();

        // 5. Cart + cart items
        seedCartData();

        // 6. Room booking slots for statuses that are currently holding rooms
        seedRoomBookingSlots();

        // 7. Payments, consistent with booking statuses
        seedPayments();

        log.info("DemoDataSeeder: DONE");
        log.info("Demo login ADMIN : admin / @Admin123");
        log.info("Demo login USER  : alice / @User123");
        log.info("Locked USER      : lockeduser / @Locked123");
    }

    // =========================================================
    // RESET
    // =========================================================

    /**
     * Reset demo collections in child -> parent order so every application start
     * recreates the same demo dataset structure.
     * <p>
     * IMPORTANT: this intentionally deletes existing demo/application data.
     */
    private void resetDemoData() {

        roomBookingSlotRepository.deleteAll();
        roomAssignmentRepository.deleteAll();
        paymentRepository.deleteAll();
        bookingItemRepository.deleteAll();
        bookingRepository.deleteAll();

        cartItemRepository.deleteAll();
        cartRepository.deleteAll();

        roomRepository.deleteAll();
        roomTypeRepository.deleteAll();

        userRepository.deleteAll();
        roleRepository.deleteAll();
        permissionRepository.deleteAll();

        log.info("DemoDataSeeder: collections reset successfully");
    }

    // =========================================================
    // RBAC + USERS
    // =========================================================

    private void seedRbacData() {

        log.info("DemoDataSeeder: seeding permissions, roles and users...");

        Map<String, Permission> perms = new LinkedHashMap<>();

        perms.put("USER_VIEW", savePermission("USER_VIEW", "View users"));
        perms.put("USER_CREATE", savePermission("USER_CREATE", "Create user"));
        perms.put("USER_UPDATE", savePermission("USER_UPDATE", "Update user"));
        perms.put("USER_DELETE", savePermission("USER_DELETE", "Delete user"));
        perms.put("USER_ASSIGN_ROLE", savePermission("USER_ASSIGN_ROLE", "Assign or remove role"));
        perms.put("ADMIN_VIEW", savePermission("ADMIN_VIEW", "Admin can view"));

        Role roleAdmin = saveRole(
                "ADMIN",
                "Administrator",
                List.of(
                        perms.get("USER_VIEW"),
                        perms.get("USER_CREATE"),
                        perms.get("USER_UPDATE"),
                        perms.get("USER_DELETE"),
                        perms.get("USER_ASSIGN_ROLE"),
                        perms.get("ADMIN_VIEW")
                )
        );

        Role roleEditor = saveRole(
                "EDITOR",
                "Editor",
                List.of(
                        perms.get("USER_VIEW"),
                        perms.get("USER_CREATE"),
                        perms.get("USER_UPDATE"),
                        perms.get("ADMIN_VIEW")
                )
        );

        Role roleUser = saveRole(
                "USER",
                "User",
                List.of(perms.get("USER_VIEW"))
        );

        // Main demo accounts
        saveUser(
                "admin",
                "admin@demo.local",
                "@Admin123",
                "System Admin",
                Gender.MALE,
                LocalDate.of(1995, 1, 15),
                "0901000001",
                "Ho Chi Minh City",
                null,
                true,
                List.of(roleAdmin.getId())
        );

        saveUser(
                "editor",
                "editor@demo.local",
                "@Editor123",
                "Hotel Editor",
                Gender.FEMALE,
                LocalDate.of(1998, 5, 20),
                "0901000002",
                "Ho Chi Minh City",
                null,
                true,
                List.of(roleEditor.getId())
        );

        saveUser(
                "alice",
                "alice@demo.local",
                "@User123",
                "Alice Nguyen",
                Gender.FEMALE,
                LocalDate.of(2000, 10, 10),
                "0901000003",
                "Da Nang",
                null,
                true,
                List.of(roleUser.getId())
        );

        saveUser(
                "peter",
                "peter@demo.local",
                "@User456",
                "Peter Tran",
                Gender.MALE,
                LocalDate.of(2001, 8, 5),
                "0901000004",
                "Ha Noi",
                null,
                true,
                List.of(roleUser.getId())
        );

        // Locked account for login/authorization demo
        saveUser(
                "lockeduser",
                "lockeduser@demo.local",
                "@Locked123",
                "Locked Demo User",
                Gender.MALE,
                LocalDate.of(1999, 3, 12),
                "0901000005",
                "Can Tho",
                null,
                false,
                List.of(roleUser.getId())
        );

        // Extra users for admin pagination/search demo
        String[] cities = {
                "Ho Chi Minh City",
                "Ha Noi",
                "Da Nang",
                "Can Tho",
                "Hue"
        };

        for (int i = 1; i <= 12; i++) {
            String number = String.format("%02d", i);

            saveUser(
                    "demo" + number,
                    "demo" + number + "@demo.local",
                    "@Demo123",
                    "Demo User " + number,
                    i % 2 == 0 ? Gender.FEMALE : Gender.MALE,
                    LocalDate.of(1990 + (i % 10), ((i - 1) % 12) + 1, Math.min(i + 5, 28)),
                    "09120000" + number,
                    cities[(i - 1) % cities.length],
                    null,
                    true,
                    List.of(roleUser.getId())
            );
        }

        log.info("DemoDataSeeder: users seeded = {}", userRepository.count());
    }

    // =========================================================
    // ROOM TYPES
    // =========================================================

    private void seedRoomTypeData() {

        log.info("DemoDataSeeder: seeding room types...");

        saveRoomType("Deluxe King Room", 35.5, "WiFi, Air Conditioning, Mini Bar, Flat-screen TV, Safe", 2, new BigDecimal("120.00"), RoomTypeStatus.ACTIVE);
        saveRoomType("Standard Twin Room", 22.0, "WiFi, Air Conditioning, Flat-screen TV", 2, new BigDecimal("75.50"), RoomTypeStatus.ACTIVE);
        saveRoomType("Executive Suite", 55.0, "WiFi, Air Conditioning, Mini Bar, Jacuzzi, Living Area, Ocean View", 4, new BigDecimal("250.00"), RoomTypeStatus.ACTIVE);
        saveRoomType("Family Room", 45.0, "WiFi, Air Conditioning, Extra Bed, Flat-screen TV, Balcony", 5, new BigDecimal("180.75"), RoomTypeStatus.ACTIVE);
        saveRoomType("Single Economy Room", 16.0, "WiFi, Fan, Shared Bathroom", 1, new BigDecimal("45.00"), RoomTypeStatus.ACTIVE);
        saveRoomType("Superior Double Room", 28.0, "WiFi, Air Conditioning, Mini Fridge, Flat-screen TV", 2, new BigDecimal("95.00"), RoomTypeStatus.INACTIVE);
        saveRoomType("Presidential Suite", 90.0, "WiFi, Air Conditioning, Private Pool, Butler Service, Jacuzzi, Living Area", 6, new BigDecimal("500.00"), RoomTypeStatus.INACTIVE);
        saveRoomType("Junior Suite", 42.0, "WiFi, Air Conditioning, Mini Bar, Sofa, City View", 3, new BigDecimal("160.00"), RoomTypeStatus.INACTIVE);
        saveRoomType("Double Room with Balcony", 26.0, "WiFi, Air Conditioning, Balcony, Flat-screen TV", 2, new BigDecimal("88.00"), RoomTypeStatus.INACTIVE);
        saveRoomType("Twin Room City View", 24.0, "WiFi, Air Conditioning, City View, Flat-screen TV", 2, new BigDecimal("80.00"), RoomTypeStatus.INACTIVE);
        saveRoomType("Deluxe Queen Room", 32.0, "WiFi, Air Conditioning, Mini Bar, Flat-screen TV", 2, new BigDecimal("110.00"), RoomTypeStatus.ACTIVE);
        saveRoomType("Family Suite", 60.0, "WiFi, Air Conditioning, Kitchenette, 2 Bedrooms, Living Area", 6, new BigDecimal("220.00"), RoomTypeStatus.ACTIVE);
        saveRoomType("Economy Single", 14.0, "WiFi, Fan", 1, new BigDecimal("38.00"), RoomTypeStatus.ACTIVE);
        saveRoomType("Budget Double", 18.0, "WiFi, Air Conditioning", 2, new BigDecimal("55.00"), RoomTypeStatus.INACTIVE);
        saveRoomType("Honeymoon Suite", 48.0, "WiFi, Air Conditioning, Jacuzzi, Mini Bar, Romantic Decor", 2, new BigDecimal("210.00"), RoomTypeStatus.ACTIVE);
        saveRoomType("Accessible Room", 30.0, "WiFi, Air Conditioning, Wheelchair Access, Grab Bars", 2, new BigDecimal("90.00"), RoomTypeStatus.ACTIVE);
        saveRoomType("Penthouse Suite", 120.0, "WiFi, Air Conditioning, Private Terrace, Jacuzzi, Bar, Panoramic View", 8, new BigDecimal("650.00"), RoomTypeStatus.INACTIVE);
        saveRoomType("Standard Single Room", 15.0, "WiFi, Air Conditioning, Desk", 1, new BigDecimal("50.00"), RoomTypeStatus.ACTIVE);
        saveRoomType("Deluxe Twin Room", 30.0, "WiFi, Air Conditioning, Mini Bar, Flat-screen TV", 2, new BigDecimal("105.00"), RoomTypeStatus.INACTIVE);
        saveRoomType("Garden View Room", 27.0, "WiFi, Air Conditioning, Garden View, Flat-screen TV", 2, new BigDecimal("92.00"), RoomTypeStatus.ACTIVE);
        saveRoomType("Ocean View Suite", 50.0, "WiFi, Air Conditioning, Mini Bar, Ocean View, Balcony", 3, new BigDecimal("240.00"), RoomTypeStatus.ACTIVE);
        saveRoomType("Studio Room", 25.0, "WiFi, Air Conditioning, Kitchenette, Flat-screen TV", 2, new BigDecimal("98.00"), RoomTypeStatus.ACTIVE);
        saveRoomType("Loft Suite", 65.0, "WiFi, Air Conditioning, Mini Bar, Mezzanine, City View", 4, new BigDecimal("270.00"), RoomTypeStatus.INACTIVE);
        saveRoomType("Connecting Family Room", 55.0, "WiFi, Air Conditioning, 2 Connecting Rooms, Flat-screen TV", 5, new BigDecimal("195.00"), RoomTypeStatus.ACTIVE);
        saveRoomType("Poolside Room", 33.0, "WiFi, Air Conditioning, Pool Access, Flat-screen TV", 2, new BigDecimal("115.00"), RoomTypeStatus.ACTIVE);
        saveRoomType("Business Room", 29.0, "WiFi, Air Conditioning, Work Desk, Flat-screen TV, Coffee Machine", 2, new BigDecimal("100.00"), RoomTypeStatus.INACTIVE);
        saveRoomType("Royal Suite", 100.0, "WiFi, Air Conditioning, Private Pool, Butler Service, Living Area, Dining Area", 6, new BigDecimal("580.00"), RoomTypeStatus.ACTIVE);
        saveRoomType("Cozy Single Room", 13.0, "WiFi, Fan, Desk", 1, new BigDecimal("35.00"), RoomTypeStatus.INACTIVE);
        saveRoomType("Mountain View Room", 31.0, "WiFi, Air Conditioning, Mountain View, Flat-screen TV", 2, new BigDecimal("102.00"), RoomTypeStatus.ACTIVE);
        saveRoomType("Duplex Suite", 70.0, "WiFi, Air Conditioning, 2 Floors, Mini Bar, Living Area", 4, new BigDecimal("300.00"), RoomTypeStatus.ACTIVE);
        saveRoomType("Classic Double Room", 23.0, "WiFi, Air Conditioning, Flat-screen TV", 2, new BigDecimal("78.00"), RoomTypeStatus.ACTIVE);
        saveRoomType("VIP Suite", 85.0, "WiFi, Air Conditioning, Private Bar, Jacuzzi, Butler Service", 5, new BigDecimal("450.00"), RoomTypeStatus.INACTIVE);
        saveRoomType("Compact Twin Room", 19.0, "WiFi, Air Conditioning, Flat-screen TV", 2, new BigDecimal("60.00"), RoomTypeStatus.INACTIVE);
        saveRoomType("Skyline Suite", 58.0, "WiFi, Air Conditioning, Mini Bar, Panoramic City View, Balcony", 3, new BigDecimal("260.00"), RoomTypeStatus.ACTIVE);
        saveRoomType("Traditional Family Room", 47.0, "WiFi, Air Conditioning, Extra Bed, Flat-screen TV", 4, new BigDecimal("165.00"), RoomTypeStatus.ACTIVE);

        log.info("DemoDataSeeder: room types seeded = {}", roomTypeRepository.count());
    }

    // =========================================================
    // ROOMS
    // =========================================================

    private void seedRoomData() {

        List<RoomType> roomTypes = roomTypeRepository.findAllByDeleteFlagFalse();

        RoomType deluxeKing = requireSeedRoomType(roomTypes, "Deluxe King Room");
        RoomType standardTwin = requireSeedRoomType(roomTypes, "Standard Twin Room");
        RoomType executiveSuite = requireSeedRoomType(roomTypes, "Executive Suite");
        RoomType familyRoom = requireSeedRoomType(roomTypes, "Family Room");
        RoomType singleEconomy = requireSeedRoomType(roomTypes, "Single Economy Room");
        RoomType deluxeQueen = requireSeedRoomType(roomTypes, "Deluxe Queen Room");
        RoomType familySuite = requireSeedRoomType(roomTypes, "Family Suite");
        RoomType honeymoonSuite = requireSeedRoomType(roomTypes, "Honeymoon Suite");
        RoomType accessibleRoom = requireSeedRoomType(roomTypes, "Accessible Room");
        RoomType gardenView = requireSeedRoomType(roomTypes, "Garden View Room");
        RoomType oceanViewSuite = requireSeedRoomType(roomTypes, "Ocean View Suite");
        RoomType studioRoom = requireSeedRoomType(roomTypes, "Studio Room");
        RoomType poolsideRoom = requireSeedRoomType(roomTypes, "Poolside Room");
        RoomType classicDouble = requireSeedRoomType(roomTypes, "Classic Double Room");
        RoomType mountainView = requireSeedRoomType(roomTypes, "Mountain View Room");
        RoomType duplexSuite = requireSeedRoomType(roomTypes, "Duplex Suite");

        // Floor 1
        saveRoom(deluxeKing, 101, 1, RoomStatus.ACTIVE);
        saveRoom(standardTwin, 102, 1, RoomStatus.ACTIVE);
        saveRoom(executiveSuite, 103, 1, RoomStatus.ACTIVE);
        saveRoom(familyRoom, 104, 1, RoomStatus.ACTIVE);
        saveRoom(singleEconomy, 105, 1, RoomStatus.ACTIVE);
        saveRoom(deluxeQueen, 106, 1, RoomStatus.MAINTENANCE);

        // Floor 2
        saveRoom(standardTwin, 201, 2, RoomStatus.ACTIVE);
        saveRoom(deluxeQueen, 202, 2, RoomStatus.ACTIVE);
        saveRoom(deluxeKing, 203, 2, RoomStatus.ACTIVE);
        saveRoom(familySuite, 204, 2, RoomStatus.ACTIVE);
        saveRoom(executiveSuite, 205, 2, RoomStatus.ACTIVE);
        saveRoom(accessibleRoom, 206, 2, RoomStatus.ACTIVE);

        // Floor 3
        saveRoom(familyRoom, 301, 3, RoomStatus.ACTIVE);
        saveRoom(gardenView, 302, 3, RoomStatus.ACTIVE);
        saveRoom(deluxeKing, 303, 3, RoomStatus.ACTIVE);
        saveRoom(standardTwin, 304, 3, RoomStatus.ACTIVE);
        saveRoom(executiveSuite, 305, 3, RoomStatus.ACTIVE);
        saveRoom(studioRoom, 306, 3, RoomStatus.MAINTENANCE);

        // Floor 4
        saveRoom(oceanViewSuite, 401, 4, RoomStatus.ACTIVE);
        saveRoom(familySuite, 402, 4, RoomStatus.ACTIVE);
        saveRoom(standardTwin, 403, 4, RoomStatus.ACTIVE);
        saveRoom(deluxeKing, 404, 4, RoomStatus.ACTIVE);
        saveRoom(honeymoonSuite, 405, 4, RoomStatus.ACTIVE);
        saveRoom(accessibleRoom, 406, 4, RoomStatus.OUT_OF_SERVICE);

        // Floor 5
        saveRoom(executiveSuite, 501, 5, RoomStatus.ACTIVE);
        saveRoom(familyRoom, 502, 5, RoomStatus.ACTIVE);
        saveRoom(deluxeQueen, 503, 5, RoomStatus.ACTIVE);
        saveRoom(gardenView, 504, 5, RoomStatus.ACTIVE);
        saveRoom(deluxeKing, 505, 5, RoomStatus.ACTIVE);
        saveRoom(classicDouble, 506, 5, RoomStatus.MAINTENANCE);

        // Floor 6
        saveRoom(singleEconomy, 601, 6, RoomStatus.ACTIVE);
        saveRoom(standardTwin, 602, 6, RoomStatus.ACTIVE);
        saveRoom(familyRoom, 603, 6, RoomStatus.ACTIVE);
        saveRoom(executiveSuite, 604, 6, RoomStatus.ACTIVE);
        saveRoom(mountainView, 605, 6, RoomStatus.ACTIVE);
        saveRoom(studioRoom, 606, 6, RoomStatus.ACTIVE);

        // Floor 7
        saveRoom(deluxeKing, 701, 7, RoomStatus.ACTIVE);
        saveRoom(executiveSuite, 702, 7, RoomStatus.ACTIVE);
        saveRoom(familySuite, 703, 7, RoomStatus.ACTIVE);
        saveRoom(poolsideRoom, 704, 7, RoomStatus.ACTIVE);
        saveRoom(classicDouble, 705, 7, RoomStatus.ACTIVE);
        saveRoom(accessibleRoom, 706, 7, RoomStatus.OUT_OF_SERVICE);

        // Floor 8
        saveRoom(familyRoom, 801, 8, RoomStatus.ACTIVE);
        saveRoom(standardTwin, 802, 8, RoomStatus.ACTIVE);
        saveRoom(deluxeKing, 803, 8, RoomStatus.ACTIVE);
        saveRoom(executiveSuite, 804, 8, RoomStatus.ACTIVE);
        saveRoom(oceanViewSuite, 805, 8, RoomStatus.ACTIVE);
        saveRoom(deluxeQueen, 806, 8, RoomStatus.MAINTENANCE);

        // Floor 9
        saveRoom(duplexSuite, 901, 9, RoomStatus.ACTIVE);
        saveRoom(executiveSuite, 902, 9, RoomStatus.ACTIVE);
        saveRoom(familyRoom, 903, 9, RoomStatus.ACTIVE);
        saveRoom(deluxeKing, 904, 9, RoomStatus.ACTIVE);
        saveRoom(honeymoonSuite, 905, 9, RoomStatus.ACTIVE);
        saveRoom(mountainView, 906, 9, RoomStatus.ACTIVE);

        log.info("DemoDataSeeder: rooms seeded = {}", roomRepository.count());
    }

    // =========================================================
    // BOOKINGS
    // =========================================================

    private void seedBookingData() {

        List<RoomType> roomTypes = roomTypeRepository.findAllByDeleteFlagFalse();
        List<Room> rooms = roomRepository.findAll();

        User alice = requireSeedUser("alice");
        User peter = requireSeedUser("peter");

        // ---------------------------------------------------------
        // A. Dynamic demo bookings based on today
        // ---------------------------------------------------------
        seedDynamicDemoBookings(alice, peter, roomTypes, rooms);

        // ---------------------------------------------------------
        // B. Historical/statistics data
        // Revenue is intentionally spread across many months.
        // 2024: Jan-Dec
        // 2025: Jan-Dec
        // 2026: Jan-Sep (no artificial future revenue after Sep 2026)
        // ---------------------------------------------------------
        seedStatisticsYear(2024, 12, roomTypes, rooms);
        seedStatisticsYear(2025, 12, roomTypes, rooms);
        seedStatisticsYear(2026, 9, roomTypes, rooms);

        log.info("DemoDataSeeder: bookings seeded = {}", bookingRepository.count());
    }

    private void seedDynamicDemoBookings(
            User alice,
            User peter,
            List<RoomType> roomTypes,
            List<Room> rooms
    ) {

        LocalDate today = LocalDate.now();
        LocalDateTime now = LocalDateTime.now();

        RoomType deluxeKing = requireSeedRoomType(roomTypes, "Deluxe King Room");
        RoomType executiveSuite = requireSeedRoomType(roomTypes, "Executive Suite");
        RoomType familyRoom = requireSeedRoomType(roomTypes, "Family Room");
        RoomType oceanViewSuite = requireSeedRoomType(roomTypes, "Ocean View Suite");

        // CONFIRMED: future stay, paid successfully, currently holds room 101.
        saveBookingScenario(
                alice,
                BookingStatus.CONFIRMED,
                today.plusDays(3),
                today.plusDays(5),
                now.minusDays(2),
                deluxeKing,
                requireSeedRoom(rooms, 101),
                1,
                new BigDecimal("110.00")
        );

        // PAID: future stay, payment successful, currently holds room 203.
        saveBookingScenario(
                peter,
                BookingStatus.PAID,
                today.plusDays(4),
                today.plusDays(6),
                now.minusDays(1),
                deluxeKing,
                requireSeedRoom(rooms, 203),
                1,
                new BigDecimal("120.00")
        );

        // CANCELLED: no longer holds room 303.
        saveBookingScenario(
                alice,
                BookingStatus.CANCELLED,
                today.plusDays(3),
                today.plusDays(5),
                now.minusDays(3),
                deluxeKing,
                requireSeedRoom(rooms, 303),
                1,
                new BigDecimal("118.00")
        );

        // CONFIRMED boundary case: checkout is exactly today + 3.
        saveBookingScenario(
                peter,
                BookingStatus.CONFIRMED,
                today.plusDays(1),
                today.plusDays(3),
                now.minusDays(4),
                deluxeKing,
                requireSeedRoom(rooms, 404),
                1,
                new BigDecimal("120.00")
        );

        // PENDING: waiting for payment, expires in 15 minutes, holds room 702.
        saveBookingScenario(
                alice,
                BookingStatus.PENDING,
                today.plusDays(2),
                today.plusDays(4),
                now,
                executiveSuite,
                requireSeedRoom(rooms, 702),
                1,
                new BigDecimal("250.00")
        );

        // EXPIRED: payment window already expired, does not hold room 903.
        saveBookingScenario(
                peter,
                BookingStatus.EXPIRED,
                today.plusDays(3),
                today.plusDays(6),
                now.minusDays(1),
                familyRoom,
                requireSeedRoom(rooms, 903),
                1,
                new BigDecimal("180.75")
        );

        // CHECKED_IN: guest is currently staying, holds room 501.
        saveBookingScenario(
                alice,
                BookingStatus.CHECKED_IN,
                today.minusDays(1),
                today.plusDays(2),
                now.minusDays(7),
                executiveSuite,
                requireSeedRoom(rooms, 501),
                1,
                new BigDecimal("240.00")
        );

        // COMPLETED: finished stay, suitable for booking history demo.
        saveBookingScenario(
                alice,
                BookingStatus.COMPLETED,
                today.minusDays(8),
                today.minusDays(5),
                now.minusDays(15),
                familyRoom,
                requireSeedRoom(rooms, 502),
                1,
                new BigDecimal("175.00")
        );

        // REFUNDED: was paid and then refunded before check-in, no active room hold.
        saveBookingScenario(
                alice,
                BookingStatus.REFUNDED,
                today.plusDays(7),
                today.plusDays(9),
                now.minusDays(5),
                oceanViewSuite,
                requireSeedRoom(rooms, 401),
                1,
                new BigDecimal("230.00")
        );
    }

    /**
     * Create historical bookings for admin statistics.
     * Each month has one COMPLETED/SUCCESS booking so the revenue chart has data.
     * Extra CANCELLED/REFUNDED/EXPIRED records keep booking statistics realistic.
     */
    private void seedStatisticsYear(
            int year,
            int maxMonth,
            List<RoomType> roomTypes,
            List<Room> rooms
    ) {

        String[] revenueRoomTypeNames = {
                "Deluxe King Room",
                "Standard Twin Room",
                "Executive Suite",
                "Family Room",
                "Single Economy Room",
                "Deluxe Queen Room",
                "Family Suite",
                "Accessible Room",
                "Garden View Room",
                "Ocean View Suite",
                "Honeymoon Suite",
                "Mountain View Room"
        };

        int[] revenueRoomNumbers = {
                101, 102, 103, 104, 105, 202,
                204, 206, 302, 401, 405, 605
        };

        List<User> statsUsers = List.of(
                requireSeedUser("alice"),
                requireSeedUser("peter"),
                requireSeedUser("demo01"),
                requireSeedUser("demo02"),
                requireSeedUser("demo03"),
                requireSeedUser("demo04"),
                requireSeedUser("demo05"),
                requireSeedUser("demo06")
        );

        // 1 successful completed booking per month -> revenue chart data.
        for (int month = 1; month <= maxMonth; month++) {

            int index = (month - 1) % revenueRoomTypeNames.length;

            RoomType roomType = requireSeedRoomType(
                    roomTypes,
                    revenueRoomTypeNames[index]
            );

            Room room = requireSeedRoom(
                    rooms,
                    revenueRoomNumbers[index]
            );

            User user = statsUsers.get((month - 1) % statsUsers.size());

            LocalDateTime createdAt = LocalDateTime.of(
                    year,
                    month,
                    3,
                    10,
                    0
            );

            LocalDate checkInDate = LocalDate.of(
                    year,
                    month,
                    10
            );

            LocalDate checkOutDate = checkInDate.plusDays(2 + (month % 3));

            // Historical snapshot price differs slightly from current price.
            BigDecimal snapshotPrice = roomType.getPrice()
                    .subtract(new BigDecimal((month % 3) * 2))
                    .max(new BigDecimal("20.00"));

            saveBookingScenario(
                    user,
                    BookingStatus.COMPLETED,
                    checkInDate,
                    checkOutDate,
                    createdAt,
                    roomType,
                    room,
                    1,
                    snapshotPrice
            );
        }

        // CANCELLED bookings: one per quarter, no successful payment.
        int[] cancelledMonths = {2, 5, 8, 11};

        for (int month : cancelledMonths) {
            if (month > maxMonth) {
                continue;
            }

            RoomType roomType = requireSeedRoomType(roomTypes, "Standard Twin Room");
            Room room = requireSeedRoom(rooms, 304);

            saveBookingScenario(
                    requireSeedUser("alice"),
                    BookingStatus.CANCELLED,
                    LocalDate.of(year, month, 18),
                    LocalDate.of(year, month, 20),
                    LocalDateTime.of(year, month, 6, 9, 0),
                    roomType,
                    room,
                    1,
                    new BigDecimal("72.00")
            );
        }

        // REFUNDED bookings: paid previously, later refunded; excluded from revenue
        // because AdminStatisticsService counts PaymentStatus.SUCCESS only.
        int[] refundedMonths = {4, 10};

        for (int month : refundedMonths) {
            if (month > maxMonth) {
                continue;
            }

            RoomType roomType = requireSeedRoomType(roomTypes, "Family Suite");
            Room room = requireSeedRoom(rooms, 402);

            saveBookingScenario(
                    requireSeedUser("demo02"),
                    BookingStatus.REFUNDED,
                    LocalDate.of(year, month, 20),
                    LocalDate.of(year, month, 23),
                    LocalDateTime.of(year, month, 8, 11, 0),
                    roomType,
                    room,
                    1,
                    new BigDecimal("210.00")
            );
        }

        // One expired booking in June for each year when June is included.
        if (maxMonth >= 6) {
            RoomType roomType = requireSeedRoomType(roomTypes, "Single Economy Room");
            Room room = requireSeedRoom(rooms, 601);

            saveBookingScenario(
                    requireSeedUser("demo03"),
                    BookingStatus.EXPIRED,
                    LocalDate.of(year, 6, 24),
                    LocalDate.of(year, 6, 26),
                    LocalDateTime.of(year, 6, 15, 8, 0),
                    roomType,
                    room,
                    1,
                    new BigDecimal("42.00")
            );
        }
    }

    private Booking saveBookingScenario(
            User user,
            BookingStatus status,
            LocalDate checkInDate,
            LocalDate checkOutDate,
            LocalDateTime createdAt,
            RoomType roomType,
            Room room,
            int quantity,
            BigDecimal snapshotPrice
    ) {

        Booking booking = saveBooking(
                user,
                status,
                checkInDate,
                checkOutDate,
                createdAt
        );

        BookingItem bookingItem = saveBookingItem(
                booking,
                roomType,
                quantity,
                snapshotPrice
        );

        // For demo seed scenarios quantity is 1, therefore exactly one room
        // assignment is created for the booking item.
        saveRoomAssignment(
                booking,
                bookingItem,
                room
        );

        return booking;
    }

    // =========================================================
    // CART
    // =========================================================

    private void seedCartData() {

        List<RoomType> roomTypes = roomTypeRepository.findAllByDeleteFlagFalse();

        // Main demo user starts with an empty cart so the video can demonstrate
        // Add to Cart -> change quantity -> Booking -> clear cart from scratch.
        Cart aliceCart = createCart(requireSeedUser("alice"));

        // Keep one non-empty cart for another account to demonstrate cart UI quickly.
        Cart peterCart = createCart(requireSeedUser("peter"));

        saveCartItem(
                peterCart,
                requireSeedRoomType(roomTypes, "Standard Twin Room"),
                1
        );

        saveCartItem(
                peterCart,
                requireSeedRoomType(roomTypes, "Executive Suite"),
                1
        );

        log.info(
                "DemoDataSeeder: carts seeded. Alice cart={}, Peter cart={}",
                aliceCart.getId(),
                peterCart.getId()
        );
    }

    private Cart createCart(User user) {

        Cart cart = new Cart();
        cart.setUserId(user.getId());
        cart.setDeleteFlag(false);

        Instant now = Instant.now();

        cart.setCreatedBy("admin");
        cart.setCreatedAt(now);
        cart.setUpdatedBy("admin");
        cart.setUpdatedAt(now);

        return cartRepository.save(cart);
    }

    // =========================================================
    // ROOM BOOKING SLOTS
    // =========================================================

    private void seedRoomBookingSlots() {

        List<BookingStatus> holdingStatuses = List.of(
                BookingStatus.PENDING,
                BookingStatus.PAID,
                BookingStatus.CONFIRMED,
                BookingStatus.CHECKED_IN
        );

        List<Booking> bookings =
                bookingRepository.findByDeleteFlagFalseAndStatusIn(holdingStatuses);

        for (Booking booking : bookings) {

            // PENDING whose payment window is already expired must not hold a room.
            if (BookingStatus.PENDING.equals(booking.getStatus())
                    && booking.getExpiresAt() != null
                    && booking.getExpiresAt().isBefore(LocalDateTime.now())) {
                continue;
            }

            List<BookingItem> bookingItems =
                    bookingItemRepository.findByDeleteFlagFalseAndBookingId(
                            booking.getId()
                    );

            for (BookingItem bookingItem : bookingItems) {

                List<RoomAssignment> assignments =
                        roomAssignmentRepository.findByBookingItemIdAndDeleteFlagFalse(
                                bookingItem.getId()
                        );

                for (RoomAssignment assignment : assignments) {
                    createRoomBookingSlots(
                            booking,
                            assignment.getRoomId()
                    );
                }
            }
        }

        log.info(
                "DemoDataSeeder: room booking slots seeded = {}",
                roomBookingSlotRepository.count()
        );
    }

    // =========================================================
    // PAYMENTS
    // =========================================================

    private void seedPayments() {

        List<Booking> bookings = bookingRepository.findByDeleteFlagFalse();

        for (Booking booking : bookings) {

            switch (booking.getStatus()) {

                // Waiting for payment.
                case PENDING -> seedPendingPayment(booking);

                // Payment succeeded but hotel has not confirmed yet.
                case PAID -> seedOnlineSuccessPayment(booking);

                // Confirmed booking must already have a successful payment.
                // Use CASH success here so demo data contains both payment methods.
                case CONFIRMED -> seedCashSuccessPayment(booking);

                // Guest has checked in, payment was successful beforehand.
                case CHECKED_IN -> seedOnlineSuccessPayment(booking);

                // Completed stay, payment was successful.
                case COMPLETED -> seedOnlineSuccessPayment(booking);

                // Payment window expired / payment failed.
                case EXPIRED -> seedFailedPayment(booking);

                // Booking was paid and later refunded.
                case REFUNDED -> seedRefundedPayment(booking);

                // CANCELLED demo records are intentionally unpaid/no-payment.
                case CANCELLED -> {
                }
            }
        }

        log.info("DemoDataSeeder: payments seeded = {}", paymentRepository.count());
    }

    private void seedCashSuccessPayment(Booking booking) {

        LocalDateTime paymentDate = getSeedPaymentDate(booking);

        Payment payment = Payment.builder()
                .bookingId(booking.getId())
                .amount(calculateBookingTotal(booking))
                .paymentMethod("CASH")
                .status(PaymentStatus.SUCCESS)
                .paymentDate(paymentDate)
                .transactionId("CASH-" + UUID.randomUUID())
                .build();

        setPaymentAudit(payment, paymentDate);
        paymentRepository.save(payment);
    }

    private void seedOnlineSuccessPayment(Booking booking) {

        LocalDateTime paymentDate = getSeedPaymentDate(booking);

        Payment payment = Payment.builder()
                .bookingId(booking.getId())
                .amount(calculateBookingTotal(booking))
                .paymentMethod("ONLINE")
                .status(PaymentStatus.SUCCESS)
                .paymentDate(paymentDate)
                .transactionId(UUID.randomUUID().toString())
                .build();

        setPaymentAudit(payment, paymentDate);
        paymentRepository.save(payment);
    }

    private void seedPendingPayment(Booking booking) {

        Payment payment = Payment.builder()
                .bookingId(booking.getId())
                .amount(calculateBookingTotal(booking))
                .paymentMethod("ONLINE")
                .status(PaymentStatus.PENDING)
                .paymentDate(null)
                .transactionId(null)
                .build();

        setPaymentAudit(payment, toLocalDateTime(booking.getCreatedAt()));
        paymentRepository.save(payment);
    }

    private void seedFailedPayment(Booking booking) {

        Payment payment = Payment.builder()
                .bookingId(booking.getId())
                .amount(calculateBookingTotal(booking))
                .paymentMethod("ONLINE")
                .status(PaymentStatus.FAILED)
                .paymentDate(null)
                .transactionId("FAILED-" + UUID.randomUUID())
                .build();

        setPaymentAudit(payment, toLocalDateTime(booking.getCreatedAt()).plusMinutes(15));
        paymentRepository.save(payment);
    }

    private void seedRefundedPayment(Booking booking) {

        LocalDateTime paymentDate = getSeedPaymentDate(booking);

        Payment payment = Payment.builder()
                .bookingId(booking.getId())
                .amount(calculateBookingTotal(booking))
                .paymentMethod("ONLINE")
                .status(PaymentStatus.REFUNDED)
                .paymentDate(paymentDate)
                .transactionId("REFUND-" + UUID.randomUUID())
                .build();

        setPaymentAudit(payment, paymentDate.plusDays(1));
        paymentRepository.save(payment);
    }

    // =========================================================
    // SAVE HELPERS
    // =========================================================

    private Permission savePermission(
            String code,
            String name
    ) {

        Permission permission = new Permission();
        permission.setCode(code);
        permission.setName(name);
        permission.setDescription("");

        return permissionRepository.save(permission);
    }

    private Role saveRole(
            String code,
            String name,
            List<Permission> permissions
    ) {

        Role role = new Role();
        role.setCode(code);
        role.setRoleName(name);
        role.setDescription("");

        List<String> permissionIds = new ArrayList<>();

        for (Permission permission : permissions) {
            permissionIds.add(permission.getId());
        }

        role.setPermissionIds(permissionIds);

        return roleRepository.save(role);
    }

    private void saveUser(
            String username,
            String email,
            String rawPassword,
            String fullName,
            Gender gender,
            LocalDate dateOfBirth,
            String phoneNumber,
            String address,
            String profileUrlLink,
            boolean enabled,
            List<String> roleIds
    ) {

        User user = new User();

        user.setUsername(username);
        user.setPassword(passwordEncoder.encode(rawPassword));
        user.setFullName(fullName);
        user.setGender(gender);
        user.setDateOfBirth(dateOfBirth);
        user.setEmail(email);
        user.setPhoneNumber(phoneNumber);
        user.setAddress(address);
        user.setProfileUrlLink(profileUrlLink);
        user.setRoleIds(roleIds);
        user.setEnabled(enabled);
        user.setDeleteFlag(false);

        Instant now = Instant.now();

        user.setCreatedBy("admin");
        user.setCreatedAt(now);
        user.setUpdatedBy("admin");
        user.setUpdatedAt(now);

        userRepository.save(user);
    }

    private void saveRoomType(
            String roomTypeName,
            Double roomSize,
            String facility,
            Integer maximumPeople,
            BigDecimal price,
            RoomTypeStatus status
    ) {

        RoomType roomType = new RoomType();
        roomType.setRoomTypeName(roomTypeName);
        roomType.setRoomSize(roomSize);
        roomType.setFacility(facility);
        roomType.setMaximumPeople(maximumPeople);
        roomType.setPrice(price);
        roomType.setStatus(status);
        roomType.setDeleteFlag(false);

        Instant now = Instant.now();

        roomType.setCreatedBy("admin");
        roomType.setCreatedAt(now);
        roomType.setUpdatedBy("admin");
        roomType.setUpdatedAt(now);

        roomTypeRepository.save(roomType);
    }

    private void saveRoom(
            RoomType roomType,
            Integer roomNumber,
            Integer floorNumber,
            RoomStatus status
    ) {

        Room room = new Room();
        room.setRoomTypeId(roomType.getId());
        room.setRoomNumber(roomNumber);
        room.setFloorNumber(floorNumber);
        room.setStatus(status);
        room.setDeleteFlag(false);

        Instant now = Instant.now();

        room.setCreatedBy("admin");
        room.setCreatedAt(now);
        room.setUpdatedBy("admin");
        room.setUpdatedAt(now);

        roomRepository.save(room);
    }

    private Booking saveBooking(
            User user,
            BookingStatus status,
            LocalDate checkInDate,
            LocalDate checkOutDate,
            LocalDateTime createdAt
    ) {

        if (!checkOutDate.isAfter(checkInDate)) {
            throw new IllegalArgumentException(
                    "Seed booking checkout must be after check-in"
            );
        }

        Booking booking = new Booking();

        booking.setUserId(user.getId());
        booking.setStatus(status);
        booking.setCheckInDate(checkInDate);
        booking.setCheckOutDate(checkOutDate);

        if (BookingStatus.PENDING.equals(status)) {
            // Current pending demo booking: room hold remains valid for 15 minutes.
            booking.setExpiresAt(LocalDateTime.now().plusMinutes(15));
        } else if (BookingStatus.EXPIRED.equals(status)) {
            // Expired booking must have an expiry time in the past relative to its creation.
            booking.setExpiresAt(createdAt.plusMinutes(15));
        } else {
            booking.setExpiresAt(null);
        }

        booking.setDeleteFlag(false);

        Instant createdInstant = toInstant(createdAt);

        booking.setCreatedBy("admin");
        booking.setCreatedAt(createdInstant);
        booking.setUpdatedBy("admin");
        booking.setUpdatedAt(createdInstant);

        return bookingRepository.save(booking);
    }

    private BookingItem saveBookingItem(
            Booking booking,
            RoomType roomType,
            int quantity,
            BigDecimal price
    ) {

        BookingItem bookingItem = new BookingItem();

        bookingItem.setBookingId(booking.getId());
        bookingItem.setRoomTypeId(roomType.getId());
        bookingItem.setQuantity(quantity);
        bookingItem.setPrice(price);
        bookingItem.setDeleteFlag(false);

        Instant auditTime = booking.getCreatedAt();

        bookingItem.setCreatedBy("admin");
        bookingItem.setCreatedAt(auditTime);
        bookingItem.setUpdatedBy("admin");
        bookingItem.setUpdatedAt(auditTime);

        return bookingItemRepository.save(bookingItem);
    }

    private void saveRoomAssignment(
            Booking booking,
            BookingItem bookingItem,
            Room room
    ) {

        RoomAssignment assignment = new RoomAssignment();
        assignment.setBookingItemId(bookingItem.getId());
        assignment.setRoomId(room.getId());
        assignment.setDeleteFlag(false);

        Instant auditTime = booking.getCreatedAt();

        assignment.setCreatedBy("admin");
        assignment.setCreatedAt(auditTime);
        assignment.setUpdatedBy("admin");
        assignment.setUpdatedAt(auditTime);

        roomAssignmentRepository.save(assignment);
    }

    private void saveCartItem(
            Cart cart,
            RoomType roomType,
            Integer quantity
    ) {

        CartItem item = new CartItem();
        item.setCartId(cart.getId());
        item.setRoomTypeId(roomType.getId());
        item.setQuantity(quantity);
        item.setPrice(roomType.getPrice());
        item.setDeleteFlag(false);

        Instant now = Instant.now();

        item.setCreatedBy("admin");
        item.setCreatedAt(now);
        item.setUpdatedBy("admin");
        item.setUpdatedAt(now);

        cartItemRepository.save(item);
    }

    // =========================================================
    // FIND HELPERS
    // =========================================================

    private User requireSeedUser(String username) {
        return userRepository
                .findByUsernameAndDeleteFlagFalse(username)
                .orElseThrow(() ->
                        new IllegalStateException(
                                "Seeder user not found: " + username
                        )
                );
    }

    private RoomType requireSeedRoomType(
            List<RoomType> roomTypes,
            String roomTypeName
    ) {

        return roomTypes.stream()
                .filter(roomType ->
                        roomTypeName.equals(roomType.getRoomTypeName())
                )
                .findFirst()
                .orElseThrow(() ->
                        new IllegalStateException(
                                "Seeder room type not found: " + roomTypeName
                        )
                );
    }

    private Room requireSeedRoom(
            List<Room> rooms,
            Integer roomNumber
    ) {

        return rooms.stream()
                .filter(room ->
                        roomNumber.equals(room.getRoomNumber())
                )
                .findFirst()
                .orElseThrow(() ->
                        new IllegalStateException(
                                "Seeder room not found: " + roomNumber
                        )
                );
    }

    // =========================================================
    // SLOT / PAYMENT HELPERS
    // =========================================================

    private void createRoomBookingSlots(
            Booking booking,
            String roomId
    ) {

        LocalDate stayDate = booking.getCheckInDate();

        while (stayDate.isBefore(booking.getCheckOutDate())) {

            RoomBookingSlot slot = RoomBookingSlot.builder()
                    .roomId(roomId)
                    .bookingId(booking.getId())
                    .stayDate(stayDate)
                    .createdAt(booking.getCreatedAt())
                    .build();

            roomBookingSlotRepository.save(slot);

            stayDate = stayDate.plusDays(1);
        }
    }

    private LocalDateTime getSeedPaymentDate(Booking booking) {
        return toLocalDateTime(booking.getCreatedAt())
                .plusMinutes(5);
    }

    private BigDecimal calculateBookingTotal(Booking booking) {

        List<BookingItem> bookingItems =
                bookingItemRepository.findByDeleteFlagFalseAndBookingId(
                        booking.getId()
                );

        long numberOfNights = ChronoUnit.DAYS.between(
                booking.getCheckInDate(),
                booking.getCheckOutDate()
        );

        BigDecimal totalAmount = BigDecimal.ZERO;

        for (BookingItem bookingItem : bookingItems) {
            BigDecimal itemAmount = bookingItem.getPrice()
                    .multiply(BigDecimal.valueOf(numberOfNights))
                    .multiply(BigDecimal.valueOf(bookingItem.getQuantity()));

            totalAmount = totalAmount.add(itemAmount);
        }

        return totalAmount;
    }

    private void setPaymentAudit(
            Payment payment,
            LocalDateTime auditDateTime
    ) {

        payment.setDeleteFlag(false);

        Instant auditInstant = toInstant(auditDateTime);

        payment.setCreatedBy("admin");
        payment.setCreatedAt(auditInstant);
        payment.setUpdatedBy("admin");
        payment.setUpdatedAt(auditInstant);
    }

    private Instant toInstant(LocalDateTime localDateTime) {
        return localDateTime
                .atZone(ZoneId.systemDefault())
                .toInstant();
    }

    private LocalDateTime toLocalDateTime(Instant instant) {
        if (instant == null) {
            return LocalDateTime.now();
        }

        return LocalDateTime.ofInstant(
                instant,
                ZoneId.systemDefault()
        );
    }
}
