package com.hoangduong.hoangduongcomputer.mapper;

import org.mapstruct.Mapper;

import com.hoangduong.hoangduongcomputer.dto.request.ProductTypeRequest;
import com.hoangduong.hoangduongcomputer.dto.response.ProductTypeResponse;
import com.hoangduong.hoangduongcomputer.entity.ProductType;

@Mapper(componentModel = "spring")
public interface ProductTypeMapper {
    ProductType toEntity(ProductTypeRequest request);

    ProductTypeResponse toResponse(ProductType productType);
}
