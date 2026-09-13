package com.hotelbooking.service;

import com.hotelbooking.dto.cart.*;
import com.hotelbooking.enums.RoomTypeStatus;
import com.hotelbooking.model.Cart;
import com.hotelbooking.model.CartItem;
import com.hotelbooking.model.RoomType;
import com.hotelbooking.model.User;
import com.hotelbooking.repository.CartItemRepository;
import com.hotelbooking.repository.CartRepository;
import com.hotelbooking.validator.EntityValidator;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.time.Instant;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class CartService {

    private final CartRepository cartRepository;
    private final CartItemRepository cartItemRepository;
    private final EntityValidator entityValidator;

    public CartResponse findByUsername(String username) {

        BigDecimal subTotal;
        BigDecimal totalAmount = BigDecimal.ZERO;
        CartItemResponse cartItemResponse;
        List<CartItemResponse> cartItemList = new ArrayList<>();

        String userId = getUserId(username);

        Cart cart = getOrCreateCart(userId, username);
        List<CartItem> cartItems = cartItemRepository.findByCartIdAndDeleteFlagFalse(cart.getId());

        for (CartItem cartItem : cartItems) {
            // Get room type name
            String roomTypeName = getRoomTypeName(cartItem.getRoomTypeId());

            // Calculate total price of a cart
            subTotal = cartItem.getPrice().multiply(BigDecimal.valueOf(cartItem.getQuantity()));

            cartItemResponse = toCartItemResponse(cartItem, roomTypeName, subTotal);
            cartItemList.add(cartItemResponse);

            totalAmount = totalAmount.add(subTotal);
        }

        return toCartResponse(cart, cartItemList, totalAmount, null);
    }

    public CartResponse addCartItems(AddCartRequest request, String username) {
        // 1. Lấy User + Cart
        String userId = getUserId(username);

        Cart cart = getOrCreateCart(
                userId,
                username
        );

        Instant now = Instant.now();

        List<CartItemResponse> cartItemResponseList = new ArrayList<>();

        // 2. Duyệt từng item FE gửi lên
        for (AddCartItemRequest requestItem : request.items()) {

            // 3. Check RoomType tồn tại + ACTIVE
            RoomType roomType =
                    entityValidator.requireRoomType(
                            requestItem.roomTypeId(),
                            RoomTypeStatus.ACTIVE
                    );

            // 4. Check RoomType này đã có trong Cart chưa
            Optional<CartItem> existingItem =
                    cartItemRepository
                            .findByCartIdAndRoomTypeIdAndDeleteFlagFalse(
                                    cart.getId(),
                                    requestItem.roomTypeId()
                            );

            CartItem cartItem;

            if (existingItem.isPresent()) {

                // Đã có trong Cart
                // → cộng thêm quantity
                cartItem = existingItem.get();

                cartItem.setQuantity(
                        cartItem.getQuantity()
                                + requestItem.quantity()
                );

                // Refresh lại giá RoomType hiện tại
                cartItem.setPrice(
                        roomType.getPrice()
                );

                cartItem.setUpdatedBy(username);
                cartItem.setUpdatedAt(now);

            } else {

                // Chưa có trong Cart
                // → tạo mới
                cartItem = new CartItem();

                cartItem.setCartId(
                        cart.getId()
                );

                cartItem.setRoomTypeId(
                        roomType.getId()
                );

                cartItem.setQuantity(
                        requestItem.quantity()
                );

                // Cart luôn lấy giá hiện tại từ DB
                cartItem.setPrice(
                        roomType.getPrice()
                );

                cartItem.setDeleteFlag(false);

                cartItem.setCreatedBy(username);
                cartItem.setCreatedAt(now);

                cartItem.setUpdatedBy(null);
                cartItem.setUpdatedAt(null);
            }

            // 5. Save CartItem
            CartItem savedItem =
                    cartItemRepository.save(cartItem);

            // 6. Tính subtotal
            BigDecimal subTotal =
                    savedItem.getPrice()
                            .multiply(
                                    BigDecimal.valueOf(
                                            savedItem.getQuantity()
                                    )
                            );

            // 7. Tạo response
            CartItemResponse response =
                    toCartItemResponse(
                            savedItem,
                            roomType.getRoomTypeName(),
                            subTotal
                    );

            cartItemResponseList.add(response);
        }

        // 8. Return Cart
        return toCartResponse(
                cart,
                cartItemResponseList,
                null,
                null
        );
    }

    public CartResponse updateQuantity(String itemId, UpdateCartItemRequest request, String username) {
        BigDecimal subTotal;
        CartItemResponse cartItemResponse;
        List<CartItemResponse> cartItemList = new ArrayList<>();
        List<String> warningMessages;

        String userId = getUserId(username);
        Cart cart = getOrCreateCart(userId, username);
        CartItem cartItem = entityValidator.requireCartItem(cart.getId(), itemId);
        RoomType roomType = entityValidator.requireRoomType(cartItem.getRoomTypeId(), RoomTypeStatus.ACTIVE);

        // Get room type name
        String roomTypeName = roomType.getRoomTypeName();

        warningMessages = checkChangeRoomTypePrice(cartItem.getPrice(), roomType.getPrice(), roomTypeName);

        cartItem.setQuantity(request.quantity());
        cartItem.setPrice(roomType.getPrice()); // Refresh lại giá hiện tại
        cartItem.setUpdatedBy(username);
        cartItem.setUpdatedAt(Instant.now());

        cartItemRepository.save(cartItem);

        // Calculate total price of a cart
        subTotal = cartItem.getPrice().multiply(BigDecimal.valueOf(cartItem.getQuantity()));

        cartItemResponse = toCartItemResponse(cartItem, roomTypeName, subTotal);
        cartItemList.add(cartItemResponse);

        return toCartResponse(cart, cartItemList, null, warningMessages);
    }

    public CartResponse deleteCartItem(String itemId, String username) {
        List<CartItemResponse> cartItemList = new ArrayList<>();

        String userId = getUserId(username);
        Cart cart = getOrCreateCart(userId, username);
        CartItem cartItem = entityValidator.requireCartItem(cart.getId(), itemId);

        cartItemRepository.delete(cartItem);

        return toCartResponse(cart, cartItemList, BigDecimal.valueOf(0), null);
    }

    private Cart getOrCreateCart(String userId, String username) {
        return cartRepository.findByUserIdAndDeleteFlagFalse(userId)
                .orElseGet(() -> {
                    // tự tạo cart rỗng nếu chưa có cart
                    Cart cart = new Cart();
                    cart.setUserId(userId);
                    cart.setDeleteFlag(false);
                    cart.setCreatedBy(username);
                    cart.setCreatedAt(Instant.now());
                    return cartRepository.save(cart);
                });
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

    private CartResponse toCartResponse(Cart cart, List<CartItemResponse> items, BigDecimal totalAmount,
                                        List<String> warningMessages) {
        warningMessages = warningMessages == null ? List.of() : warningMessages;
        return new CartResponse(
                cart.getId(),
                items,
                totalAmount,
                warningMessages);
    }

    private String getUserId(String username) {
        User user = entityValidator.requireUserByUsername(username);
        return user.getId();
    }

    private String getRoomTypeName(String roomTypeId) {
        RoomType roomType = entityValidator.requireRoomType(roomTypeId, RoomTypeStatus.ACTIVE);
        return roomType.getRoomTypeName();
    }

    private List<String> checkChangeRoomTypePrice(BigDecimal oldPrice, BigDecimal currentPrice, String roomTypeName) {
        List<String> warnings = new ArrayList<>();
        if (oldPrice.compareTo(currentPrice) != 0) {
            warnings.add("The price of " + roomTypeName + " has changed from " + oldPrice + " to " + currentPrice);
        }
        return warnings;
    }

}
