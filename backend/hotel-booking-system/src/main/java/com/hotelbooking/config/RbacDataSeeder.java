package com.hotelbooking.config;

import com.hotelbooking.enums.Gender;
import com.hotelbooking.model.Permission;
import com.hotelbooking.model.Role;
import com.hotelbooking.model.User;
import com.hotelbooking.repository.PermissionRepository;
import com.hotelbooking.repository.RoleRepository;
import com.hotelbooking.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

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
    private final PasswordEncoder passwordEncoder;

    @Override
    public void run(ApplicationArguments args) {
        // Đã seed rồi → bỏ qua (idempotent)
        if (userRepository.existsByUsername("admin")) {
            log.info("RbacDataSeeder: data already present — skip");
            return;
        }

        log.info("RbacDataSeeder: seeding permissions, roles, users…");

        // Permissions
        Map<String, Permission> perms = new LinkedHashMap<>();
        perms.put("USER_VIEW", savePermission("USER_VIEW", "View users"));
        perms.put("USER_CREATE", savePermission("USER_CREATE", "Create user"));
        perms.put("USER_UPDATE", savePermission("USER_UPDATE", "Update user"));
        perms.put("USER_DELETE", savePermission("USER_DELETE", "Delete user"));
        perms.put("USER_ASSIGN_ROLE", savePermission("USER_ASSIGN_ROLE", "Assign or remove role"));

        // Roles (permissionIds)
        Role roleAdmin = saveRole("ADMIN", "Administrator", List.of(
                perms.get("USER_VIEW"),
                perms.get("USER_CREATE"),
                perms.get("USER_UPDATE"),
                perms.get("USER_DELETE"),
                perms.get("USER_ASSIGN_ROLE")));
        Role roleEditor = saveRole("EDITOR", "Editor", List.of(
                perms.get("USER_VIEW"),
                perms.get("USER_CREATE"),
                perms.get("USER_UPDATE")));
        Role roleUser = saveRole("USER", "User", List.of(
                perms.get("USER_VIEW")));

        // Users (BCrypt)
        saveUser(
                "admin",
                "admin@demo.local",
                "admin123",
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
                "editor123",
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
                "user123",
                "Alice Nguyen",
                Gender.FEMALE,
                LocalDate.of(2000, 10, 10),
                "0901000003",
                "Da Nang",
                null,
                List.of(roleUser.getId())
        );

        log.info("RbacDataSeeder: done. Logins: admin/admin123, editor/editor123, alice/user123");
    }

    private Permission savePermission(String code, String name) {
        Permission p = new Permission();
        p.setCode(code);
        p.setName(name);
        p.setDescription("");
        return permissionRepository.save(p);
    }

    private Role saveRole(String code, String name, List<Permission> permissions) {
        Role r = new Role();
        r.setCode(code);
        r.setRoleName(name);
        r.setDescription("");
        List<String> ids = new ArrayList<>();
        for (Permission p : permissions) {
            ids.add(p.getId());
        }
        r.setPermissionIds(ids);
        return roleRepository.save(r);
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
            List<String> roleIds
    ) {
        User u = new User();

        u.setUsername(username);

        // Chỉ encode một lần
        u.setPassword(passwordEncoder.encode(rawPassword));

        u.setFullName(fullName);
        u.setGender(gender);
        u.setDateOfBirth(dateOfBirth);
        u.setEmail(email);
        u.setPhoneNumber(phoneNumber);
        u.setAddress(address);
        u.setProfileUrlLink(profileUrlLink);

        u.setRoleIds(roleIds);

        // User mới mặc định hoạt động
        u.setEnabled(true);

        // Audit
        Instant now = Instant.now();

        u.setDeleteFlag(false);
        u.setCreatedBy("admin");
        u.setCreatedAt(now);
        u.setUpdatedBy("admin");
        u.setUpdatedAt(now);

        userRepository.save(u);
    }
}
