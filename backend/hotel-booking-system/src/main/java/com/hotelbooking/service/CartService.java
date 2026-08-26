package com.hotelbooking.service;

import com.hotelbooking.dto.CartItemResponse;
import com.hotelbooking.dto.CartResponse;
import com.hotelbooking.exception.NotFoundException;
import com.hotelbooking.model.Cart;
import com.hotelbooking.model.CartItem;
import com.hotelbooking.model.RoomType;
import com.hotelbooking.repository.CartItemRepository;
import com.hotelbooking.repository.CartRepository;
import com.hotelbooking.repository.RoomTypeRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.time.Instant;
import java.util.ArrayList;
import java.util.List;

@Service
@RequiredArgsConstructor
public class CartService {

    private final CartRepository cartRepository;
    private final CartItemRepository cartItemRepository;
    private final RoomTypeRepository roomTypeRepository;

    public CartResponse findById(String userId) {

        BigDecimal subTotal;
        BigDecimal totalAmount = BigDecimal.ZERO;
        CartItemResponse cartItemResponse;
        List<CartItemResponse> cartItemList = new ArrayList<>();

        Cart cart = getOrCreateCart(userId);
        List<CartItem> cartItems = cartItemRepository.findAllByDeleteFlagFalse();

        for (CartItem cartItem : cartItems) {
            // Get room type name
            RoomType roomType = requireRomeType(cartItem.getRoomTypeId());
            String roomTypeName = roomType.getRoomTypeName();

            // Calculate total price of a cart
            subTotal = cartItem.getPrice().multiply(BigDecimal.valueOf(cartItem.getQuantity()));

            cartItemResponse = toCartItemResponse(cartItem, roomTypeName, subTotal);
            cartItemList.add(cartItemResponse);

            totalAmount = totalAmount.add(subTotal);
        }

        return toCartResponse(cart, cartItemList, totalAmount);
    }

    private Cart getOrCreateCart(String userId) {
        return cartRepository.findByUserIdAndDeleteFlagFalse(userId)
                .orElseGet(() -> {
                    // tự tạo cart rỗng nếu chưa có cart
                    Cart cart = new Cart();
                    cart.setUserId(userId);
                    cart.setDeleteFlag(false);
                    cart.setCreatedAt(Instant.now());
                    return cartRepository.save(cart);
                });
    }

    private RoomType requireRomeType(String id) {
        return roomTypeRepository.findByIdAndDeleteFlagFalse(id)
                .orElseThrow(() ->
                        new NotFoundException("Room type not found: " + id)
                );
    }

    private CartItemResponse toCartItemResponse(CartItem cartItem, String roomTypeName, BigDecimal subTotal) {
        return new CartItemResponse(
                cartItem.getId(),
                cartItem.getRoomTypeId(),
                roomTypeName,
                cartItem.getQuantity(),
                cartItem.getPrice(),
                subTotal);
    }

    private CartResponse toCartResponse(Cart cart, List<CartItemResponse> items, BigDecimal totalAmount) {
        return new CartResponse(
                cart.getId(),
                items,
                totalAmount);
    }

}
