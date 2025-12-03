package com.hoangduong.hoangduongcomputer.dto.request;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Map;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class OrderItemRequest {
    String productId;
    Integer quantity;
    Map<String, Object> attributes;
}
