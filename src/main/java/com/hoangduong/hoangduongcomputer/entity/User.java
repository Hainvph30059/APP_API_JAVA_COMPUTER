package com.hoangduong.hoangduongcomputer.entity;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.index.Indexed;
import org.springframework.data.mongodb.core.mapping.Document;
import org.springframework.data.mongodb.core.mapping.Field;

import java.time.Instant;

@Document(collection = "users")
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class User {

    @Id
    private String id;

    @Indexed(unique = true)
    private String email;

    private String password;

    private String username;

    @Field("displayName")
    private String displayName;

    private String avatar;

    @Builder.Default
    private String role = UserRole.CLIENT.getValue();

    @Builder.Default
    @Field("isActive")
    private Boolean isActive = false;

    @Field("verifyToken")
    private String verifyToken;

    @Builder.Default
    private Instant createdAt = Instant.now();

    private Instant updatedAt;

    @Builder.Default
    @Field("_destroy")
    private Boolean destroy = false;

    public enum UserRole {
        CLIENT("client"),
        ADMIN("admin");

        private final String value;

        UserRole(String value) {
            this.value = value;
        }

        public String getValue() {
            return value;
        }
    }
}
