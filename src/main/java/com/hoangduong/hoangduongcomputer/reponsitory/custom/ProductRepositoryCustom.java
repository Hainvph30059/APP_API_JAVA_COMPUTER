package com.hoangduong.hoangduongcomputer.reponsitory.custom;

import java.util.List;

public interface ProductRepositoryCustom {
    void addImageUrl(String productId, List<String> urls);
}
