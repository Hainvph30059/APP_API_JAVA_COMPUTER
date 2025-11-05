package com.hoangduong.hoangduongcomputer.dto.response;

import java.util.List;

import lombok.*;
import lombok.experimental.FieldDefaults;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)
public class AttributeTemplateResponse {
    // String productTypeId;
    List<String> attributeNames;
}
