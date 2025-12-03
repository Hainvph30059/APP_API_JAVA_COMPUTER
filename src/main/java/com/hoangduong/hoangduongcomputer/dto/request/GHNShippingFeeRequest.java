package com.hoangduong.hoangduongcomputer.dto.request;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class GHNShippingFeeRequest {
    @JsonProperty("service_type_id")
    Integer serviceTypeId;

    @JsonProperty("to_district_id")
    Integer toDistrictId;

    @JsonProperty("to_ward_code")
    String toWardCode;

    Integer length;
    Integer width;
    Integer height;
    Integer weight;

    @JsonProperty("insurance_value")
    Integer insuranceValue;

    String coupon;
}
