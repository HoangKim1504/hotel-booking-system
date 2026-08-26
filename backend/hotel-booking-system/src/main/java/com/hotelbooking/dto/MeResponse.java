package com.hotelbooking.dto;

import java.util.List;

/**
 * DTO — identity + quyền hiện tại (load từ DB qua filter).
 * tiện đối chiếu {@code @PreAuthorize}.
 */
public record MeResponse(
        String id,
        String username,
        String email,
        List<String> roles,
        List<String> permissions) {
}
