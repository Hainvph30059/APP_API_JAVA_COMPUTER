package com.hoangduong.hoangduongcomputer.controller;

import java.io.IOException;
import java.util.List;

import org.springframework.core.io.Resource;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.hoangduong.hoangduongcomputer.dto.ApiResponse;
import com.hoangduong.hoangduongcomputer.dto.PageResponse;
import com.hoangduong.hoangduongcomputer.dto.request.ProductRequest;
import com.hoangduong.hoangduongcomputer.dto.response.ProductResponse;
import com.hoangduong.hoangduongcomputer.service.ProductService;

import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;

@RestController
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class ProductController {
    ProductService productService;
    ObjectMapper objectMapper;

    @PostMapping(value = "/product", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    ApiResponse<ProductResponse> create(
            @RequestParam("request") String requestJson, @RequestParam("file") List<MultipartFile> fileImages)
            throws IOException {
        var request = objectMapper.readValue(requestJson, ProductRequest.class);
        return ApiResponse.<ProductResponse>builder()
                .result(productService.create(request, fileImages))
                .build();
    }

    @GetMapping(value = "/product/{productId}")
    ApiResponse<ProductResponse> getById(@PathVariable String productId) {
        return ApiResponse.<ProductResponse>builder()
                .result(productService.getById(productId))
                .build();
    }

    @GetMapping(value = "/product")
    ApiResponse<PageResponse<ProductResponse>> getAll(
            @RequestParam(value = "page", required = false, defaultValue = "1") int page,
            @RequestParam(value = "size", required = false, defaultValue = "10") int size) {
        return ApiResponse.<PageResponse<ProductResponse>>builder()
                .result(productService.getAll(page, size))
                .build();
    }

    @GetMapping(value = "/product/product-type-name/{productTypeName}")
    ApiResponse<PageResponse<ProductResponse>> getByProductTypeName(
            @PathVariable String productTypeName,
            @RequestParam(value = "page", required = false, defaultValue = "1") int page,
            @RequestParam(value = "size", required = false, defaultValue = "10") int size) {
        return ApiResponse.<PageResponse<ProductResponse>>builder()
                .result(productService.getByProductTypeName(productTypeName,page, size))
                .build();
    }

    @PutMapping(value = "/product/{productId}", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    ApiResponse<ProductResponse> update(
                @PathVariable String productId,
                @RequestParam("request") String requestJson,
                @RequestParam(value = "file", required = false) List<MultipartFile> fileImages)
            throws IOException {
        var request = objectMapper.readValue(requestJson, ProductRequest.class);
        return ApiResponse.<ProductResponse>builder()
                .result(productService.update(productId, request, fileImages))
                .build();
    }

    @DeleteMapping(value = "/product/{productId}")
    ApiResponse<String> delete(@PathVariable String productId) {
        productService.delete(productId);
        return ApiResponse.<String>builder()
                .result("Delete product successfully")
                .build();
    }

    @GetMapping("product/image/{fileName:.+}")
    public ResponseEntity<Resource> serveFile(@PathVariable String fileName) {
        var imageresponse = productService.serveProductImage(fileName);
        if (imageresponse != null) {
            return ResponseEntity.ok()
                    .contentType(MediaType.parseMediaType(imageresponse.contentType()))
                    .body(imageresponse.resource());
        } else {
            return ResponseEntity.notFound().build();
        }
    }

    @DeleteMapping("product/image")
    public ResponseEntity<Void> deleteImageUrl(@RequestParam String productId, @RequestParam String url) {
        productService.deleteImgageUrl(productId, url);
        return ResponseEntity.noContent().build();
    }

    @GetMapping("product/view-count/top")
    ApiResponse<List<ProductResponse>> getTopProducts(
        @RequestParam(value = "type", required = false) String type,
        @RequestParam(value = "limit", required = false, defaultValue = "10") int limit)
        {
        return ApiResponse.<List<ProductResponse>>builder()
                .result(productService.getTopProducts(type, limit))
                .build();
    }

}
