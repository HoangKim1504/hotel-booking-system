package com.hotelbooking.service;

import com.hotelbooking.exception.UnauthorizedException;
import com.hotelbooking.security.AuthUserPrincipal;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;

/**
 * Helper — lấy {@link AuthUserPrincipal} từ SecurityContext (JWT filter đã gắn).
 */
@Component
public class SecurityUtils {

    public AuthUserPrincipal currentUser() {
        // Đọc Authentication đã gắn bởi JWT filter
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();

        // Chưa login / principal không đúng kiểu → 401
        if (auth == null || !(auth.getPrincipal() instanceof AuthUserPrincipal principal)) {
            throw new UnauthorizedException("Unauthorized");
        }
        return principal;
    }

}
