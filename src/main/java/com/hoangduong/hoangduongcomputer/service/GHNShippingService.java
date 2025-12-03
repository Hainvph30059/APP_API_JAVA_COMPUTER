package com.hoangduong.hoangduongcomputer.service;

import com.hoangduong.hoangduongcomputer.dto.ProductDimensions;
import com.hoangduong.hoangduongcomputer.dto.request.GHNShippingFeeRequest;
import com.hoangduong.hoangduongcomputer.dto.response.*;
import com.hoangduong.hoangduongcomputer.entity.ShippingAddress;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.http.*;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.client.RestClientException;

import java.util.List;

// ==================== GHN SHIPPING SERVICE ====================

@Service
@Slf4j
@RequiredArgsConstructor
public class GHNShippingService {

    @Autowired
    private final RestTemplate restTemplate;

    private static final String GHN_API_BASE = "https://dev-online-gateway.ghn.vn/shiip/public-api";
    private static final String SHOP_ID = "198218";
    private static final String TOKEN = "adcba9a1-cb3b-11f0-b989-ea7e29c7fb39";
    private static final Integer DEFAULT_SERVICE_TYPE = 2;

    public Double calculateShippingFee(ShippingAddress address, Double insuranceValue) {
        try {
            ProductDimensions dimensions = calculateProductDimensions(insuranceValue);

            GHNShippingFeeRequest request = GHNShippingFeeRequest.builder()
                    .serviceTypeId(DEFAULT_SERVICE_TYPE)
                    .toDistrictId(address.getDistrictId())
                    .toWardCode(address.getWardCode())
                    .length(dimensions.getLength())
                    .width(dimensions.getWidth())
                    .height(dimensions.getHeight())
                    .weight(dimensions.getWeight())
                    .insuranceValue(insuranceValue.intValue())
                    .coupon(null)
                    .build();

            HttpHeaders headers = createGHNHeaders();
            HttpEntity<GHNShippingFeeRequest> entity = new HttpEntity<>(request, headers);

            String url = GHN_API_BASE + "/v2/shipping-order/fee";

            ResponseEntity<GHNShippingFeeResponse> response = restTemplate.exchange(
                    url, HttpMethod.POST, entity, GHNShippingFeeResponse.class);

            if (response.getBody() != null && response.getBody().getCode() == 200) {
                Integer totalFee = response.getBody().getData().getTotal();
                log.info("Calculated shipping fee: {} VND for district: {}, ward: {}",
                        totalFee, address.getDistrictId(), address.getWardCode());
                return totalFee.doubleValue();
            }

            log.error("GHN API returned error: {}", response.getBody());
            return getDefaultShippingFee();

        } catch (RestClientException e) {
            log.error("Error calling GHN API: {}", e.getMessage());
            return getDefaultShippingFee();
        }
    }

    private ProductDimensions calculateProductDimensions(Double orderValue) {
        int weight;
        if (orderValue < 500000) weight = 500;
        else if (orderValue < 2000000) weight = 1000;
        else if (orderValue < 5000000) weight = 2000;
        else weight = 3000;

        return ProductDimensions.builder()
                .length(30).width(40).height(20).weight(weight).build();
    }

    private Double getDefaultShippingFee() {
        return 30000.0;
    }

    public List<GHNProvinceResponse.GHNProvince> getProvinces() {
        try {
            HttpHeaders headers = createGHNHeaders();
            HttpEntity<?> entity = new HttpEntity<>(headers);

            String url = GHN_API_BASE + "/master-data/province";

            ResponseEntity<GHNProvinceResponse> response = restTemplate.exchange(
                    url, HttpMethod.GET, entity, GHNProvinceResponse.class);

            if (response.getBody() != null && response.getBody().getCode() == 200) {
                return response.getBody().getData();
            }
            return List.of();
        } catch (RestClientException e) {
            log.error("Error getting provinces from GHN: {}", e.getMessage());
            return List.of();
        }
    }

    public List<GHNDistrictResponse.GHNDistrict> getDistricts(Integer provinceId) {
        try {
            HttpHeaders headers = createGHNHeaders();
            HttpEntity<?> entity = new HttpEntity<>(headers);

            String url = GHN_API_BASE + "/master-data/district?province_id=" + provinceId;

            ResponseEntity<GHNDistrictResponse> response = restTemplate.exchange(
                    url, HttpMethod.GET, entity, GHNDistrictResponse.class);

            if (response.getBody() != null && response.getBody().getCode() == 200) {
                return response.getBody().getData();
            }
            return List.of();
        } catch (RestClientException e) {
            log.error("Error getting districts from GHN: {}", e.getMessage());
            return List.of();
        }
    }

    public List<GHNWardResponse.GHNWard> getWards(Integer districtId) {
        try {
            HttpHeaders headers = createGHNHeaders();
            HttpEntity<?> entity = new HttpEntity<>(headers);

            String url = GHN_API_BASE + "/master-data/ward?district_id=" + districtId;

            ResponseEntity<GHNWardResponse> response = restTemplate.exchange(
                    url, HttpMethod.GET, entity, GHNWardResponse.class);

            if (response.getBody() != null && response.getBody().getCode() == 200) {
                return response.getBody().getData();
            }
            return List.of();
        } catch (RestClientException e) {
            log.error("Error getting wards from GHN: {}", e.getMessage());
            return List.of();
        }
    }

    private HttpHeaders createGHNHeaders() {
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        headers.set("Token", TOKEN);
        headers.set("ShopId", SHOP_ID);
        return headers;
    }
}
