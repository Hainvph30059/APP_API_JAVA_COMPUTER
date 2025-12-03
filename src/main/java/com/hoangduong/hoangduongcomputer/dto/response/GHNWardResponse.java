package com.hoangduong.hoangduongcomputer.dto.response;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;

import java.util.List;

@Data
public class GHNWardResponse {
    Integer code;
    String message;
    List<GHNWard> data;

    @Data
    public static class GHNWard {
        @JsonProperty("WardCode")
        String wardCode;

        @JsonProperty("DistrictID")
        Integer districtID;

        @JsonProperty("WardName")
        String wardName;

        @JsonProperty("NameExtension")
        List<String> nameExtension;
    }
}
