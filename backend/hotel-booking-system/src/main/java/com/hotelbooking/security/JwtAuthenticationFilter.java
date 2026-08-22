package com.hotelbooking.security;

import com.hotelbooking.model.User;
import com.hotelbooking.repository.UserRepository;
import com.hotelbooking.service.PermissionLoader;
import io.jsonwebtoken.Claims;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpHeaders;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;
import java.util.List;
import java.util.Set;

@Slf4j
@Component
@RequiredArgsConstructor
public class JwtAuthenticationFilter extends OncePerRequestFilter {

    private final JwtService jwtService;
    private final UserRepository userRepository;
    private final PermissionLoader permissionLoader;

    @Override
    protected void doFilterInternal(
            HttpServletRequest request,
            HttpServletResponse response,
            FilterChain filterChain) throws ServletException, IOException {

        // Đọc header Authorization; thiếu / không Bearer → đi tiếp chain
        String header = request.getHeader(HttpHeaders.AUTHORIZATION);
        if (header == null || !header.startsWith("Bearer ")) {
            filterChain.doFilter(request, response);
            return;
        }

        String token = header.substring(7).trim();
        try {
            // Verify chữ ký + exp
            Claims claims = jwtService.parseClaims(token);
            String userId = jwtService.userId(claims);

            // Đã có Authentication rồi thì không ghi đè
            if (userId != null && SecurityContextHolder.getContext().getAuthentication() == null) {
                // Load User từ Mongo (không tin quyền trong JWT)
                User user = userRepository.findById(userId).orElse(null);
                if (user == null || !user.isEnabled()) {
                    log.debug("JWT ok nhưng user không tồn tại / disabled: {}", userId);
                    filterChain.doFilter(request, response);
                    return;
                }

                // Nạp Permission codes từ Role → gắn SecurityContext
                Set<String> permissionCodes = permissionLoader.loadPermissionCodes(user.getRoleIds());
                List<String> roleCodes = permissionLoader.loadRoleCodes(user.getRoleIds());
                AuthUserPrincipal principal = new AuthUserPrincipal(
                        user.getId(),
                        user.getUsername(),
                        user.getEmail(),
                        user.isEnabled(),
                        roleCodes,
                        permissionCodes);

                UsernamePasswordAuthenticationToken authentication =
                        new UsernamePasswordAuthenticationToken(principal, null, principal.getAuthorities());
                authentication.setDetails(new WebAuthenticationDetailsSource().buildDetails(request));
                SecurityContextHolder.getContext().setAuthentication(authentication);
            }
        } catch (Exception ex) {
            // Token hỏng / hết hạn → không set context (→ 401)
            log.debug("JWT rejected: {}", ex.getMessage());
        }

        filterChain.doFilter(request, response);
    }

}
