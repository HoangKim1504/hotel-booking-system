package com.hotelbooking.model;

import com.hotelbooking.enums.RoomTypeStatus;
import lombok.*;
import lombok.experimental.SuperBuilder;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.index.Indexed;
import org.springframework.data.mongodb.core.mapping.Document;
import org.springframework.data.mongodb.core.mapping.Field;

import java.math.BigDecimal;

@Data
@SuperBuilder
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode(callSuper = false)
@Document(collection = "room_types")
public class RoomType extends BaseModel {

    @Id
    private String id;

    @Indexed(unique = true)
    @Field("room_type_name")
    private String roomTypeName;

    @Field("room_size")
    private Double roomSize;

    private String facility;

    @Field("maximum_people")
    private Integer maximumPeople;

    private BigDecimal price;

    @Builder.Default
    private RoomTypeStatus status = RoomTypeStatus.ACTIVE;

}
