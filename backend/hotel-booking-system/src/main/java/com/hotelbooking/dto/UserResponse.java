package com.hotelbooking.dto;

import com.hotelbooking.enums.Gender;

import java.time.Instant;
import java.time.LocalDate;
import java.util.List;

/**
 * DTO — response user (không lộ password).
 */
public record UserResponse(
        String id,
        String username,
        String fullName,
        Gender gender,
        LocalDate dateOfBirth,
        String email,
        String phoneNumber,
        String address,
        boolean enabled,
        List<String> roles,
        Instant createdAt,
        Instant updatedAt) {
}
