package com.hoangduong.hoangduongcomputer.controller;

import com.hoangduong.hoangduongcomputer.dto.ApiResponse;
import com.hoangduong.hoangduongcomputer.dto.response.GHNDistrictResponse;
import com.hoangduong.hoangduongcomputer.dto.response.GHNProvinceResponse;
import com.hoangduong.hoangduongcomputer.dto.response.GHNWardResponse;
import com.hoangduong.hoangduongcomputer.exception.AppException;
import com.hoangduong.hoangduongcomputer.exception.ErrorCode;
import com.hoangduong.hoangduongcomputer.service.GHNShippingService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/ghn")
@RequiredArgsConstructor
@Slf4j
public class GHNLocationController {

    private final GHNShippingService ghnShippingService;

    /**
     * Lấy danh sách tỉnh/thành
     */
    @GetMapping("/provinces")
    ApiResponse<List<GHNProvinceResponse.GHNProvince>> getProvinces() {

        return ApiResponse.<List<GHNProvinceResponse.GHNProvince>>builder()
                .result(ghnShippingService.getProvinces())
                .build();
    }

    /**
     * Lấy danh sách quận/huyện theo tỉnh
     */
    @GetMapping("/districts")
    ApiResponse<List<GHNDistrictResponse.GHNDistrict>> getDistricts(
            @RequestParam(value = "provinceId") Integer provinceId) {

        return ApiResponse.<List<GHNDistrictResponse.GHNDistrict>>builder()
                .result(ghnShippingService.getDistricts(provinceId))
                .build();
    }

    /**
     * Lấy danh sách phường/xã theo quận
     */
    @GetMapping("/wards")
    ApiResponse<List<GHNWardResponse.GHNWard>> getWards(
            @RequestParam(value = "districtId") Integer districtId) {

        return ApiResponse.<List<GHNWardResponse.GHNWard>>builder()
                .result(ghnShippingService.getWards(districtId))
                .build();
    }
}

