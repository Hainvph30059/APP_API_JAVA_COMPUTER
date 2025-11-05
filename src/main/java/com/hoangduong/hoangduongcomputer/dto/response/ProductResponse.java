package com.hoangduong.hoangduongcomputer.dto.response;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import lombok.*;
import lombok.experimental.FieldDefaults;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)
public class ProductResponse {
    String id;
    String productTypeName;
    String name;
    Double price;
    Integer stock;
    String description;
    int viewCount;
    Map<String, Object> attributes = new HashMap<>();
    LocalDateTime dateCreated;

    List<String> urls;
}
