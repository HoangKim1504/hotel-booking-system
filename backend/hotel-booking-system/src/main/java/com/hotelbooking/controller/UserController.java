package com.hotelbooking.controller;

import com.hotelbooking.dto.AssignRoleRequest;
import com.hotelbooking.dto.CreateUserRequest;
import com.hotelbooking.dto.UpdateUserRequest;
import com.hotelbooking.dto.UserResponse;
import com.hotelbooking.service.UserService;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/users")
@RequiredArgsConstructor
@Tag(name = "Users")
public class UserController {

    private final UserService userService;

    @GetMapping
    @PreAuthorize("hasAuthority('USER_VIEW')")
    public List<UserResponse> list() {
        // Cần USER_VIEW (ADMIN / EDITOR / USER đều có)
        return userService.findAll();
    }

    @GetMapping("/{id}")
    @PreAuthorize("hasAuthority('USER_VIEW')")
    public UserResponse get(@PathVariable String id) {
        return userService.findById(id);
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    @PreAuthorize("hasAuthority('USER_CREATE')")
    public UserResponse create(@Valid @RequestBody CreateUserRequest request) {
        // ADMIN + EDITOR
        return userService.create(request);
    }

    @PutMapping("/{id}")
    @PreAuthorize("hasAuthority('USER_UPDATE')")
    public UserResponse update(@PathVariable String id, @Valid @RequestBody UpdateUserRequest request) {
        // ADMIN + EDITOR
        return userService.update(id, request);
    }

    @DeleteMapping("/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    @PreAuthorize("hasAuthority('USER_DELETE')")
    public void delete(@PathVariable String id) {
        // Chỉ ADMIN (EDITOR / USER → 403)
        userService.delete(id);
    }

    @PostMapping("/{id}/roles")
    @PreAuthorize("hasAuthority('USER_ASSIGN_ROLE')")
    public UserResponse assignRole(
            @PathVariable String id,
            @Valid @RequestBody AssignRoleRequest request) {
        // Chỉ ADMIN
        return userService.assignRole(id, request.roleCode());
    }

    @DeleteMapping("/{id}/roles/{roleCode}")
    @PreAuthorize("hasAuthority('USER_ASSIGN_ROLE')")
    public UserResponse removeRole(@PathVariable String id, @PathVariable String roleCode) {
        // Chỉ ADMIN
        return userService.removeRole(id, roleCode);
    }

}