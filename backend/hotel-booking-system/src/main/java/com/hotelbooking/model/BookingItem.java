package com.hotelbooking.model;

import lombok.*;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;
import org.springframework.data.mongodb.core.mapping.Field;

import java.time.LocalDate;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode(callSuper = true)
@Document(collection = "booking_items")
public class BookingItem extends BaseModel {

    @Id
    private String id;

    @Field("booking_id")
    private String bookingId;

    @Field("room_type_id")
    private String roomTypeId;

    private Integer quantity;

    @Field("check_in_date")
    private LocalDate checkInDate;

    @Field("check_out_date")
    private LocalDate checkOutDate;

}
