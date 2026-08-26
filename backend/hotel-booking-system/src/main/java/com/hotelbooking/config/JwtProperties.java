package com.hotelbooking.config;

import org.springframework.boot.context.properties.ConfigurationProperties;

/**
 * Bind {@code app.jwt.*} từ {@code application.properties}.
 */
@ConfigurationProperties(prefix = "app.jwt")
public record JwtProperties(
        String secret,
        long expirationMs) {
}
