package com.hotelbooking.dto;

import jakarta.validation.constraints.NotBlank;

/**
 * DTO — đăng nhập
 */
public record LoginRequest(
        @NotBlank String username,
        @NotBlank String password) {
}
