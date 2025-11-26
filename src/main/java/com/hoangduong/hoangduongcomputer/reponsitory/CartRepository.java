package com.hoangduong.hoangduongcomputer.reponsitory;

import com.hoangduong.hoangduongcomputer.entity.Cart;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface CartRepository extends MongoRepository<Cart, String> {
}
