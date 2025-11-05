package com.hoangduong.hoangduongcomputer.entity;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Set;

import lombok.Builder;
import lombok.experimental.FieldDefaults;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import lombok.Data;

@Data
@Document(collection = "Users")
@Builder
@FieldDefaults(level = lombok.AccessLevel.PRIVATE)
public class User {
    @Id
    String id;

    String username;
    String passwordHash;
    String email;
    String firstName;
    String lastName;
    String phoneNumber;
    List<String> address;
    LocalDateTime dateCreated;

    Set<String> roleIds;
}
