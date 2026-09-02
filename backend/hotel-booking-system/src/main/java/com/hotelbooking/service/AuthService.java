package com.hotelbooking.service;

import com.hotelbooking.dto.LoginRequest;
import com.hotelbooking.dto.LoginResponse;
import com.hotelbooking.exception.UnauthorizedException;
import com.hotelbooking.model.User;
import com.hotelbooking.security.JwtService;
import com.hotelbooking.validator.EntityValidator;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

/**
 * SERVICE — đăng nhập: BCrypt → JWT (chỉ identity).
 *
 * <p>Permission <b>không</b> đưa vào token; filter sẽ nạp từ Mongo mỗi request.</p>
 */
@Service
@RequiredArgsConstructor
public class AuthService {

    private final EntityValidator entityValidator;
    private final PasswordEncoder passwordEncoder;
    private final JwtService jwtService;

    public LoginResponse login(LoginRequest request) {
        // Tìm user theo username
        User user = entityValidator.requireUserLogin(request.username());

        // Tài khoản bị khóa
        if (!user.isEnabled()) {
            throw new UnauthorizedException("User disabled" );
        }

        // So khớp mật khẩu plain với BCrypt hash
        if (!passwordEncoder.matches(request.password(), user.getPassword())) {
            throw new UnauthorizedException("Invalid username or password" );
        }

        // Phát hành JWT (sub=id, username) — không nhét permissions
        String token = jwtService.generateToken(user.getId(), user.getUsername());
        return new LoginResponse(token);
    }

}

