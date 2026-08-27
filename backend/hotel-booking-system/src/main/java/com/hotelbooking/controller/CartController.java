package com.hotelbooking.controller;

import com.hotelbooking.dto.AddCartItemRequest;
import com.hotelbooking.dto.CartResponse;
import com.hotelbooking.service.CartService;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/carts")
@RequiredArgsConstructor
@Tag(name = "Carts")
public class CartController {

    private final CartService cartService;

    @GetMapping
    public CartResponse get(Authentication authentication) {
        String username = authentication.getName();
        return cartService.findByUsername(username);
    }

    @PostMapping("/items")
    public CartResponse addItem(@Valid @RequestBody AddCartItemRequest request,
                                Authentication authentication) {
        String username = authentication.getName();
        return cartService.addCartItem(request, username);
    }

}
