package com.hoangduong.hoangduongcomputer.entity;

import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import lombok.AccessLevel;
import lombok.Builder;
import lombok.Data;
import lombok.experimental.FieldDefaults;

@Data
@Document(collection = "CartDetails")
@Builder
@FieldDefaults(level = AccessLevel.PRIVATE)
public class CartDetail {
    @Id
    String id;

    String cartId;
    String productId;
    Integer quantity;
}
