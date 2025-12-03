package com.hoangduong.hoangduongcomputer.dto.response;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;

@Data
public class GHNShippingFeeResponse {
    Integer code;
    String message;
    GHNShippingFeeData data;

    @Data
    public static class GHNShippingFeeData {
        Integer total;

        @JsonProperty("service_fee")
        Integer serviceFee;

        @JsonProperty("insurance_fee")
        Integer insuranceFee;

        @JsonProperty("pick_station_fee")
        Integer pickStationFee;

        @JsonProperty("coupon_value")
        Integer couponValue;

        @JsonProperty("r2s_fee")
        Integer r2sFee;
    }
}
