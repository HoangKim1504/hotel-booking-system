package com.hotelbooking.controller;

import com.hotelbooking.dto.*;
import com.hotelbooking.security.AuthUserPrincipal;
import com.hotelbooking.service.AuthService;
import com.hotelbooking.service.SecurityUtils;
import com.hotelbooking.service.UserService;
import io.swagger.v3.oas.annotations.security.SecurityRequirements;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

/**
 * CONTROLLER — Authentication API.
 *
 * <ul>
 *   <li>{@code POST /api/auth/login} — public, trả JWT (identity)</li>
 *   <li>{@code GET /api/auth/me} — Bearer; roles + permissions từ principal (đã load DB)</li>
 * </ul>
 */
@RestController
@RequestMapping("/api/auth")
@RequiredArgsConstructor
@Tag(name = "Auth")
public class AuthController {

    private final AuthService authService;
    private final UserService userService;
    private final SecurityUtils securityUtils;

    /**
     * Public — không yêu cầu Bearer trên Swagger (tắt security scheme toàn cục).
     */
    @PostMapping("/login")
    @SecurityRequirements // empty = không bắt Authorize khi Try it out login
    public LoginResponse login(@Valid @RequestBody LoginRequest request) {
        // Ủy thác AuthService; sai credentials → UnauthorizedException → 401
        return authService.login(request);
    }

    @GetMapping("/me")
    public MeResponse me() {
        // Identity + quyền đã gắn bởi JWT filter (Permission từ Mongo)
        AuthUserPrincipal user = securityUtils.currentUser();
        return new MeResponse(
                user.getId(),
                user.getUsername(),
                user.getEmail(),
                user.getRoleCodes(),
                user.getPermissionCodes());
    }

    @PostMapping("/register")
    @SecurityRequirements // empty = không bắt Authorize khi Try it out login
    public UserResponse register(@Valid @RequestBody CreateUserRequest request) {
        return userService.create(request);
    }

}
