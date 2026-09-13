package com.hotelbooking.dto.cart;

import jakarta.validation.Valid;
import jakarta.validation.constraints.NotEmpty;

import java.util.List;

public record AddCartRequest(

        @NotEmpty(message = "Items must not be empty")
        @Valid
        List<AddCartItemRequest> items

) {
}
