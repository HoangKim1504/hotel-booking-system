package com.hotelbooking.dto;

import com.hotelbooking.enums.Gender;
import jakarta.validation.constraints.*;
import org.hibernate.validator.constraints.URL;

import java.time.LocalDate;
import java.util.List;

/**
 * DTO — tạo user
 */
public record CreateUserRequest(
        @NotBlank(message = "Username is required")
        @Size(
                min = 3,
                max = 50,
                message = "Username must be between 3 and 50 characters"
        )
        String username,

        @NotBlank(message = "Password is required")
        @Size(
                min = 6,
                max = 100,
                message = "Password must be between 6 and 100 characters"
        )
        @Pattern(
                regexp = "^(?=.*[a-z])(?=.*[A-Z])(?=.*\\d)(?=.*[@$!%*?&]).+$",
                message = "Password must contain at least one uppercase letter, one lowercase letter, one number, and one special character"
        )
        String password,

        @NotBlank(message = "Full name is required")
        @Size(
                min = 1,
                max = 100,
                message = "Full name must not exceed 100 characters"
        )
        String fullName,

        @NotNull(message = "Gender is required")
        Gender gender,

        @NotNull(message = "Date of birth is required")
        @Past(message = "Date of birth must be in the past")
        LocalDate dateOfBirth,

        @NotBlank(message = "Email is required")
        @Email(message = "Email format is invalid")
        @Size(
                max = 100,
                message = "Email must not exceed 100 characters"
        )
        String email,

        @NotBlank(message = "Phone number is required")
        @Pattern(
                regexp = "^(0|\\+84)[0-9]{9}$",
                message = "Phone number must be 10 digits or start with +84 followed by 9 digits"
        )
        String phoneNumber,

        @Size(
                max = 255,
                message = "Address must not exceed 255 characters"
        )
        String address,

        @URL(message = "Profile URL format is invalid")
        @Size(
                max = 500,
                message = "Profile URL must not exceed 500 characters"
        )
        String profileUrlLink,

        // Optional. Defaults to USER when null or empty.
        List<String> roleCodes) {
}
