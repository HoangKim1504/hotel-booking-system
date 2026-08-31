package com.hotelbooking.config;

import com.hotelbooking.enums.Gender;
import com.hotelbooking.model.*;
import com.hotelbooking.repository.*;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
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

    private final CartRepository cartRepository;
    private final CartItemRepository cartItemRepository;

    private final PasswordEncoder passwordEncoder;

    @Override
    public void run(ApplicationArguments args) {

        // ========================================
        // 1. SEED PERMISSION / ROLE / USER
        // ========================================

        if (userRepository.existsByUsername("admin" )) {
            log.info("RbacDataSeeder: RBAC data already present — skip" );
        } else {
            seedRbacData();
        }

        // ========================================
        // 2. SEED ROOM TYPES
        // ========================================

        seedRoomTypeData();

        // ========================================
        // 3. SEED CART + CART ITEMS
        // ========================================

        seedCartData();
    }

    // =========================================================
    // RBAC
    // =========================================================

    private void seedRbacData() {

        log.info("RbacDataSeeder: seeding permissions, roles, users..." );

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

        // ========================
        // Roles
        // ========================

        Role roleAdmin = saveRole(
                "ADMIN",
                "Administrator",
                List.of(
                        perms.get("USER_VIEW" ),
                        perms.get("USER_CREATE" ),
                        perms.get("USER_UPDATE" ),
                        perms.get("USER_DELETE" ),
                        perms.get("USER_ASSIGN_ROLE" )
                )
        );

        Role roleEditor = saveRole(
                "EDITOR",
                "Editor",
                List.of(
                        perms.get("USER_VIEW" ),
                        perms.get("USER_CREATE" ),
                        perms.get("USER_UPDATE" )
                )
        );

        Role roleUser = saveRole(
                "USER",
                "User",
                List.of(
                        perms.get("USER_VIEW" )
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
            log.info("RoomTypeSeeder: data already present — skip" );
            return;
        }

        log.info("RoomTypeSeeder: seeding room types..." );

        saveRoomType("Deluxe King Room", 35.5, "WiFi, Air Conditioning, Mini Bar, Flat-screen TV, Safe", 2, new BigDecimal("120.00" ));
        saveRoomType("Standard Twin Room", 22.0, "WiFi, Air Conditioning, Flat-screen TV", 2, new BigDecimal("75.50" ));
        saveRoomType("Executive Suite", 55.0, "WiFi, Air Conditioning, Mini Bar, Jacuzzi, Living Area, Ocean View", 4, new BigDecimal("250.00" ));
        saveRoomType("Family Room", 45.0, "WiFi, Air Conditioning, Extra Bed, Flat-screen TV, Balcony", 5, new BigDecimal("180.75" ));
        saveRoomType("Single Economy Room", 16.0, "WiFi, Fan, Shared Bathroom", 1, new BigDecimal("45.00" ));
        saveRoomType("Superior Double Room", 28.0, "WiFi, Air Conditioning, Mini Fridge, Flat-screen TV", 2, new BigDecimal("95.00" ));
        saveRoomType("Presidential Suite", 90.0, "WiFi, Air Conditioning, Private Pool, Butler Service, Jacuzzi, Living Area", 6, new BigDecimal("500.00" ));
        saveRoomType("Junior Suite", 42.0, "WiFi, Air Conditioning, Mini Bar, Sofa, City View", 3, new BigDecimal("160.00" ));
        saveRoomType("Double Room with Balcony", 26.0, "WiFi, Air Conditioning, Balcony, Flat-screen TV", 2, new BigDecimal("88.00" ));
        saveRoomType("Twin Room City View", 24.0, "WiFi, Air Conditioning, City View, Flat-screen TV", 2, new BigDecimal("80.00" ));
        saveRoomType("Deluxe Queen Room", 32.0, "WiFi, Air Conditioning, Mini Bar, Flat-screen TV", 2, new BigDecimal("110.00" ));
        saveRoomType("Family Suite", 60.0, "WiFi, Air Conditioning, Kitchenette, 2 Bedrooms, Living Area", 6, new BigDecimal("220.00" ));
        saveRoomType("Economy Single", 14.0, "WiFi, Fan", 1, new BigDecimal("38.00" ));
        saveRoomType("Budget Double", 18.0, "WiFi, Air Conditioning", 2, new BigDecimal("55.00" ));
        saveRoomType("Honeymoon Suite", 48.0, "WiFi, Air Conditioning, Jacuzzi, Mini Bar, Romantic Decor", 2, new BigDecimal("210.00" ));
        saveRoomType("Accessible Room", 30.0, "WiFi, Air Conditioning, Wheelchair Access, Grab Bars", 2, new BigDecimal("90.00" ));
        saveRoomType("Penthouse Suite", 120.0, "WiFi, Air Conditioning, Private Terrace, Jacuzzi, Bar, Panoramic View", 8, new BigDecimal("650.00" ));
        saveRoomType("Standard Single Room", 15.0, "WiFi, Air Conditioning, Desk", 1, new BigDecimal("50.00" ));
        saveRoomType("Deluxe Twin Room", 30.0, "WiFi, Air Conditioning, Mini Bar, Flat-screen TV", 2, new BigDecimal("105.00" ));
        saveRoomType("Garden View Room", 27.0, "WiFi, Air Conditioning, Garden View, Flat-screen TV", 2, new BigDecimal("92.00" ));
        saveRoomType("Ocean View Suite", 50.0, "WiFi, Air Conditioning, Mini Bar, Ocean View, Balcony", 3, new BigDecimal("240.00" ));
        saveRoomType("Studio Room", 25.0, "WiFi, Air Conditioning, Kitchenette, Flat-screen TV", 2, new BigDecimal("98.00" ));
        saveRoomType("Loft Suite", 65.0, "WiFi, Air Conditioning, Mini Bar, Mezzanine, City View", 4, new BigDecimal("270.00" ));
        saveRoomType("Connecting Family Room", 55.0, "WiFi, Air Conditioning, 2 Connecting Rooms, Flat-screen TV", 5, new BigDecimal("195.00" ));
        saveRoomType("Poolside Room", 33.0, "WiFi, Air Conditioning, Pool Access, Flat-screen TV", 2, new BigDecimal("115.00" ));
        saveRoomType("Business Room", 29.0, "WiFi, Air Conditioning, Work Desk, Flat-screen TV, Coffee Machine", 2, new BigDecimal("100.00" ));
        saveRoomType("Royal Suite", 100.0, "WiFi, Air Conditioning, Private Pool, Butler Service, Living Area, Dining Area", 6, new BigDecimal("580.00" ));
        saveRoomType("Cozy Single Room", 13.0, "WiFi, Fan, Desk", 1, new BigDecimal("35.00" ));
        saveRoomType("Mountain View Room", 31.0, "WiFi, Air Conditioning, Mountain View, Flat-screen TV", 2, new BigDecimal("102.00" ));
        saveRoomType("Duplex Suite", 70.0, "WiFi, Air Conditioning, 2 Floors, Mini Bar, Living Area", 4, new BigDecimal("300.00" ));
        saveRoomType("Classic Double Room", 23.0, "WiFi, Air Conditioning, Flat-screen TV", 2, new BigDecimal("78.00" ));
        saveRoomType("VIP Suite", 85.0, "WiFi, Air Conditioning, Private Bar, Jacuzzi, Butler Service", 5, new BigDecimal("450.00" ));
        saveRoomType("Compact Twin Room", 19.0, "WiFi, Air Conditioning, Flat-screen TV", 2, new BigDecimal("60.00" ));
        saveRoomType("Skyline Suite", 58.0, "WiFi, Air Conditioning, Mini Bar, Panoramic City View, Balcony", 3, new BigDecimal("260.00" ));
        saveRoomType("Traditional Family Room", 47.0, "WiFi, Air Conditioning, Extra Bed, Flat-screen TV", 4, new BigDecimal("165.00" ));

        log.info("RoomTypeSeeder: done" );
    }

    private void saveRoomType(
            String roomTypeName,
            Double roomSize,
            String facility,
            Integer maximumPeople,
            BigDecimal price
    ) {

        RoomType roomType = new RoomType();

        roomType.setRoomTypeName(roomTypeName);
        roomType.setRoomSize(roomSize);
        roomType.setFacility(facility);
        roomType.setMaximumPeople(maximumPeople);
        roomType.setPrice(price);

        // Soft delete
        roomType.setDeleteFlag(false);

        // Audit
        Instant now = Instant.now();

        roomType.setCreatedBy("admin" );
        roomType.setCreatedAt(now);
        roomType.setUpdatedBy("admin" );
        roomType.setUpdatedAt(now);

        roomTypeRepository.save(roomType);
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
            log.warn("CartSeeder: no room types found — skip" );
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

        cart.setCreatedBy("admin" );
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

        item.setCreatedBy("admin" );
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
        permission.setDescription("" );

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
        role.setDescription("" );

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

        user.setCreatedBy("admin" );
        user.setCreatedAt(now);
        user.setUpdatedBy("admin" );
        user.setUpdatedAt(now);

        userRepository.save(user);
    }
}