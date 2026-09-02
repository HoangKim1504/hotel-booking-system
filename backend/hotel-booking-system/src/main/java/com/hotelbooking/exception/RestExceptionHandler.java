package com.hotelbooking.exception;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.method.annotation.HandlerMethodValidationException;
import org.springframework.web.method.annotation.MethodArgumentTypeMismatchException;

import java.time.LocalDate;
import java.util.LinkedHashMap;
import java.util.Map;

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

    @ExceptionHandler(MethodArgumentTypeMismatchException.class)
    public ResponseEntity<Map<String, Map<String, String>>> handleMethodArgumentTypeMismatch(
            MethodArgumentTypeMismatchException ex
    ) {
        Map<String, String> errors = new LinkedHashMap<>();

        if (ex.getRequiredType() == LocalDate.class) {
            errors.put(
                    ex.getName(),
                    "Invalid date format. Expected yyyy-MM-dd"
            );
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

}

