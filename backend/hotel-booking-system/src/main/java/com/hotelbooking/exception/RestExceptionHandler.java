package com.hotelbooking.exception;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.method.annotation.HandlerMethodValidationException;
import org.springframework.web.method.annotation.MethodArgumentTypeMismatchException;
import tools.jackson.core.JacksonException;
import tools.jackson.databind.exc.InvalidFormatException;

import java.time.LocalDate;
import java.util.Arrays;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Objects;
import java.util.stream.Collectors;

/**
 * Map exception nghiệp vụ → HTTP status.
 *
 * <p>401/403 từ <b>Spring Security filter / method security</b> xử lý trong
 * {@code SecurityConfig}. Class này bắt exception ném từ Service/Controller.</p>
 */
@RestControllerAdvice
public class RestExceptionHandler {

    @ExceptionHandler(UnauthorizedException.class)
    public ResponseEntity<Map<String, String>> unauthorized(UnauthorizedException ex) {
        // Login sai → 401 JSON
        return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                .body(Map.of("error", ex.getMessage()));
    }

    @ExceptionHandler(NotFoundException.class)
    public ResponseEntity<Map<String, String>> notFound(NotFoundException ex) {
        // User / Role không tồn tại → 404
        return ResponseEntity.status(HttpStatus.NOT_FOUND)
                .body(Map.of("error", ex.getMessage()));
    }

    @ExceptionHandler(ConflictException.class)
    public ResponseEntity<Map<String, String>> conflict(ConflictException ex) {
        // Trùng username / email → 409
        return ResponseEntity.status(HttpStatus.CONFLICT)
                .body(Map.of("error", ex.getMessage()));
    }

    @ExceptionHandler(ForbiddenException.class)
    public ResponseEntity<Map<String, String>> forbidden(ForbiddenException ex) {
        // Không có quyền truy cập → 403
        return ResponseEntity.status(HttpStatus.FORBIDDEN)
                .body(Map.of("error", ex.getMessage()));
    }

    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<Map<String, Map<String, String>>> validation(
            MethodArgumentNotValidException ex
    ) {
        // @Valid fail → 400
        Map<String, String> errors = new LinkedHashMap<>();

        ex.getBindingResult()
                .getFieldErrors()
                .forEach(error ->
                        errors.putIfAbsent(
                                error.getField(),
                                error.getDefaultMessage()
                        )
                );

        return ResponseEntity
                .status(HttpStatus.BAD_REQUEST)
                .body(Map.of("errors", errors));
    }

    @ExceptionHandler(HandlerMethodValidationException.class)
    public ResponseEntity<Map<String, Map<String, String>>> handleMethodValidation(
            HandlerMethodValidationException ex
    ) {
        // @RequestParam / @PathVariable validation fail → 400
        Map<String, String> errors = new LinkedHashMap<>();

        ex.getParameterValidationResults()
                .forEach(result -> {
                    String field = result.getMethodParameter().getParameterName();
                    result.getResolvableErrors().forEach(error ->
                            errors.putIfAbsent(
                                    field,
                                    error.getDefaultMessage()
                            )
                    );
                });

        return ResponseEntity
                .status(HttpStatus.BAD_REQUEST)
                .body(Map.of("errors", errors));
    }

    @ExceptionHandler(BadRequestException.class)
    public ResponseEntity<Map<String, Map<String, String>>> handleBadRequestException(
            BadRequestException ex
    ) {
        // Business validation fail → 400
        Map<String, String> errors = new LinkedHashMap<>();

        errors.put(
                ex.getField(),
                ex.getMessage()
        );

        return ResponseEntity
                .status(HttpStatus.BAD_REQUEST)
                .body(Map.of("errors", errors));
    }

    /**
     * Handle lỗi khi request parameter không thể convert sang kiểu dữ liệu yêu cầu.
     * Hỗ trợ Date, Enum và các kiểu dữ liệu khác.
     */
    @ExceptionHandler(MethodArgumentTypeMismatchException.class)
    public ResponseEntity<Map<String, Map<String, String>>> handleTypeMismatch(
            MethodArgumentTypeMismatchException ex
    ) {
        Map<String, String> errors = new LinkedHashMap<>();

        Class<?> requiredType = ex.getRequiredType();

        // Sai format LocalDate
        if (requiredType == LocalDate.class) {
            errors.put(
                    ex.getName(),
                    "Invalid date format. Expected yyyy-MM-dd"
            );

            // Sai giá trị Enum
        } else if (requiredType != null && requiredType.isEnum()) {
            String allowedValues = Arrays.stream(requiredType.getEnumConstants())
                    .map(Object::toString)
                    .collect(Collectors.joining(", "));

            errors.put(
                    ex.getName(),
                    String.format(
                            "%s must be one of: %s",
                            formatFieldName(ex.getName()),
                            allowedValues
                    )
            );

            // Các trường hợp sai kiểu dữ liệu khác
        } else {
            errors.put(
                    ex.getName(),
                    "Invalid value format"
            );
        }

        return ResponseEntity
                .status(HttpStatus.BAD_REQUEST)
                .body(Map.of("errors", errors));
    }

    /**
     * Handle lỗi request body có giá trị không đúng kiểu dữ liệu.
     * Ví dụ: truyền "abc" vào field Integer.
     */
    @ExceptionHandler(HttpMessageNotReadableException.class)
    public ResponseEntity<Map<String, Map<String, String>>> handleInvalidRequestBody(
            HttpMessageNotReadableException ex
    ) {
        Map<String, String> errors = new LinkedHashMap<>();

        InvalidFormatException invalidFormatException =
                findInvalidFormatException(ex);

        if (invalidFormatException != null) {

            String fieldName = invalidFormatException.getPath()
                    .stream()
                    .map(JacksonException.Reference::getPropertyName)
                    .filter(Objects::nonNull)
                    .collect(Collectors.joining("."));

            Class<?> targetType = invalidFormatException.getTargetType();

            if (targetType == Integer.class || targetType == int.class) {
                errors.put(
                        fieldName,
                        formatFieldName(fieldName) + " must be a valid number"
                );
            } else if (targetType != null && targetType.isEnum()) {
                String allowedValues = Arrays.stream(targetType.getEnumConstants())
                        .map(Object::toString)
                        .collect(Collectors.joining(", "));

                errors.put(
                        fieldName,
                        String.format(
                                "%s must be one of: %s",
                                formatFieldName(fieldName),
                                allowedValues
                        )
                );
            } else {
                errors.put(
                        fieldName,
                        "Invalid value format"
                );
            }

        } else {
            errors.put(
                    "requestBody",
                    "Invalid request body format"
            );
        }

        return ResponseEntity
                .status(HttpStatus.BAD_REQUEST)
                .body(Map.of("errors", errors));
    }

    /**
     * Convert tên field dạng camelCase sang chuỗi dễ đọc.
     * Ví dụ:
     * roomStatus     -> Room status
     * bookingStatus  -> Booking status
     * roomTypeStatus -> Room type status
     */
    private String formatFieldName(String fieldName) {
        String formatted = fieldName
                .replaceAll("([a-z])([A-Z])", "$1 $2")
                .toLowerCase();

        return Character.toUpperCase(formatted.charAt(0))
                + formatted.substring(1);
    }

    /**
     * Tìm InvalidFormatException trong chuỗi nguyên nhân exception.
     */
    private InvalidFormatException findInvalidFormatException(Throwable throwable) {
        while (throwable != null) {
            if (throwable instanceof InvalidFormatException invalidFormatException) {
                return invalidFormatException;
            }

            throwable = throwable.getCause();
        }

        return null;
    }

}

