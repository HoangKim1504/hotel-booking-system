package com.hotelbooking.config;

import com.hotelbooking.security.JwtAuthenticationFilter;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.AuthenticationEntryPoint;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.access.AccessDeniedHandler;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.CorsConfigurationSource;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;

import java.io.IOException;
import java.util.List;

@Configuration
@EnableWebSecurity
@EnableMethodSecurity
@RequiredArgsConstructor
public class SecurityConfig {

    private final JwtAuthenticationFilter jwtAuthenticationFilter;

    @Bean
    PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

    @Bean
    SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        http
                // Global CORS configuration
                .cors(cors -> cors.configurationSource(corsConfigurationSource()))

                // API JWT không dùng cookie form → tắt CSRF
                .csrf(csrf -> csrf.disable())

                // Stateless: không tạo HTTP session
                .sessionManagement(sm
                        -> sm.sessionCreationPolicy(SessionCreationPolicy.STATELESS))

                // URL: login + Swagger/OpenAPI public; còn lại cần JWT (@PreAuthorize)
                .authorizeHttpRequests(auth -> auth
                        // Public
                        .requestMatchers(
                                HttpMethod.POST,
                                "/api/auth/login",
                                "/api/auth/register"
                        ).permitAll()

                        // Public các chức năng GET cơ bản
                        .requestMatchers(
                                HttpMethod.GET,
                                "/api/room-types/**"
                        ).permitAll()

                        // Public -> có thể thấy Spring trả đúng lỗi gốc
                        .requestMatchers("/error").permitAll()

                        // User management
                        .requestMatchers(
                                HttpMethod.POST,
                                "/api/users"
                        ).hasAuthority("USER_CREATE")

                        // Cart management
                        .requestMatchers("/api/cart/**")
                        .authenticated()

                        // Room type management
                        .requestMatchers(
                                HttpMethod.GET,
                                "/api/admin/room-types",
                                "/api/admin/room-types/*",
                                "/api/admin/room-types/search"
                        ).hasAuthority("ADMIN_VIEW")

                        // Limit các chức năng liên quan update Room Type và Room
                        // - Insert một Room Type/ Room mới
                        // - Update Room Type/ Room có sẵn
                        // - Soft-delete Room Type/ Room có sẵn
                        .requestMatchers(
                                HttpMethod.POST,
                                "/api/room-types",
                                "/api/admin/rooms"
                        ).hasAuthority("USER_CREATE")
                        .requestMatchers(
                                HttpMethod.PUT,
                                "/api/room-types/*",
                                "/api/admin/rooms/*"
                        ).hasAuthority("USER_UPDATE")
                        .requestMatchers(
                                HttpMethod.DELETE,
                                "/api/room-types/*",
                                "/api/admin/rooms/*"
                        ).hasAuthority("USER_DELETE")

                        // Room management
                        .requestMatchers(
                                HttpMethod.GET,
                                "/api/admin/rooms",
                                "/api/admin/rooms/*",
                                "/api/admin/rooms/search"
                        ).hasAuthority("ADMIN_VIEW")

                        // Booking management for User
                        // View bookings
                        .requestMatchers(
                                HttpMethod.GET,
                                "/api/bookings",
                                "/api/bookings/*"
                        ).hasAuthority("USER_VIEW")
                        // Create a Booking
                        .requestMatchers(
                                HttpMethod.POST,
                                "/api/bookings"
                        ).authenticated()

                        // Booking management for Admin
                        // View bookings
                        .requestMatchers(
                                HttpMethod.GET,
                                "/api/admin/bookings",
                                "/api/admin/bookings/*"
                        ).hasAuthority("ADMIN_VIEW")
                        .requestMatchers(
                                HttpMethod.POST,
                                "/api/admin/bookings"
                        ).hasAuthority("USER_CREATE")

                        // springdoc: UI + spec JSON (để Try it out không bị 401)
                        .requestMatchers(
                                "/swagger-ui.html",
                                "/swagger-ui/**",
                                "/v3/api-docs",
                                "/v3/api-docs/**").permitAll()
                        .anyRequest().authenticated())

                // JSON 401 / 403 (không redirect HTML /login)
                .exceptionHandling(ex -> ex
                        .authenticationEntryPoint(json401EntryPoint())
                        .accessDeniedHandler(json403Handler()))

                // JWT filter trước filter form-login mặc định
                .addFilterBefore(jwtAuthenticationFilter, UsernamePasswordAuthenticationFilter.class);

        return http.build();
    }

    @Bean
    CorsConfigurationSource corsConfigurationSource() {
        CorsConfiguration configuration = new CorsConfiguration();

        configuration.setAllowedOrigins(
                List.of("http://localhost:5173")
        );

        configuration.setAllowedMethods(
                List.of(
                        "GET",
                        "POST",
                        "PUT",
                        "PATCH",
                        "DELETE",
                        "OPTIONS"
                )
        );

        configuration.setAllowedHeaders(List.of("*"));

        UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();

        source.registerCorsConfiguration(
                "/api/**",
                configuration
        );

        return source;
    }

    private AuthenticationEntryPoint json401EntryPoint() {
        return (request, response, authException)
                -> writeJson(response, HttpStatus.UNAUTHORIZED, "Unauthorized");
    }

    private AccessDeniedHandler json403Handler() {
        return (request, response, accessDeniedException)
                -> writeJson(response, HttpStatus.FORBIDDEN, "Forbidden");
    }

    private static void writeJson(HttpServletResponse response, HttpStatus status, String error) throws IOException {
        response.setStatus(status.value());
        response.setContentType(MediaType.APPLICATION_JSON_VALUE);
        response.getWriter().write("{\"error\":\"" + error + "\"}");
    }

}
