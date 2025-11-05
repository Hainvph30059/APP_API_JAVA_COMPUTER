package com.hoangduong.hoangduongcomputer.service;

import java.util.Collections;
import java.util.List;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.hoangduong.hoangduongcomputer.dto.request.ProductTypeRequest;
import com.hoangduong.hoangduongcomputer.dto.response.ProductTypeResponse;
import com.hoangduong.hoangduongcomputer.entity.AttributeTemplates;
import com.hoangduong.hoangduongcomputer.exception.AppException;
import com.hoangduong.hoangduongcomputer.exception.ErrorCode;
import com.hoangduong.hoangduongcomputer.mapper.ProductTypeMapper;
import com.hoangduong.hoangduongcomputer.reponsitory.AttributeTemplateRepository;
import com.hoangduong.hoangduongcomputer.reponsitory.ProductTypeRepository;

import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import lombok.extern.slf4j.Slf4j;

@Service
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
@Slf4j
public class ProductTypeService {
    ProductTypeRepository productTypeRepository;
    AttributeTemplateRepository attributeTemplateRepository;
    ProductTypeMapper productTypeMapper;

    @Transactional
    public ProductTypeResponse create(ProductTypeRequest request) {
        var productType = productTypeMapper.toEntity(request);
        try {
            productTypeRepository.save(productType);
        } catch (Exception e) {
            log.error("Exception: {}", e.getClass().getName()); // kiểm tra loại lỗi thực sự
            throw new AppException(ErrorCode.PRODUCT_TYPE_EXISTED);
        }
        log.info("Product type created {}", productType);
        var attributeTemplate = AttributeTemplates.builder()
                .productTypeName(productType.getTypeName())
                .attributeNames(request.getAttributeNames())
                .build();
        attributeTemplateRepository.save(attributeTemplate);

        var productTypeResponse = productTypeMapper.toResponse(productType);
        productTypeResponse.setAttributeNames(attributeTemplate.getAttributeNames());
        return productTypeResponse;
    }

    public ProductTypeResponse getById(String productTypeId) {
        var productType = productTypeRepository
                .findById(productTypeId)
                .orElseThrow(() -> new AppException(ErrorCode.PRODUCT_TYPE_NOT_EXISTED));
        return productTypeMapper.toResponse(productType);
    }

    public List<ProductTypeResponse> getAll() {
        var productType = productTypeRepository.findAll();
        return productType.stream()
                .map(pt ->{
                    List<String> attributeNames = attributeTemplateRepository
                            .findByProductTypeName(pt.getTypeName())
                            .map(AttributeTemplates::getAttributeNames)
                            .orElse(Collections.emptyList());
                    return new ProductTypeResponse(
                            pt.getTypeName(),
                            pt.getDisplayName(),
                            pt.getDescription(),
                            attributeNames
                    );
                })
                .toList();
    }

    public ProductTypeResponse update(ProductTypeRequest request) {
        var productType = productTypeRepository
                .findByTypeName(request.getTypeName())
                .orElseThrow(() -> new AppException(ErrorCode.PRODUCT_TYPE_NOT_EXISTED));
        productType.setDisplayName(request.getDisplayName());
        productType.setDescription(request.getDescription());
        productTypeRepository.save(productType);
        var attributeTemplate = attributeTemplateRepository
                .findByProductTypeName(productType.getTypeName())
                .orElseThrow(() -> new AppException(ErrorCode.ATTRIBUTE_TEMPLATE_NOT_EXISTED));
        attributeTemplate.setAttributeNames(request.getAttributeNames());
        attributeTemplateRepository.save(attributeTemplate);
        ProductTypeResponse pt = productTypeMapper.toResponse(productType);
        pt.setAttributeNames(attributeTemplate.getAttributeNames());
        return pt;
    }
}
