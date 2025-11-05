package com.hoangduong.hoangduongcomputer.mapper;

import org.mapstruct.Mapper;
import org.mapstruct.MappingTarget;

import com.hoangduong.hoangduongcomputer.dto.request.ProductRequest;
import com.hoangduong.hoangduongcomputer.dto.response.ProductResponse;
import com.hoangduong.hoangduongcomputer.entity.Product;

@Mapper(componentModel = "spring")
public interface ProductMapper {
    Product toEntity(ProductRequest request);

    ProductResponse toResponse(Product product);

    void updateEntity(@MappingTarget Product product, ProductRequest request);
}
