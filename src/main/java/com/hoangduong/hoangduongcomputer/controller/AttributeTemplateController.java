package com.hoangduong.hoangduongcomputer.controller;

import org.springframework.web.bind.annotation.*;

import com.hoangduong.hoangduongcomputer.dto.ApiResponse;
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
    ApiResponse<AttributeTemplateResponse> create(@RequestBody AttributeTemplateRequest request) {
        return ApiResponse.<AttributeTemplateResponse>builder()
                .result(attributeTemplateService.create(request))
                .build();
    }

    @GetMapping("/attribute-template/{productTypeId}")
    ApiResponse<AttributeTemplateResponse> getByProductTypeId(@PathVariable String productTypeId) {
        return ApiResponse.<AttributeTemplateResponse>builder()
                .result(attributeTemplateService.getByProductTypeId(productTypeId))
                .build();
    }

    @PutMapping("/attribute-template/{productTypeName}")
    ApiResponse<AttributeTemplateResponse> update(
            @PathVariable String productTypeName, @RequestBody AttributeTemplateRequest request) {
        return ApiResponse.<AttributeTemplateResponse>builder()
                .result(attributeTemplateService.update(productTypeName, request))
                .build();
    }
}
