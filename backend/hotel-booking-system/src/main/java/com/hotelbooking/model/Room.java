package com.hotelbooking.model;

import com.hotelbooking.enums.RoomStatus;
import lombok.*;
import lombok.experimental.SuperBuilder;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.index.Indexed;
import org.springframework.data.mongodb.core.mapping.Document;
import org.springframework.data.mongodb.core.mapping.Field;

@Data
@SuperBuilder
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode(callSuper = false)
@Document(collection = "rooms")
public class Room extends BaseModel {

    @Id
    private String id;

    @Field("room_type_id")
    private String roomTypeId;

    @Indexed(unique = true)
    @Field("room_number")
    private String roomNumber;

    @Field("floor_number")
    private Integer floorNumber;

    @Builder.Default
    private RoomStatus status = RoomStatus.ACTIVE;

}
