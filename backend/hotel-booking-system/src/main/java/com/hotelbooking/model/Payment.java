package com.hotelbooking.model;

import com.hotelbooking.enums.PaymentStatus;
import lombok.*;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.index.Indexed;
import org.springframework.data.mongodb.core.mapping.Document;
import org.springframework.data.mongodb.core.mapping.Field;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode(callSuper = true)
@Document(collection = "payment")
public class Payment extends BaseModel {

    @Id
    private String id;

    @Field("booking_id")
    private String bookingId;

    private BigDecimal amount;

    @Field("payment_method")
    private String paymentMethod;

    @Builder.Default
    private PaymentStatus status = PaymentStatus.PENDING;

    @Field("payment_date")
    private LocalDateTime paymentDate;

    @Indexed(unique = true, sparse = true)
    @Field("transaction_id")
    private String transactionId;

}
