package com.hoangduong.hoangduongcomputer.dto.request;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.Map;

import lombok.*;
import lombok.experimental.FieldDefaults;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)
public class ProductRequest {
    String productTypeName;
    String name;
    Double price;
    Integer stock;
    String description;
    int viewCount = 0;
    Map<String, Object> attributes = new HashMap<>();
    LocalDateTime dateCreated = LocalDateTime.now();
}
