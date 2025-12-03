package com.hoangduong.hoangduongcomputer.controller;

import com.hoangduong.hoangduongcomputer.dto.ApiResponse;
import com.hoangduong.hoangduongcomputer.dto.request.ShippingAddressRequest;
import com.hoangduong.hoangduongcomputer.entity.ShippingAddress;
import com.hoangduong.hoangduongcomputer.entity.User;
import com.hoangduong.hoangduongcomputer.service.ShippingAddressService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/shipping-addresses")
@RequiredArgsConstructor
@Slf4j
public class ShippingAddressController {

    private final ShippingAddressService shippingAddressService;

    /**
     * Lấy danh sách địa chỉ của user
     */
    @GetMapping
    ApiResponse<List<ShippingAddress>> getUserAddresses(
            @AuthenticationPrincipal String userId) {

        return ApiResponse.<List<ShippingAddress>>builder()
                .result(shippingAddressService.getUserAddresses(userId))
                .build();
    }

    /**
     * Tạo địa chỉ mới
     */
    @PostMapping
    ApiResponse<ShippingAddress> createAddress(
            @AuthenticationPrincipal String userId,
            @RequestBody ShippingAddressRequest request) {

        return ApiResponse.<ShippingAddress>builder()
                .result(shippingAddressService.createAddress(userId, request))
                .build();
    }

    /**
     * Cập nhật địa chỉ
     */
    @PutMapping("/{addressId}")
    ApiResponse<ShippingAddress> updateAddress(
            @AuthenticationPrincipal String userId,
            @PathVariable String addressId,
            @RequestBody ShippingAddressRequest request) {

        return ApiResponse.<ShippingAddress>builder()
                .result(shippingAddressService.updateAddress(userId, addressId, request))
                .build();
    }

    /**
     * Xóa địa chỉ
     */
    @DeleteMapping("/{addressId}")
    ApiResponse<Void> deleteAddress(
            @AuthenticationPrincipal String userId,
            @PathVariable String addressId) {

        shippingAddressService.deleteAddress(userId, addressId);

        return ApiResponse.<Void>builder()
                .build();
    }

    /**
     * Set địa chỉ mặc định
     */
    @PatchMapping("/{addressId}/default")
    ApiResponse<ShippingAddress> setDefaultAddress(
            @AuthenticationPrincipal String userId,
            @PathVariable String addressId) {

        return ApiResponse.<ShippingAddress>builder()
                .result(shippingAddressService.setDefaultAddress(userId, addressId))
                .build();
    }
}
