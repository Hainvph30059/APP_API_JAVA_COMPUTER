package com.hoangduong.hoangduongcomputer.controller;

import java.util.List;

import org.springframework.web.bind.annotation.*;

import com.hoangduong.hoangduongcomputer.dto.ApiResponse;
import com.hoangduong.hoangduongcomputer.dto.request.ProductTypeRequest;
import com.hoangduong.hoangduongcomputer.dto.response.ProductTypeResponse;
import com.hoangduong.hoangduongcomputer.service.ProductTypeService;

import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;

@RestController
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class ProductTypeController {
    ProductTypeService productTypeService;

    @PostMapping("/product-type")
    ApiResponse<ProductTypeResponse> create(@RequestBody ProductTypeRequest request) {
        return ApiResponse.<ProductTypeResponse>builder()
                .result(productTypeService.create(request))
                .build();
    }

    @GetMapping("/product-type")
    ApiResponse<List<ProductTypeResponse>> getAll() {
        return ApiResponse.<List<ProductTypeResponse>>builder()
                .result(productTypeService.getAll())
                .build();
    }

    @PutMapping("/product-type")
    ApiResponse<ProductTypeResponse> update(@RequestBody ProductTypeRequest request) {
        return ApiResponse.<ProductTypeResponse>builder()
                .result(productTypeService.update(request))
                .build();
    }
}
