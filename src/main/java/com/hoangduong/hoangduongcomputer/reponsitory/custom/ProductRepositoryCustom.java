package com.hoangduong.hoangduongcomputer.reponsitory.custom;

import com.hoangduong.hoangduongcomputer.entity.Product;

import java.util.List;

public interface ProductRepositoryCustom {
    void addImageUrl(String productId, List<String> urls);

    List<Product> findTopProductsByViewCount(String type, int limit);
}
