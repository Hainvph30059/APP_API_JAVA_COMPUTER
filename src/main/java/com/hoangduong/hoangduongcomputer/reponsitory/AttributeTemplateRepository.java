package com.hoangduong.hoangduongcomputer.reponsitory;

import java.util.Optional;

import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

import com.hoangduong.hoangduongcomputer.entity.AttributeTemplates;

@Repository
public interface AttributeTemplateRepository extends MongoRepository<AttributeTemplates, String> {
    Optional<AttributeTemplates> findByProductTypeName(String productTypeId);
}
