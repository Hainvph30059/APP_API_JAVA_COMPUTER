package com.hoangduong.hoangduongcomputer.mapper;

import org.mapstruct.Mapper;

import com.hoangduong.hoangduongcomputer.dto.request.AttributeTemplateRequest;
import com.hoangduong.hoangduongcomputer.dto.response.AttributeTemplateResponse;
import com.hoangduong.hoangduongcomputer.entity.AttributeTemplates;

@Mapper(componentModel = "spring")
public interface AttributeTemplateMapper {
    AttributeTemplateResponse toResponse(AttributeTemplates attributeTemplates);

    AttributeTemplates toEntity(AttributeTemplateRequest request);
}
