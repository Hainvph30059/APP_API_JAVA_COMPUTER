package com.hoangduong.hoangduongcomputer.dto.response;

import java.time.LocalDateTime;
import java.util.List;

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
public class CartResponse {
    String cartId;
    String userId;
    LocalDateTime dateCreated;
    List<CartItemResponse> items;
    Integer totalItems; // Tổng số lượng sản phẩm
    Double totalPrice; // Tổng giá trị giỏ hàng
}
