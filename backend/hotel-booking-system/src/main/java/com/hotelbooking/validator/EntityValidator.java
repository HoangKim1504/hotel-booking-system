package com.hotelbooking.validator;

import com.hotelbooking.enums.RoomTypeStatus;
import com.hotelbooking.exception.ForbiddenException;
import com.hotelbooking.exception.NotFoundException;
import com.hotelbooking.exception.UnauthorizedException;
import com.hotelbooking.model.*;
import com.hotelbooking.repository.*;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class EntityValidator {

    private final UserRepository userRepository;
    private final RoleRepository roleRepository;
    private final CartItemRepository cartItemRepository;
    private final RoomTypeRepository roomTypeRepository;
    private final RoomRepository roomRepository;

    // ========================
    // User
    // ========================
    public User requireUserByUserId(String id) {
        return userRepository.findByIdAndDeleteFlagFalse(id)
                .orElseThrow(() ->
                        new NotFoundException("User not found: " + id)
                );
    }

    public User requireUserLogin(String username) {
        return userRepository.findByUsernameAndDeleteFlagFalse(username)
                .orElseThrow(() ->
                        new UnauthorizedException("Invalid username or password")
                );
    }

    public User requireUserByUsername(String username) {
        return userRepository.findByUsernameAndDeleteFlagFalse(username)
                .orElseThrow(() ->
                        new NotFoundException("Username not found: " + username)
                );
    }

    // ========================
    // Role
    // ========================
    public Role requireRole(String roleCode) {
        String code = roleCode.trim().toUpperCase();

        return roleRepository.findByCodeAndDeleteFlagFalse(code)
                .orElseThrow(() ->
                        new NotFoundException("Role not found: " + roleCode)
                );
    }

    // ========================
    // Cart item
    // ========================
    public CartItem requireCartItem(String cartId, String itemId) {
        CartItem cartItem = cartItemRepository.findByIdAndDeleteFlagFalse(itemId)
                .orElseThrow(() ->
                        new NotFoundException("Cart item not found: " + itemId)
                );

        if (!cartItem.getCartId().equals(cartId)) {
            throw new ForbiddenException(
                    "Cart item does not belong to current user"
            );
        }

        return cartItem;
    }

    // ========================
    // Room type
    // ========================
    public RoomType requireAdminRoomType(String id) {
        return roomTypeRepository.findByIdAndDeleteFlagFalse(id)
                .orElseThrow(() ->
                        new NotFoundException("Room type not found: " + id)
                );
    }

    public RoomType requireAdminRoomTypeByName(String roomTypeName) {
        return roomTypeRepository.findByRoomTypeNameAndDeleteFlagFalse(roomTypeName)
                .orElseThrow(() ->
                        new NotFoundException("Room type name not found: " + roomTypeName)
                );
    }

    public RoomType requireRoomType(String id, RoomTypeStatus status) {
        return roomTypeRepository.findByIdAndStatusAndDeleteFlagFalse(id, status)
                .orElseThrow(() ->
                        new NotFoundException("Room type not found: " + id)
                );
    }

    // ========================
    // Room
    // ========================
    public Room requireAdminRoom(String id) {
        return roomRepository.findByIdAndDeleteFlagFalse(id)
                .orElseThrow(() ->
                        new NotFoundException("Room not found: " + id)
                );
    }

}
