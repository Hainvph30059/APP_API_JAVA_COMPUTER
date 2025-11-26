package com.hoangduong.hoangduongcomputer.entity;

import java.time.LocalDateTime;

import lombok.*;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import lombok.experimental.FieldDefaults;

@Data
@Document(collection = "Carts")
@Builder
@FieldDefaults(level = AccessLevel.PRIVATE)
@NoArgsConstructor
@AllArgsConstructor
public class Cart {
    @Id
    String id;

    String userId;
    LocalDateTime dateCreated;
}
