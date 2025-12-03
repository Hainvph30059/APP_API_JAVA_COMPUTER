package com.hoangduong.hoangduongcomputer.service;

import com.hoangduong.hoangduongcomputer.dto.request.ShippingAddressRequest;
import com.hoangduong.hoangduongcomputer.entity.ShippingAddress;
import com.hoangduong.hoangduongcomputer.exception.AppException;
import com.hoangduong.hoangduongcomputer.exception.ErrorCode;
import com.hoangduong.hoangduongcomputer.reponsitory.ShippingAddressRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Instant;
import java.util.List;

@Service
@RequiredArgsConstructor
@Slf4j
public class ShippingAddressService {

    private final ShippingAddressRepository shippingAddressRepository;

    public List<ShippingAddress> getUserAddresses(String userId) {
        return shippingAddressRepository.findByUserId(userId);
    }

    @Transactional
    public ShippingAddress createAddress(String userId, ShippingAddressRequest request) {
        // Validate dữ liệu địa chỉ
        validateAddressRequest(request);

        if (Boolean.TRUE.equals(request.getIsDefault())) {
            unsetDefaultAddress(userId);
        }

        ShippingAddress address = ShippingAddress.builder()
                .userId(userId)
                .recipientName(request.getRecipientName())
                .phoneNumber(request.getPhoneNumber())
                .email(request.getEmail())
                .addressDetail(request.getAddressDetail())
                .provinceId(request.getProvinceId())
                .provinceName(request.getProvinceName())
                .districtId(request.getDistrictId())
                .districtName(request.getDistrictName())
                .wardCode(request.getWardCode())
                .wardName(request.getWardName())
                .isDefault(request.getIsDefault() != null ? request.getIsDefault() : false)
                .build();

        return shippingAddressRepository.save(address);
    }

    @Transactional
    public ShippingAddress updateAddress(String userId, String addressId, ShippingAddressRequest request) {
        ShippingAddress address = shippingAddressRepository.findById(addressId)
                .orElseThrow(() -> new AppException(ErrorCode.SHIPPING_ADDRESS_NOT_FOUND));

        if (!address.getUserId().equals(userId)) {
            throw new AppException(ErrorCode.ORDER_ACCESS_DENIED);
        }

        // Validate dữ liệu địa chỉ
        validateAddressRequest(request);

        if (Boolean.TRUE.equals(request.getIsDefault())) {
            unsetDefaultAddress(userId);
        }

        address.setRecipientName(request.getRecipientName());
        address.setPhoneNumber(request.getPhoneNumber());
        address.setEmail(request.getEmail());
        address.setAddressDetail(request.getAddressDetail());
        address.setProvinceId(request.getProvinceId());
        address.setProvinceName(request.getProvinceName());
        address.setDistrictId(request.getDistrictId());
        address.setDistrictName(request.getDistrictName());
        address.setWardCode(request.getWardCode());
        address.setWardName(request.getWardName());
        address.setIsDefault(address.getIsDefault());
        address.setUpdatedAt(Instant.now());

        return shippingAddressRepository.save(address);
    }

    public void deleteAddress(String userId, String addressId) {
        ShippingAddress address = shippingAddressRepository.findById(addressId)
                .orElseThrow(() -> new AppException(ErrorCode.SHIPPING_ADDRESS_NOT_FOUND));

        if (!address.getUserId().equals(userId)) {
            throw new AppException(ErrorCode.ORDER_ACCESS_DENIED);
        }

        shippingAddressRepository.delete(address);
    }

    @Transactional
    public ShippingAddress setDefaultAddress(String userId, String addressId) {
        ShippingAddress address = shippingAddressRepository.findById(addressId)
                .orElseThrow(() -> new AppException(ErrorCode.SHIPPING_ADDRESS_NOT_FOUND));

        if (!address.getUserId().equals(userId)) {
            throw new AppException(ErrorCode.ORDER_ACCESS_DENIED);
        }

        unsetDefaultAddress(userId);

        address.setIsDefault(true);
        address.setUpdatedAt(Instant.now());

        return shippingAddressRepository.save(address);
    }

    private void unsetDefaultAddress(String userId) {
        List<ShippingAddress> addresses = shippingAddressRepository.findByUserId(userId);
        addresses.forEach(addr -> {
            if (Boolean.TRUE.equals(addr.getIsDefault())) {
                addr.setIsDefault(false);
                shippingAddressRepository.save(addr);
            }
        });
    }

    /**
     * Validate thông tin địa chỉ từ GHN
     */
    private void validateAddressRequest(ShippingAddressRequest request) {
        if (request.getProvinceId() == null || request.getProvinceName() == null ||
                request.getDistrictId() == null || request.getDistrictName() == null ||
                request.getWardCode() == null || request.getWardName() == null) {
            throw new AppException(ErrorCode.INVALID_SHIPPING_ADDRESS);
        }
    }
}
