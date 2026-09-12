package com.hotelbooking.model;

import com.hotelbooking.enums.BookingStatus;
import lombok.*;
import lombok.experimental.SuperBuilder;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;
import org.springframework.data.mongodb.core.mapping.Field;

import java.time.LocalDate;
import java.time.LocalDateTime;

@Data
@SuperBuilder
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode(callSuper = true)
@Document(collection = "bookings")
public class Booking extends BaseModel {

    @Id
    private String id;

    @Field("user_id")
    private String userId;

    @Builder.Default
    private BookingStatus status = BookingStatus.PENDING;

    @Field("check_in_date")
    private LocalDate checkInDate;

    @Field("check_out_date")
    private LocalDate checkOutDate;

    @Field("expires_at")
    private LocalDateTime expiresAt;

}
