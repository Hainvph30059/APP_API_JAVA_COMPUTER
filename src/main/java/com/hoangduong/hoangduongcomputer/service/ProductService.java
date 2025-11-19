package com.hoangduong.hoangduongcomputer.service;

import java.io.File;
import java.io.IOException;
import java.util.List;
import java.util.Optional;

import com.hoangduong.hoangduongcomputer.entity.Product;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.FileSystemResource;
import org.springframework.core.io.Resource;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import com.hoangduong.hoangduongcomputer.dto.PageResponse;
import com.hoangduong.hoangduongcomputer.dto.request.ProductRequest;
import com.hoangduong.hoangduongcomputer.dto.response.ImageDataResponse;
import com.hoangduong.hoangduongcomputer.dto.response.ProductResponse;
import com.hoangduong.hoangduongcomputer.exception.AppException;
import com.hoangduong.hoangduongcomputer.exception.ErrorCode;
import com.hoangduong.hoangduongcomputer.mapper.ProductMapper;
import com.hoangduong.hoangduongcomputer.reponsitory.ImageStorageRepository;
import com.hoangduong.hoangduongcomputer.reponsitory.ProductRepository;

import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import lombok.experimental.NonFinal;
import lombok.extern.slf4j.Slf4j;

@Service
@RequiredArgsConstructor
@FieldDefaults(level = lombok.AccessLevel.PRIVATE, makeFinal = true)
@Slf4j
public class ProductService {
    ProductRepository productRepository;
    ImageStorageRepository imageStorageRepository;

    ProductMapper productMapper;

    @Value("${app.file.storage-dir}")
    @NonFinal
    String storageDir;

    public ProductResponse create(ProductRequest request, List<MultipartFile> fileImages) throws IOException {
        var product = productMapper.toEntity(request);
        var fileInfo = imageStorageRepository.store(fileImages);
        product.setUrls(fileInfo.getUrls());
        return productMapper.toResponse(productRepository.save(product));
    }

    public ProductResponse getById(String productId) {
        var product = productRepository
                .findById(productId)
                .orElseThrow(() -> new AppException(ErrorCode.PRODUCT_NOT_EXISTED));

        return productMapper.toResponse(product);
    }

    public PageResponse<ProductResponse> getAll(int page, int size) {
        Pageable pageable = PageRequest.of(page - 1, size);
        var pageData = productRepository.findAll(pageable);

        return PageResponse.<ProductResponse>builder()
                .currentPage(page)
                .pageSize(pageData.getSize())
                .totalElements(pageData.getTotalElements())
                .totalPages(pageData.getTotalPages())
                .data(pageData.getContent().stream()
                        .map(productMapper::toResponse)
                        .toList())
                .build();
    }

    public PageResponse<ProductResponse> getByProductTypeName(String productTypeName, int page, int size) {
        Pageable pageable = PageRequest.of(page - 1, size);
        var pageData = productRepository.findByProductTypeName(productTypeName, pageable);

        return PageResponse.<ProductResponse>builder()
                .currentPage(page)
                .pageSize(pageData.getSize())
                .totalElements(pageData.getTotalElements())
                .totalPages(pageData.getTotalPages())
                .data(pageData.getContent().stream()
                        .map(productMapper::toResponse)
                        .toList())
                .build();
    }

    public void delete(String productId) {
        var product = productRepository
                .findById(productId)
                .orElseThrow(() -> new AppException(ErrorCode.PRODUCT_NOT_EXISTED));
        productRepository.delete(product);
    }

    public ProductResponse update(String productId, ProductRequest request, List<MultipartFile> fileImages) {
        var product = productRepository
                .findById(productId)
                .orElseThrow(() -> new AppException(ErrorCode.PRODUCT_NOT_EXISTED));
        productMapper.updateEntity(product, request);

        Optional.ofNullable(fileImages)
                .map(files -> files.stream()
                        .filter(file -> file != null
                                && !file.isEmpty()
                                && !file.getOriginalFilename().isBlank())
                        .toList())
                .filter(validFiles -> !validFiles.isEmpty())
                .ifPresent(validFiles -> {
                    try {
                        var fileInfo = imageStorageRepository.store(validFiles);
                        product.getUrls().addAll(fileInfo.getUrls());
                    } catch (IOException e) {
                        throw new AppException(ErrorCode.FILE_STORAGE_ERROR);
                    }
                });
        return productMapper.toResponse(productRepository.save(product));
    }

    public ImageDataResponse serveProductImage(String fileName) {
        File file = new File(storageDir + File.separator + fileName);
        if (file.exists()) {
            Resource resource = new FileSystemResource(file);
            String contentType = determineContentType(fileName);
            return new ImageDataResponse(contentType, resource);
        } else {
            throw new AppException(ErrorCode.IMAGE_NOT_EXIST);
        }
    }

    private String determineContentType(String fileName) {
        if (fileName.endsWith(".jpg") || fileName.endsWith(".jpeg")) {
            return "image/jpeg";
        } else if (fileName.endsWith(".png")) {
            return "image/png";
        } else if (fileName.endsWith(".gif")) {
            return "image/gif";
        } else {
            return "application/octet-stream";
        }
    }

    // delete image url
    public void deleteImgageUrl(String productId, String urlDelete) {
        var product = productRepository
                .findById(productId)
                .orElseThrow(() -> new AppException(ErrorCode.PRODUCT_NOT_EXISTED));
        if (!product.getUrls().contains(urlDelete)) {
            throw new AppException(ErrorCode.IMAGE_NOT_EXIST);
        }
        var updateUrls =
                product.getUrls().stream().filter(url -> !url.equals(urlDelete)).toList();
        product.setUrls(updateUrls);
        productRepository.save(product);
    }


    public List<ProductResponse> getTopProducts(String type, int limit) {
        List<Product> topProducts = productRepository.findTopProductsByViewCount(type, limit);

        return topProducts.stream()
                .map(productMapper::toResponse)
                .toList();
    }
}
