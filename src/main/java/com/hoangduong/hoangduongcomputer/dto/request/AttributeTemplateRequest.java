package com.hoangduong.hoangduongcomputer.dto.request;

import java.util.List;

import lombok.*;
import lombok.experimental.FieldDefaults;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)
public class AttributeTemplateRequest {
    String productTypeName;
    List<String> attributeNames;
}
