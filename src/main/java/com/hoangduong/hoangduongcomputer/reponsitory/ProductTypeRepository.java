package com.hoangduong.hoangduongcomputer.reponsitory;

import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

import com.hoangduong.hoangduongcomputer.entity.ProductType;

import java.util.Optional;

@Repository
public interface ProductTypeRepository extends MongoRepository<ProductType, String> {
    Optional<ProductType> findByTypeName(String typeName);
}
