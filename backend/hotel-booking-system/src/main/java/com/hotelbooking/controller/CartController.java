package com.hotelbooking.controller;

import com.hotelbooking.dto.cart.AddCartRequest;
import com.hotelbooking.dto.cart.CartResponse;
import com.hotelbooking.dto.cart.UpdateCartItemRequest;
import com.hotelbooking.service.CartService;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/cart")
@RequiredArgsConstructor
@Tag(name = "Cart")
public class CartController {

    private final CartService cartService;

    @GetMapping
    public CartResponse get(Authentication authentication) {
        String username = authentication.getName();
        return cartService.findByUsername(username);
    }

    @PostMapping("/items")
    public CartResponse addCartItems(@Valid @RequestBody AddCartRequest request, Authentication authentication) {
        return cartService.addCartItems(request, authentication.getName());
    }

    @PutMapping("/items/{itemId}")
    public CartResponse updateItem(@PathVariable String itemId, @Valid @RequestBody UpdateCartItemRequest request,
                                   Authentication authentication) {
        String username = authentication.getName();
        return cartService.updateQuantity(itemId, request, username);
    }

    @DeleteMapping("/items/{itemId}")
    public CartResponse deleteItem(@PathVariable String itemId, Authentication authentication) {
        String username = authentication.getName();
        return cartService.deleteCartItem(itemId, username);
    }

}
