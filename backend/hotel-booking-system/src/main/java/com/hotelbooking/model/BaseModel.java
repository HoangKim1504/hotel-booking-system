package com.hotelbooking.model;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.SuperBuilder;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.LastModifiedDate;
import org.springframework.data.mongodb.core.mapping.Field;

import java.time.Instant;

@Data
@SuperBuilder
@NoArgsConstructor
@AllArgsConstructor
public abstract class BaseModel {

    @Builder.Default
    @Field("delete_flag")
    private Boolean deleteFlag = false;

    @Field("created_by")
    private String createdBy;

    @CreatedDate
    @Field("created_at")
    private Instant createdAt;

    @Field("updated_by")
    private String updatedBy;

    @LastModifiedDate
    @Field("updated_at")
    private Instant updatedAt;

}
