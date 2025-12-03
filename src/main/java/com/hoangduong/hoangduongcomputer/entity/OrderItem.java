package com.hoangduong.hoangduongcomputer.entity;

import lombok.*;
import lombok.experimental.FieldDefaults;
import org.springframework.data.mongodb.core.mapping.Field;

import java.util.HashMap;
import java.util.Map;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)
public class OrderItem {

    @Field("productId")
    String productId;

    String productName;
    String productTypeName;
    Double price;
    Integer quantity;
    String imageUrl;

    @Builder.Default
    Map<String, Object> attributes = new HashMap<>();

    public Double getSubtotal() {
        return price * quantity;
    }
}

