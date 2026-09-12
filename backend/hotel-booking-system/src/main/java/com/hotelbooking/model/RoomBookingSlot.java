package com.hotelbooking.model;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.index.CompoundIndex;
import org.springframework.data.mongodb.core.mapping.Document;
import org.springframework.data.mongodb.core.mapping.Field;

import java.time.Instant;
import java.time.LocalDate;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Document(collection = "room_booking_slots")
@CompoundIndex(
        name = "uk_room_stay_date",
        def = "{'room_id': 1, 'stay_date': 1}",
        unique = true
)
public class RoomBookingSlot {

    @Id
    private String id;

    @Field("room_id")
    private String roomId;

    @Field("booking_id")
    private String bookingId;

    @Field("stay_date")
    private LocalDate stayDate;

    private Instant createdAt;

}
