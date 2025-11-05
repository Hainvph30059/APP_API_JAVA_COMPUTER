package com.hoangduong.hoangduongcomputer.controller;

import org.springframework.web.bind.annotation.*;

import com.hoangduong.hoangduongcomputer.dto.ApiReponse;
import com.hoangduong.hoangduongcomputer.dto.request.AttributeTemplateRequest;
import com.hoangduong.hoangduongcomputer.dto.response.AttributeTemplateResponse;
import com.hoangduong.hoangduongcomputer.service.AttributeTemplateService;

import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;

@RestController
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class AttributeTemplateController {
    AttributeTemplateService attributeTemplateService;

    @PostMapping("/attribute-template")
    ApiReponse<AttributeTemplateResponse> create(@RequestBody AttributeTemplateRequest request) {
        return ApiReponse.<AttributeTemplateResponse>builder()
                .result(attributeTemplateService.create(request))
                .build();
    }

    @GetMapping("/attribute-template/{productTypeId}")
    ApiReponse<AttributeTemplateResponse> getByProductTypeId(@PathVariable String productTypeId) {
        return ApiReponse.<AttributeTemplateResponse>builder()
                .result(attributeTemplateService.getByProductTypeId(productTypeId))
                .build();
    }

    @PutMapping("/attribute-template/{productTypeName}")
    ApiReponse<AttributeTemplateResponse> update(
            @PathVariable String productTypeName, @RequestBody AttributeTemplateRequest request) {
        return ApiReponse.<AttributeTemplateResponse>builder()
                .result(attributeTemplateService.update(productTypeName, request))
                .build();
    }
}
