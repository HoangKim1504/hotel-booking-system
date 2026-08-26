package com.hotelbooking.security;

import com.hotelbooking.config.JwtProperties;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.security.Keys;
import org.springframework.stereotype.Service;

import javax.crypto.SecretKey;
import java.nio.charset.StandardCharsets;
import java.util.Date;
import java.util.List;

@Service
public class JwtService {

    private final JwtProperties properties;
    private final SecretKey key;

    public JwtService(JwtProperties properties) {
        this.properties = properties;
        // Secret ≥ 32 bytes cho HS256
        this.key = Keys.hmacShaKeyFor(properties.secret().getBytes(StandardCharsets.UTF_8));
    }

    /**
     * Phát hành access token sau login thành công.
     *
     * @param userId   Mongo {@code users._id} → claim {@code sub}
     * @param username claim phụ (tiện debug /me)
     */
    public String generateToken(String userId, String username) {
        // Build JWT: chỉ identity + thời điểm hết hạn + ký HS256
        Date now = new Date();
        Date exp = new Date(now.getTime() + properties.expirationMs());
        return Jwts.builder()
                .subject(userId)
                .claim("username", username)
                .issuedAt(now)
                .expiration(exp)
                .signWith(key)
                .compact();
    }

    /**
     * Verify chữ ký + exp; trả claims. Fail → JwtException (filter → không set context → 401).
     */
    public Claims parseClaims(String token) {
        // Parser yêu cầu đúng secret
        return Jwts.parser()
                .verifyWith(key)
                .build()
                .parseSignedClaims(token)
                .getPayload();
    }

    public String userId(Claims claims) {
        return claims.getSubject();
    }

    public String username(Claims claims) {
        Object v = claims.get("username");
        return v == null ? null : v.toString();
    }

    /**
     * Helper test / debug — không dùng roles trong token.
     */
    public List<String> rolesIfAny(Claims claims) {
        Object v = claims.get("roles");
        if (v instanceof List<?> list) {
            return list.stream().map(Object::toString).toList();
        }
        return List.of();
    }

}
