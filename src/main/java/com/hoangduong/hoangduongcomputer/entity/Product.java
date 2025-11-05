package com.hoangduong.hoangduongcomputer.entity;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import lombok.AccessLevel;
import lombok.Builder;
import lombok.Data;
import lombok.experimental.FieldDefaults;

@Data
@Document(collection = "Products")
@Builder
@FieldDefaults(level = AccessLevel.PRIVATE)
public class Product {
    @Id
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
