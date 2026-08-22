package com.hotelbooking.model;

import com.hotelbooking.enums.UserStatus;
import lombok.*;
import lombok.experimental.SuperBuilder;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.index.Indexed;
import org.springframework.data.mongodb.core.mapping.Document;
import org.springframework.data.mongodb.core.mapping.Field;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

@Data
@SuperBuilder
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode(callSuper = false)
@Document(collection = "users")
public class User extends BaseModel {

    @Id
    private String id;

    @Field("role_id")
    private String roleId;

    @Indexed(unique = true)
    @Field("user_name")
    private String userName;

    /**
     * BCrypt hash — không lưu plain text.
     */
    private String password;

    @Field("full_name")
    private String fullName;

    private String gender;

    @Field("date_of_birth")
    private LocalDate dateOfBirth;

    @Indexed(unique = true)
    private String email;

    @Field("phone_number")
    private String phoneNumber;

    private String address;

    @Field("profile_url_link")
    private String profileUrlLink;

    @Builder.Default
    private UserStatus status = UserStatus.ACTIVE;

    /**
     * Reference → {@code roles._id}.
     */
    private List<String> roleIds = new ArrayList<>();

}