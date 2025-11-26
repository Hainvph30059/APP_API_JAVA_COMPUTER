package com.hoangduong.hoangduongcomputer.reponsitory;

import com.hoangduong.hoangduongcomputer.entity.CartDetail;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface CartDetailRepository extends MongoRepository<CartDetail, String> {
}
