package com.hoangduong.hoangduongcomputer.reponsitory.custom;

import java.util.List;

import org.bson.types.ObjectId;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.data.mongodb.core.query.Update;
import org.springframework.stereotype.Component;

import com.hoangduong.hoangduongcomputer.entity.Product;
import com.mongodb.client.result.UpdateResult;

import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import lombok.extern.slf4j.Slf4j;

@Component
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
@Slf4j
public class ProductRepositoryCustomImpl implements ProductRepositoryCustom {
    MongoTemplate mongoTemplate;

    @Override
    public void addImageUrl(String productId, List<String> urls) {
        Query query = new Query(Criteria.where("_id").is(new ObjectId(productId)));
        Update update = new Update().addToSet("urls").each(urls.toArray());

        UpdateResult result = mongoTemplate.updateFirst(query, update, Product.class);
        log.info("Update result: Matched={}, Modified={}", result.getMatchedCount(), result.getModifiedCount());
        log.info("url added: {}", urls);
        log.info("productId: {}", new ObjectId(productId));
    }
}
