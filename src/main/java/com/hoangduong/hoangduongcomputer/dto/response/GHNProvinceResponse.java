package com.hoangduong.hoangduongcomputer.dto.response;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;

import java.util.List;

@Data
public class GHNProvinceResponse {
    Integer code;
    String message;
    List<GHNProvince> data;

    @Data
    public static class GHNProvince {
        @JsonProperty("ProvinceID")
        Integer provinceID;

        @JsonProperty("ProvinceName")
        String provinceName;

        @JsonProperty("Code")
        Integer code;

        @JsonProperty("NameExtension")
        List<String> nameExtension;
    }
}
