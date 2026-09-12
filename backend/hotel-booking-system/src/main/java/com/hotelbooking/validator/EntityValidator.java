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
    private final BookingRepository bookingRepository;

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

    // ========================
    // Booking
    // ========================
    public Booking requireBooking(String bookingId) {
        return bookingRepository.findByDeleteFlagFalseAndId(bookingId)
                .orElseThrow(() ->
                        new NotFoundException("Booking not found: " + bookingId)
                );
    }

    /**
     * Tìm Booking và kiểm tra Booking có thuộc user hay không.
     */
    public Booking requireBookingOwnedByUser(String bookingId, String userId) {
        // 1. Kiểm tra User tồn tại
        requireUserByUserId(userId);

        // 2. Kiểm tra Booking tồn tại
        Booking booking = requireBooking(bookingId);

        // 3. Kiểm tra Booking thuộc User
        if (!userId.equals(booking.getUserId())) {
            throw new ForbiddenException(
                    "This booking does not belong to given user: " + userId
            );
        }

        return booking;
    }

}
