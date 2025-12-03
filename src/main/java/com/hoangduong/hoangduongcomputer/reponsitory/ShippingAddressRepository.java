package com.hoangduong.hoangduongcomputer.reponsitory;

import com.hoangduong.hoangduongcomputer.entity.ShippingAddress;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface ShippingAddressRepository extends MongoRepository<ShippingAddress, String> {
    List<ShippingAddress> findByUserId(String userId);
    Optional<ShippingAddress> findByUserIdAndIsDefaultTrue(String userId);
}
