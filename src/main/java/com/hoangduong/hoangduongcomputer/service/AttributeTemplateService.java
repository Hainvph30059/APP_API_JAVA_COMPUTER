package com.hoangduong.hoangduongcomputer.service;

import org.springframework.stereotype.Service;

import com.hoangduong.hoangduongcomputer.dto.request.AttributeTemplateRequest;
import com.hoangduong.hoangduongcomputer.dto.response.AttributeTemplateResponse;
import com.hoangduong.hoangduongcomputer.exception.AppException;
import com.hoangduong.hoangduongcomputer.exception.ErrorCode;
import com.hoangduong.hoangduongcomputer.mapper.AttributeTemplateMapper;
import com.hoangduong.hoangduongcomputer.reponsitory.AttributeTemplateRepository;

import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;

@Service
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class AttributeTemplateService {

    AttributeTemplateRepository attributeTemplateRepository;
    AttributeTemplateMapper attributeTemplateMapper;

    public AttributeTemplateResponse create(AttributeTemplateRequest request) {
        return attributeTemplateMapper.toResponse(
                attributeTemplateRepository.save(attributeTemplateMapper.toEntity(request)));
    }

    public AttributeTemplateResponse getByProductTypeId(String productTypeId) {
        var attributeTemplate = attributeTemplateRepository
                .findByProductTypeName(productTypeId)
                .orElseThrow(() -> new AppException(ErrorCode.INVALID_ATTRIBUTE_TEMPLATE));
        return attributeTemplateMapper.toResponse(attributeTemplate);
    }

    public AttributeTemplateResponse update(String productTypeName, AttributeTemplateRequest request) {
        var attributeTemplate = attributeTemplateRepository
                .findByProductTypeName(productTypeName)
                .orElseThrow(() -> new AppException(ErrorCode.INVALID_ATTRIBUTE_TEMPLATE));

        attributeTemplate.setAttributeNames(request.getAttributeNames());
        return attributeTemplateMapper.toResponse(attributeTemplateRepository.save(attributeTemplate));
    }
}
