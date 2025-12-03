package com.hoangduong.hoangduongcomputer.dto.request;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ShippingAddressRequest {
    String recipientName;
    String phoneNumber;
    String email;
    String addressDetail;
    Integer provinceId;
    String provinceName;
    Integer districtId;
    String districtName;
    String wardCode;
    String wardName;
    Boolean isDefault;
}
