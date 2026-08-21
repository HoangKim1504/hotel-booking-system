package com.hotelbooking.model;

import lombok.*;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;
import org.springframework.data.mongodb.core.mapping.Field;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode(callSuper = true)
@Document(collection = "room_assignments")
public class RoomAssignment extends BaseModel {

    @Id
    private String id;

    @Field("booking_item_id")
    private String bookingItemId;

    @Field("room_id")
    private String roomId;

}
