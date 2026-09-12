package com.hotelbooking.dto.user;

import jakarta.validation.constraints.NotBlank;

/**
 * DTO — gán role cho user.
 */
public record AssignRoleRequest(
        @NotBlank String roleCode) {
}
