package com.hotelbooking.dto;

import com.hotelbooking.enums.Gender;
import jakarta.validation.constraints.*;
import org.hibernate.validator.constraints.URL;

import java.time.LocalDate;

/**
 * DTO — cập nhật user
 * <p>
 * Field null = không cập nhật field đó.
 */
public record UpdateUserRequest(

        @NotNull(message = "Password is required when updating user information")
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

        @NotNull(message = "Full name is required when updating user information")
        @Size(
                min = 1,
                max = 100,
                message = "Full name must not exceed 100 characters"
        )
        String fullName,

        @NotNull(message = "Gender is required when updating user information")
        Gender gender,

        @Past(message = "Date of birth must be in the past")
        LocalDate dateOfBirth,

        @NotNull(message = "Email is required when updating user information")
        @Email(message = "Email format is invalid")
        @Size(
                max = 100,
                message = "Email must not exceed 100 characters"
        )
        String email,

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

        @NotNull(message = "Enabled status is required when updating user information")
        Boolean enabled
) {
}
