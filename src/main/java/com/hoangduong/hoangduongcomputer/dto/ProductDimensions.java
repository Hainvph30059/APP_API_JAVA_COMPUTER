package com.hoangduong.hoangduongcomputer.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;

@Data
@Builder
@AllArgsConstructor
public class ProductDimensions {
    Integer length;
    Integer width;
    Integer height;
    Integer weight;
}
