package com.hotelbooking.controller;

import com.hotelbooking.dto.CartResponse;
import com.hotelbooking.service.CartService;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/carts")
@RequiredArgsConstructor
@Tag(name = "Carts")
public class CartController {

    private final CartService cartService;

    @GetMapping("/{id}")
    @PreAuthorize("hasAuthority('USER_VIEW')")
    public CartResponse get(@PathVariable String userId) {
        return cartService.findById(userId);
    }

}
