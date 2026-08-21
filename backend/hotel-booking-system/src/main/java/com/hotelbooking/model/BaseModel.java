package com.hotelbooking.model;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.SuperBuilder;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.LastModifiedDate;
import org.springframework.data.mongodb.core.mapping.Field;

import java.time.LocalDateTime;

@Data
@SuperBuilder
@NoArgsConstructor
@AllArgsConstructor
public abstract class BaseModel {

    @Builder.Default
    @Field("deleted_flag")
    private Boolean deletedFlag = false;

    @Field("created_by")
    private String createdBy;

    @CreatedDate
    @Field("created_at")
    private LocalDateTime createdAt;

    @Field("updated_by")
    private String updatedBy;

    @LastModifiedDate
    @Field("updated_at")
    private LocalDateTime updatedAt;

}
