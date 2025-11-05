package com.hoangduong.hoangduongcomputer.entity;

import java.util.List;

import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import lombok.AccessLevel;
import lombok.Builder;
import lombok.Data;
import lombok.experimental.FieldDefaults;

@Data
@Document(collection = "AttributeTemplates")
@Builder
@FieldDefaults(level = AccessLevel.PRIVATE)
public class AttributeTemplates {
    @Id
    String id;

    String productTypeName;
    List<String> attributeNames;
}
