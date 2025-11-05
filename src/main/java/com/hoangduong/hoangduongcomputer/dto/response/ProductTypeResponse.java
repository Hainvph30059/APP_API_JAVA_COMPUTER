package com.hoangduong.hoangduongcomputer.dto.response;

import lombok.*;
import lombok.experimental.FieldDefaults;

import java.util.List;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)
public class ProductTypeResponse {
    String typeName;
    String displayName;
    String description;
    List<String> attributeNames;
}
