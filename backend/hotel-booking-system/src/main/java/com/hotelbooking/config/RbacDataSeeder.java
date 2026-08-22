package com.hotelbooking.config;

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
        // --- Đã seed rồi → bỏ qua (idempotent) ---
        if (userRepository.existsByUserName("admin")) {
            log.info("RbacDataSeeder: data already present — skip");
            return;
        }

        log.info("RbacDataSeeder: seeding permissions, roles, users…");

        // --- 1) Permissions ---
        Map<String, Permission> perms = new LinkedHashMap<>();
        perms.put("USER_VIEW", savePermission("USER_VIEW", "View users"));
        perms.put("USER_CREATE", savePermission("USER_CREATE", "Create user"));
        perms.put("USER_UPDATE", savePermission("USER_UPDATE", "Update user"));
        perms.put("USER_DELETE", savePermission("USER_DELETE", "Delete user"));
        perms.put("USER_ASSIGN_ROLE", savePermission("USER_ASSIGN_ROLE", "Assign or remove role"));

        // --- 2) Roles (permissionIds) ---
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

        // --- 3) Users (BCrypt) ---
        saveUser("admin", "admin@demo.local", "admin123", List.of(roleAdmin.getId()));
        saveUser("editor", "editor@demo.local", "editor123", List.of(roleEditor.getId()));
        saveUser("alice", "alice@demo.local", "user123", List.of(roleUser.getId()));

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

    private void saveUser(String username, String email, String rawPassword, List<String> roleIds) {
        User u = new User();
        u.setUserName(username);
        u.setEmail(email);
        // --- Chỉ encode một lần; login dùng matches(raw, hashTrongDb) ---
        u.setPassword(passwordEncoder.encode(rawPassword));
        u.setRoleIds(roleIds);
        Instant now = Instant.now();
        u.setCreatedAt(now);
        u.setUpdatedAt(now);
        userRepository.save(u);
    }
}
