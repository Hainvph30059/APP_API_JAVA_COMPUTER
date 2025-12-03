package com.hoangduong.hoangduongcomputer.dto.response;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;

import java.util.List;

@Data
public class GHNDistrictResponse {
    Integer code;
    String message;
    List<GHNDistrict> data;

    @Data
    public static class GHNDistrict {
        @JsonProperty("DistrictID")
        Integer districtID;

        @JsonProperty("ProvinceID")
        Integer provinceID;

        @JsonProperty("DistrictName")
        String districtName;

        @JsonProperty("Code")
        Integer code;

        @JsonProperty("NameExtension")
        List<String> nameExtension;
    }
}
