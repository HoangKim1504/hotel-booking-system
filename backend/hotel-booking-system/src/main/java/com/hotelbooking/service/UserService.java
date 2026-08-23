package com.hotelbooking.service;

import com.hotelbooking.dto.CreateUserRequest;
import com.hotelbooking.dto.UpdateUserRequest;
import com.hotelbooking.dto.UserResponse;
import com.hotelbooking.exception.ConflictException;
import com.hotelbooking.exception.NotFoundException;
import com.hotelbooking.model.Role;
import com.hotelbooking.model.User;
import com.hotelbooking.repository.RoleRepository;
import com.hotelbooking.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.time.Instant;
import java.util.ArrayList;
import java.util.List;

/**
 * SERVICE — CRUD User + gán / gỡ role.
 *
 * <p>Phân quyền API nằm ở {@code @PreAuthorize} trên Controller — service không
 * hardcode tên role để cho phép/từ chối gọi API.</p>
 */
@Service
@RequiredArgsConstructor
public class UserService {

    private final UserRepository userRepository;
    private final RoleRepository roleRepository;
    private final PermissionLoader permissionLoader;
    private final PasswordEncoder passwordEncoder;

    public List<UserResponse> findAll() {
        // Map toàn bộ user → DTO (kèm role codes)
        return userRepository.findAll().stream()
                .map(this::toResponse)
                .toList();
    }

    public UserResponse findById(String id) {
        return toResponse(requireUser(id));
    }

    public UserResponse create(CreateUserRequest request) {
        // Trùng username / email → 409
        if (userRepository.existsByUsername(request.username())) {
            throw new ConflictException("Username already exists");
        }
        if (userRepository.existsByEmail(request.email())) {
            throw new ConflictException("Email already exists");
        }

        // Resolve role codes (mặc định USER)
        List<String> roleCodes = (request.roleCodes() == null || request.roleCodes().isEmpty())
                ? List.of("USER")
                : request.roleCodes();
        List<String> roleIds = resolveRoleIds(roleCodes);

        // Lưu user (BCrypt)
        User user = new User();
        user.setUsername(request.username());
        user.setEmail(request.email());
        user.setPassword(passwordEncoder.encode(request.password()));
        user.setEnabled(true);
        user.setRoleIds(roleIds);
        Instant now = Instant.now();
        user.setCreatedAt(now);
        user.setUpdatedAt(now);
        return toResponse(userRepository.save(user));
    }

    public UserResponse update(String id, UpdateUserRequest request) {
        User user = requireUser(id);

        // Cập nhật từng field nếu client gửi
        if (request.email() != null && !request.email().isBlank()) {
            userRepository.findByEmail(request.email())
                    .filter(u -> !u.getId().equals(id))
                    .ifPresent(u -> {
                        throw new ConflictException("Email already exists");
                    });
            user.setEmail(request.email());
        }
        if (request.enabled() != null) {
            user.setEnabled(request.enabled());
        }
        if (request.password() != null && !request.password().isBlank()) {
            user.setPassword(passwordEncoder.encode(request.password()));
        }
        user.setUpdatedAt(Instant.now());
        return toResponse(userRepository.save(user));
    }

    public void delete(String id) {
        // Xóa; không thấy → 404
        if (!userRepository.existsById(id)) {
            throw new NotFoundException("User not found: " + id);
        }
        userRepository.deleteById(id);
    }

    public UserResponse assignRole(String userId, String roleCode) {
        User user = requireUser(userId);
        Role role = roleRepository.findByCode(roleCode.toUpperCase())
                .orElseThrow(() -> new NotFoundException("Role not found: " + roleCode));

        // Idempotent: đã có role thì giữ nguyên
        List<String> roleIds = new ArrayList<>(user.getRoleIds());
        if (!roleIds.contains(role.getId())) {
            roleIds.add(role.getId());
            user.setRoleIds(roleIds);
            user.setUpdatedAt(Instant.now());
            user = userRepository.save(user);
        }
        return toResponse(user);
    }

    public UserResponse removeRole(String userId, String roleCode) {
        User user = requireUser(userId);
        Role role = roleRepository.findByCode(roleCode.toUpperCase())
                .orElseThrow(() -> new NotFoundException("Role not found: " + roleCode));

        List<String> roleIds = new ArrayList<>(user.getRoleIds());
        if (roleIds.remove(role.getId())) {
            user.setRoleIds(roleIds);
            user.setUpdatedAt(Instant.now());
            user = userRepository.save(user);
        }
        return toResponse(user);
    }

    private User requireUser(String id) {
        return userRepository.findById(id)
                .orElseThrow(() -> new NotFoundException("User not found: " + id));
    }

    private List<String> resolveRoleIds(List<String> roleCodes) {
        List<String> ids = new ArrayList<>();
        for (String code : roleCodes) {
            Role role = roleRepository.findByCode(code.toUpperCase())
                    .orElseThrow(() -> new NotFoundException("Role not found: " + code));
            ids.add(role.getId());
        }
        return ids;
    }

    private UserResponse toResponse(User user) {
        List<String> roles = permissionLoader.loadRoleCodes(user.getRoleIds());
        return new UserResponse(
                user.getId(),
                user.getUsername(),
                user.getEmail(),
                user.isEnabled(),
                roles,
                user.getCreatedAt(),
                user.getUpdatedAt());
    }

}
