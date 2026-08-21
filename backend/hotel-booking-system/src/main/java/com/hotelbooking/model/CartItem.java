package com.hotelbooking.model;

import lombok.*;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;
import org.springframework.data.mongodb.core.mapping.Field;

import java.math.BigDecimal;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode(callSuper = true)
@Document(collection = "cart_items")
public class CartItem extends BaseModel {

    @Id
    private String id;

    @Field("cart_id")
    private String cartId;

    @Field("room_type_id")
    private String roomTypeId;

    private Integer quantity;

    private BigDecimal price;

}
