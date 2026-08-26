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
public class RbacDataSeeder implements ApplicationRunner {

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

        if (userRepository.existsByUsername("admin")) {
            log.info("RbacDataSeeder: RBAC data already present — skip");
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
                        perms.get("USER_ASSIGN_ROLE")
                )
        );

        Role roleEditor = saveRole(
                "EDITOR",
                "Editor",
                List.of(
                        perms.get("USER_VIEW"),
                        perms.get("USER_CREATE"),
                        perms.get("USER_UPDATE")
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

        log.info(
                "RbacDataSeeder: done. " +
                        "Logins: admin/@Admin123, " +
                        "editor/@Editor123, " +
                        "alice/@User123"
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

        saveRoomType(
                "Standard Room",
                25,
                "WiFi, TV, Air Conditioner",
                2,
                new BigDecimal("800000")
        );

        saveRoomType(
                "Deluxe Room",
                35,
                "WiFi, Smart TV, Air Conditioner, Mini Bar, Bathtub",
                2,
                new BigDecimal("1500000")
        );

        saveRoomType(
                "Family Room",
                45,
                "WiFi, Smart TV, Air Conditioner, Mini Bar",
                4,
                new BigDecimal("2000000")
        );

        saveRoomType(
                "Suite Room",
                60,
                "WiFi, Smart TV, Air Conditioner, Mini Bar, Bathtub, Living Room",
                2,
                new BigDecimal("3000000")
        );

        log.info("RoomTypeSeeder: done");
    }

    private RoomType saveRoomType(
            String roomTypeName,
            Integer roomSize,
            String facility,
            Integer maximumPeople,
            BigDecimal price
    ) {

        RoomType roomType = new RoomType();

        roomType.setRoomTypeName(roomTypeName);
        roomType.setRoomSize(Double.valueOf(roomSize));
        roomType.setFacility(facility);
        roomType.setMaximumPeople(maximumPeople);
        roomType.setPrice(price);

        // Soft delete
        roomType.setDeleteFlag(false);

        // Audit
        Instant now = Instant.now();

        roomType.setCreatedBy("admin");
        roomType.setCreatedAt(now);
        roomType.setUpdatedBy("admin");
        roomType.setUpdatedAt(now);

        return roomTypeRepository.save(roomType);
    }

    // =========================================================
    // CART + CART ITEM
    // =========================================================

    private void seedCartData() {

        // ========================
        // Find Alice
        // ========================

        User alice = userRepository
                .findByUsernameAndDeleteFlagFalse("alice")
                .orElse(null);

        if (alice == null) {
            log.warn("CartSeeder: alice not found — skip");
            return;
        }

        // ========================
        // Alice đã có cart active
        // ========================

        if (cartRepository
                .findByUserIdAndDeleteFlagFalse(alice.getId())
                .isPresent()) {

            log.info("CartSeeder: alice already has cart — skip");
            return;
        }

        // ========================
        // Find RoomTypes
        // ========================

        List<RoomType> roomTypes =
                roomTypeRepository.findAllByDeleteFlagFalse();

        if (roomTypes.isEmpty()) {
            log.warn("CartSeeder: no room types found — skip");
            return;
        }

        // ========================
        // Create Cart
        // ========================

        Cart cart = new Cart();

        cart.setUserId(alice.getId());

        cart.setDeleteFlag(false);

        Instant now = Instant.now();

        cart.setCreatedBy("admin");
        cart.setCreatedAt(now);
        cart.setUpdatedBy(null);
        cart.setUpdatedAt(null);

        cart = cartRepository.save(cart);

        // ========================
        // Cart Item 1
        // ========================

        RoomType roomType1 = roomTypes.get(0);

        saveCartItem(
                cart,
                roomType1,
                2
        );

        // ========================
        // Cart Item 2
        // ========================

        if (roomTypes.size() > 1) {

            RoomType roomType2 = roomTypes.get(1);

            saveCartItem(
                    cart,
                    roomType2,
                    1
            );
        }

        log.info(
                "CartSeeder: created cart {} for alice",
                cart.getId()
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
}