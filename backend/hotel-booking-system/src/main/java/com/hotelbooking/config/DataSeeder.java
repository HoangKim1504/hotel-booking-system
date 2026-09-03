package com.hotelbooking.config;

import com.hotelbooking.enums.BookingStatus;
import com.hotelbooking.enums.Gender;
import com.hotelbooking.enums.RoomStatus;
import com.hotelbooking.enums.RoomTypeStatus;
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
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

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

    private final PasswordEncoder passwordEncoder;

    @Override
    public void run(@NonNull ApplicationArguments args) {

        // 1. RBAC
        if (userRepository.existsByUsername("admin")) {
            log.info("RbacDataSeeder: RBAC data already present — skip");
        } else {
            seedRbacData();
        }

        // 2. ROOM TYPES
        seedRoomTypeData();

        // 3. ROOMS
        seedRoomData();

        // 4. BOOKING + BOOKING ITEMS + ROOM ASSIGNMENTS
        seedSearchBookingData();

        // 5. CART + CART ITEMS
        seedCartData();
    }

    // =========================================================
    // RBAC
    // =========================================================

    private void seedRbacData() {

        log.info("RbacDataSeeder: seeding permissions, roles, users...");

        // ========================
        // Permissions
        // ========================

        Map<String, Permission> perms = new LinkedHashMap<>();

        perms.put(
                "USER_VIEW",
                savePermission(
                        "USER_VIEW",
                        "View users"
                )
        );

        perms.put(
                "USER_CREATE",
                savePermission(
                        "USER_CREATE",
                        "Create user"
                )
        );

        perms.put(
                "USER_UPDATE",
                savePermission(
                        "USER_UPDATE",
                        "Update user"
                )
        );

        perms.put(
                "USER_DELETE",
                savePermission(
                        "USER_DELETE",
                        "Delete user"
                )
        );

        perms.put(
                "USER_ASSIGN_ROLE",
                savePermission(
                        "USER_ASSIGN_ROLE",
                        "Assign or remove role"
                )
        );

        perms.put(
                "ADMIN_VIEW",
                savePermission(
                        "ADMIN_VIEW",
                        "Admin can view"
                )
        );

        // ========================
        // Roles
        // ========================

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
                List.of(
                        perms.get("USER_VIEW")
                )
        );

        // ========================
        // Users
        // ========================

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
                List.of(roleUser.getId())
        );

        saveUser(
                "peter",
                "peter@demo.local",
                "@User456",
                "Peter Tran",
                Gender.MALE,
                LocalDate.of(2001, 8, 5),
                "0902000015",
                "Ha Noi",
                null,
                List.of(roleUser.getId())
        );

        log.info(
                "RbacDataSeeder: done. " +
                        "Logins: admin/@Admin123, " +
                        "editor/@Editor123, " +
                        "alice/@User123, " +
                        "peter/@User456"
        );
    }

    // =========================================================
    // ROOM TYPE
    // =========================================================

    private void seedRoomTypeData() {

        // Đã có RoomType chưa bị soft delete → skip
        List<RoomType> existingRoomTypes =
                roomTypeRepository.findAllByDeleteFlagFalse();

        if (!existingRoomTypes.isEmpty()) {
            log.info("RoomTypeSeeder: data already present — skip");
            return;
        }

        log.info("RoomTypeSeeder: seeding room types...");

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

        log.info("RoomTypeSeeder: done");
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

        // Soft delete
        roomType.setDeleteFlag(false);

        // Audit
        Instant now = Instant.now();

        roomType.setCreatedBy("admin");
        roomType.setCreatedAt(now);
        roomType.setUpdatedBy("admin");
        roomType.setUpdatedAt(now);

        roomTypeRepository.save(roomType);
    }

    // =========================================================
    // ROOM
    // =========================================================
    private void seedRoomData() {

        if (roomRepository.count() > 0) {
            log.info("RoomSeeder: data already present — skip");
            return;
        }

        List<RoomType> roomTypes =
                roomTypeRepository.findAllByDeleteFlagFalse();

        if (roomTypes.isEmpty()) {
            log.warn("RoomSeeder: no room types found — skip");
            return;
        }

        log.info("RoomSeeder: seeding rooms...");

        RoomType deluxeKing =
                requireSeedRoomType(roomTypes, "Deluxe King Room");
        RoomType standardTwin =
                requireSeedRoomType(roomTypes, "Standard Twin Room");
        RoomType executiveSuite =
                requireSeedRoomType(roomTypes, "Executive Suite");
        RoomType familyRoom =
                requireSeedRoomType(roomTypes, "Family Room");
        RoomType singleEconomy =
                requireSeedRoomType(roomTypes, "Single Economy Room");
        RoomType deluxeQueen =
                requireSeedRoomType(roomTypes, "Deluxe Queen Room");
        RoomType familySuite =
                requireSeedRoomType(roomTypes, "Family Suite");
        RoomType honeymoonSuite =
                requireSeedRoomType(roomTypes, "Honeymoon Suite");
        RoomType accessibleRoom =
                requireSeedRoomType(roomTypes, "Accessible Room");
        RoomType gardenView =
                requireSeedRoomType(roomTypes, "Garden View Room");
        RoomType oceanViewSuite =
                requireSeedRoomType(roomTypes, "Ocean View Suite");
        RoomType studioRoom =
                requireSeedRoomType(roomTypes, "Studio Room");
        RoomType poolsideRoom =
                requireSeedRoomType(roomTypes, "Poolside Room");
        RoomType classicDouble =
                requireSeedRoomType(roomTypes, "Classic Double Room");
        RoomType mountainView =
                requireSeedRoomType(roomTypes, "Mountain View Room");
        RoomType duplexSuite =
                requireSeedRoomType(roomTypes, "Duplex Suite");

        // Tầng 1 - 6 phòng, nhiều loại phòng hỗn hợp
        saveRoom(deluxeKing, "101", 1, RoomStatus.ACTIVE);
        saveRoom(standardTwin, "102", 1, RoomStatus.ACTIVE);
        saveRoom(executiveSuite, "103", 1, RoomStatus.ACTIVE);
        saveRoom(familyRoom, "104", 1, RoomStatus.ACTIVE);
        saveRoom(singleEconomy, "105", 1, RoomStatus.ACTIVE);
        saveRoom(deluxeQueen, "106", 1, RoomStatus.MAINTENANCE);

        // Tầng 2 - 6 phòng, nhiều loại phòng hỗn hợp
        saveRoom(standardTwin, "201", 2, RoomStatus.ACTIVE);
        saveRoom(deluxeQueen, "202", 2, RoomStatus.ACTIVE);
        saveRoom(deluxeKing, "203", 2, RoomStatus.ACTIVE);
        saveRoom(familySuite, "204", 2, RoomStatus.ACTIVE);
        saveRoom(executiveSuite, "205", 2, RoomStatus.ACTIVE);
        saveRoom(accessibleRoom, "206", 2, RoomStatus.ACTIVE);

        // Tầng 3 - 6 phòng, nhiều loại phòng hỗn hợp
        saveRoom(familyRoom, "301", 3, RoomStatus.ACTIVE);
        saveRoom(gardenView, "302", 3, RoomStatus.ACTIVE);
        saveRoom(deluxeKing, "303", 3, RoomStatus.ACTIVE);
        saveRoom(standardTwin, "304", 3, RoomStatus.ACTIVE);
        saveRoom(executiveSuite, "305", 3, RoomStatus.ACTIVE);
        saveRoom(studioRoom, "306", 3, RoomStatus.MAINTENANCE);

        // Tầng 4 - 6 phòng, nhiều loại phòng hỗn hợp
        saveRoom(oceanViewSuite, "401", 4, RoomStatus.ACTIVE);
        saveRoom(familySuite, "402", 4, RoomStatus.ACTIVE);
        saveRoom(standardTwin, "403", 4, RoomStatus.ACTIVE);
        saveRoom(deluxeKing, "404", 4, RoomStatus.ACTIVE);
        saveRoom(honeymoonSuite, "405", 4, RoomStatus.ACTIVE);
        saveRoom(accessibleRoom, "406", 4, RoomStatus.OUT_OF_SERVICE);

        // Tầng 5 - 6 phòng, nhiều loại phòng hỗn hợp
        saveRoom(executiveSuite, "501", 5, RoomStatus.ACTIVE);
        saveRoom(familyRoom, "502", 5, RoomStatus.ACTIVE);
        saveRoom(deluxeQueen, "503", 5, RoomStatus.ACTIVE);
        saveRoom(gardenView, "504", 5, RoomStatus.ACTIVE);
        saveRoom(deluxeKing, "505", 5, RoomStatus.ACTIVE);
        saveRoom(classicDouble, "506", 5, RoomStatus.MAINTENANCE);

        // Tầng 6 - 6 phòng, nhiều loại phòng hỗn hợp
        saveRoom(singleEconomy, "601", 6, RoomStatus.ACTIVE);
        saveRoom(standardTwin, "602", 6, RoomStatus.ACTIVE);
        saveRoom(familyRoom, "603", 6, RoomStatus.ACTIVE);
        saveRoom(executiveSuite, "604", 6, RoomStatus.ACTIVE);
        saveRoom(mountainView, "605", 6, RoomStatus.ACTIVE);
        saveRoom(studioRoom, "606", 6, RoomStatus.ACTIVE);

        // Tầng 7 - 6 phòng, nhiều loại phòng hỗn hợp
        saveRoom(deluxeKing, "701", 7, RoomStatus.ACTIVE);
        saveRoom(executiveSuite, "702", 7, RoomStatus.ACTIVE);
        saveRoom(familySuite, "703", 7, RoomStatus.ACTIVE);
        saveRoom(poolsideRoom, "704", 7, RoomStatus.ACTIVE);
        saveRoom(classicDouble, "705", 7, RoomStatus.ACTIVE);
        saveRoom(accessibleRoom, "706", 7, RoomStatus.OUT_OF_SERVICE);

        // Tầng 8 - 6 phòng, nhiều loại phòng hỗn hợp
        saveRoom(familyRoom, "801", 8, RoomStatus.ACTIVE);
        saveRoom(standardTwin, "802", 8, RoomStatus.ACTIVE);
        saveRoom(deluxeKing, "803", 8, RoomStatus.ACTIVE);
        saveRoom(executiveSuite, "804", 8, RoomStatus.ACTIVE);
        saveRoom(oceanViewSuite, "805", 8, RoomStatus.ACTIVE);
        saveRoom(deluxeQueen, "806", 8, RoomStatus.MAINTENANCE);

        // Tầng 9 - 6 phòng, nhiều loại phòng hỗn hợp
        saveRoom(duplexSuite, "901", 9, RoomStatus.ACTIVE);
        saveRoom(executiveSuite, "902", 9, RoomStatus.ACTIVE);
        saveRoom(familyRoom, "903", 9, RoomStatus.ACTIVE);
        saveRoom(deluxeKing, "904", 9, RoomStatus.ACTIVE);
        saveRoom(honeymoonSuite, "905", 9, RoomStatus.ACTIVE);
        saveRoom(mountainView, "906", 9, RoomStatus.ACTIVE);

        log.info("RoomSeeder: done");
    }

    // =========================================================
    // BOOKING
    // =========================================================
    private void seedSearchBookingData() {

        if (bookingRepository.count() > 0) {
            log.info("BookingSeeder: data already present — skip");
            return;
        }

        User alice = userRepository
                .findByUsernameAndDeleteFlagFalse("alice")
                .orElseThrow();

        User peter = userRepository
                .findByUsernameAndDeleteFlagFalse("peter")
                .orElseThrow();

        List<RoomType> roomTypes =
                roomTypeRepository.findAllByDeleteFlagFalse();

        List<Room> rooms = roomRepository.findAll();

        RoomType deluxeKing =
                requireSeedRoomType(roomTypes, "Deluxe King Room");

        RoomType executiveSuite =
                requireSeedRoomType(roomTypes, "Executive Suite");

        RoomType familyRoom =
                requireSeedRoomType(roomTypes, "Family Room");


        // =====================================================
        // CASE 1
        // CONFIRMED + overlap
        // 10/09 -> 12/09
        // Room 101 (tầng 1) phải bị occupied
        // =====================================================

        Booking booking1 =
                saveBooking(alice, BookingStatus.CONFIRMED);

        BookingItem bookingItem1 =
                saveBookingItem(
                        booking1,
                        deluxeKing,
                        LocalDate.of(2026, 9, 10),
                        LocalDate.of(2026, 9, 12),
                        1
                );

        saveRoomAssignment(
                bookingItem1,
                requireSeedRoom(rooms, "101")
        );


        // =====================================================
        // CASE 2
        // PAID + overlap
        // 11/09 -> 13/09
        // Room 203 (tầng 2) cũng phải bị occupied
        // =====================================================

        Booking booking2 =
                saveBooking(peter, BookingStatus.PAID);

        BookingItem bookingItem2 =
                saveBookingItem(
                        booking2,
                        deluxeKing,
                        LocalDate.of(2026, 9, 11),
                        LocalDate.of(2026, 9, 13),
                        1
                );

        saveRoomAssignment(
                bookingItem2,
                requireSeedRoom(rooms, "203")
        );


        // =====================================================
        // CASE 3
        // CANCELLED + overlap
        // Room 303 (tầng 3) KHÔNG được occupied
        // =====================================================

        Booking booking3 =
                saveBooking(alice, BookingStatus.CANCELLED);

        BookingItem bookingItem3 =
                saveBookingItem(
                        booking3,
                        deluxeKing,
                        LocalDate.of(2026, 9, 10),
                        LocalDate.of(2026, 9, 12),
                        1
                );

        saveRoomAssignment(
                bookingItem3,
                requireSeedRoom(rooms, "303")
        );


        // =====================================================
        // CASE 4
        // CONFIRMED nhưng checkout đúng ngày search check-in
        //
        // Booking: 08/09 -> 10/09
        // Search : 10/09 -> 12/09
        //
        // KHÔNG overlap vì đang dùng < và >
        // Room 404 (tầng 4) vẫn available
        // =====================================================

        Booking booking4 =
                saveBooking(peter, BookingStatus.CONFIRMED);

        BookingItem bookingItem4 =
                saveBookingItem(
                        booking4,
                        deluxeKing,
                        LocalDate.of(2026, 9, 8),
                        LocalDate.of(2026, 9, 10),
                        1
                );

        saveRoomAssignment(
                bookingItem4,
                requireSeedRoom(rooms, "404")
        );


        // =====================================================
        // CASE 5
        // PENDING + overlap Executive Suite
        // Room 702 (tầng 7) bị occupied
        // =====================================================

        Booking booking5 =
                saveBooking(alice, BookingStatus.PENDING);

        BookingItem bookingItem5 =
                saveBookingItem(
                        booking5,
                        executiveSuite,
                        LocalDate.of(2026, 9, 9),
                        LocalDate.of(2026, 9, 11),
                        1
                );

        saveRoomAssignment(
                bookingItem5,
                requireSeedRoom(rooms, "702")
        );


        // =====================================================
        // CASE 6
        // EXPIRED + overlap Family Room
        // Room 903 (tầng 9) KHÔNG được occupied
        // =====================================================

        Booking booking6 =
                saveBooking(peter, BookingStatus.EXPIRED);

        BookingItem bookingItem6 =
                saveBookingItem(
                        booking6,
                        familyRoom,
                        LocalDate.of(2026, 9, 10),
                        LocalDate.of(2026, 9, 13),
                        1
                );

        saveRoomAssignment(
                bookingItem6,
                requireSeedRoom(rooms, "903")
        );

        log.info("BookingSeeder: search test data done");
    }

    // =========================================================
    // CART + CART ITEM
    // =========================================================

    private void seedCartData() {

        // ========================
        // Find RoomTypes
        // ========================
        List<RoomType> roomTypes =
                roomTypeRepository.findAllByDeleteFlagFalse();

        if (roomTypes.isEmpty()) {
            log.warn("CartSeeder: no room types found — skip");
            return;
        }

        // Alice:
        // RoomType 1 x2
        // RoomType 2 x1
        seedCartForUser(
                "alice",
                roomTypes,
                List.of(2, 1)
        );

        // Peter:
        // RoomType 1 x5
        // RoomType 2 x1
        // RoomType 3 x7
        seedCartForUser(
                "peter",
                roomTypes,
                List.of(5, 1, 7)
        );
    }

    private void seedCartForUser(
            String username,
            List<RoomType> roomTypes,
            List<Integer> quantities
    ) {

        // ========================
        // Find User
        // ========================
        User user = userRepository
                .findByUsernameAndDeleteFlagFalse(username)
                .orElse(null);

        if (user == null) {
            log.warn(
                    "CartSeeder: user {} not found — skip",
                    username
            );
            return;
        }

        // ========================
        // User đã có cart active
        // ========================
        if (cartRepository
                .findByUserIdAndDeleteFlagFalse(user.getId())
                .isPresent()) {

            log.info(
                    "CartSeeder: {} already has cart — skip",
                    username
            );

            return;
        }

        // ========================
        // Create Cart
        // ========================
        Cart cart = new Cart();

        cart.setUserId(user.getId());
        cart.setDeleteFlag(false);

        Instant now = Instant.now();

        cart.setCreatedBy("admin");
        cart.setCreatedAt(now);
        cart.setUpdatedBy(null);
        cart.setUpdatedAt(null);

        cart = cartRepository.save(cart);

        // ========================
        // Create Cart Items
        // ========================

        int itemCount = Math.min(
                roomTypes.size(),
                quantities.size()
        );

        for (int i = 0; i < itemCount; i++) {

            RoomType roomType = roomTypes.get(i);
            Integer quantity = quantities.get(i);

            saveCartItem(
                    cart,
                    roomType,
                    quantity
            );
        }

        log.info(
                "CartSeeder: created cart {} for {} with {} items",
                cart.getId(),
                username,
                itemCount
        );
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

        // Giá lấy trực tiếp từ RoomType
        item.setPrice(roomType.getPrice());

        // Soft delete
        item.setDeleteFlag(false);

        // Audit
        Instant now = Instant.now();

        item.setCreatedBy("admin");
        item.setCreatedAt(now);
        item.setUpdatedBy(null);
        item.setUpdatedAt(null);

        cartItemRepository.save(item);
    }

    // =========================================================
    // PERMISSION
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

    // =========================================================
    // ROLE
    // =========================================================

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

    // =========================================================
    // USER
    // =========================================================

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
            List<String> roleIds
    ) {

        User user = new User();

        user.setUsername(username);

        // BCrypt - encode một lần
        user.setPassword(
                passwordEncoder.encode(rawPassword)
        );

        user.setFullName(fullName);
        user.setGender(gender);
        user.setDateOfBirth(dateOfBirth);

        user.setEmail(email);
        user.setPhoneNumber(phoneNumber);
        user.setAddress(address);
        user.setProfileUrlLink(profileUrlLink);

        user.setRoleIds(roleIds);

        // Account mặc định active
        user.setEnabled(true);

        // Soft delete
        user.setDeleteFlag(false);

        // Audit
        Instant now = Instant.now();

        user.setCreatedBy("admin");
        user.setCreatedAt(now);
        user.setUpdatedBy("admin");
        user.setUpdatedAt(now);

        userRepository.save(user);
    }

    private void saveRoom(
            RoomType roomType,
            String roomNumber,
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
            BookingStatus status
    ) {
        Booking booking = new Booking();

        booking.setUserId(user.getId());
        booking.setStatus(status);

        booking.setDeleteFlag(false);

        Instant now = Instant.now();

        booking.setCreatedBy("admin");
        booking.setCreatedAt(now);
        booking.setUpdatedBy("admin");
        booking.setUpdatedAt(now);

        return bookingRepository.save(booking);
    }

    private BookingItem saveBookingItem(
            Booking booking,
            RoomType roomType,
            LocalDate checkInDate,
            LocalDate checkOutDate,
            int quantity
    ) {
        BookingItem bookingItem = new BookingItem();

        bookingItem.setBookingId(booking.getId());
        bookingItem.setRoomTypeId(roomType.getId());

        bookingItem.setCheckInDate(checkInDate);
        bookingItem.setCheckOutDate(checkOutDate);

        bookingItem.setQuantity(quantity);

        bookingItem.setDeleteFlag(false);

        Instant now = Instant.now();

        bookingItem.setCreatedBy("admin");
        bookingItem.setCreatedAt(now);
        bookingItem.setUpdatedBy("admin");
        bookingItem.setUpdatedAt(now);

        return bookingItemRepository.save(bookingItem);
    }

    private void saveRoomAssignment(
            BookingItem bookingItem,
            Room room
    ) {
        RoomAssignment assignment = new RoomAssignment();

        assignment.setBookingItemId(bookingItem.getId());
        assignment.setRoomId(room.getId());

        assignment.setDeleteFlag(false);

        Instant now = Instant.now();

        assignment.setCreatedBy("admin");
        assignment.setCreatedAt(now);
        assignment.setUpdatedBy("admin");
        assignment.setUpdatedAt(now);

        roomAssignmentRepository.save(assignment);
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
            String roomNumber
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

}