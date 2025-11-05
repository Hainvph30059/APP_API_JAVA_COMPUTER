package com.hoangduong.hoangduongcomputer.entity;

import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.index.Indexed;
import org.springframework.data.mongodb.core.mapping.Document;

import lombok.AccessLevel;
import lombok.Builder;
import lombok.Data;
import lombok.experimental.FieldDefaults;

@Data
@Document(collection = "ProductTypes")
@Builder
@FieldDefaults(level = AccessLevel.PRIVATE)
public class ProductType {
    @Id
    String id;

    @Indexed(unique = true)
    String typeName;

    String displayName;
    String description;
}
