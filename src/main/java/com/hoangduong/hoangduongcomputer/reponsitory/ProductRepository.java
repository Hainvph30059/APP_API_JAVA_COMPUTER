package com.hoangduong.hoangduongcomputer.reponsitory;

import java.util.List;
import java.util.Optional;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.data.mongodb.repository.Query;
import org.springframework.stereotype.Repository;

import com.hoangduong.hoangduongcomputer.entity.Product;
import com.hoangduong.hoangduongcomputer.reponsitory.custom.ProductRepositoryCustom;

@Repository
public interface ProductRepository extends MongoRepository<Product, String>, ProductRepositoryCustom {
    @Override
    Optional<Product> findById(String id);
    Page<Product> findByProductTypeName(String productTypeName, Pageable pageable);

}
