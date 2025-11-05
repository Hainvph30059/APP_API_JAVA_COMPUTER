package com.hoangduong.hoangduongcomputer.controller;

import java.util.List;

import org.springframework.web.bind.annotation.*;

import com.hoangduong.hoangduongcomputer.dto.ApiReponse;
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
    ApiReponse<ProductTypeResponse> create(@RequestBody ProductTypeRequest request) {
        return ApiReponse.<ProductTypeResponse>builder()
                .result(productTypeService.create(request))
                .build();
    }

    @GetMapping("/product-type")
    ApiReponse<List<ProductTypeResponse>> getAll() {
        return ApiReponse.<List<ProductTypeResponse>>builder()
                .result(productTypeService.getAll())
                .build();
    }

    @PutMapping("/product-type")
    ApiReponse<ProductTypeResponse> update(@RequestBody ProductTypeRequest request) {
        return ApiReponse.<ProductTypeResponse>builder()
                .result(productTypeService.update(request))
                .build();
    }
}
