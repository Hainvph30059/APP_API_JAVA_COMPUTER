package com.hoangduong.hoangduongcomputer.dto.response;

import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.FieldDefaults;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)
public class CartItemResponse {
    String productId;
    String name;
    Double price;
    String imageUrl; // Ảnh đầu tiên
    Integer quantity;
    Double totalPrice; // price * quantity
}
