package com.hoangduong.hoangduongcomputer.entity;

import lombok.*;
import lombok.experimental.FieldDefaults;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)
public class AddressSnapshot {
    String recipientName;
    String phoneNumber;
    String email;
    String addressDetail;
    String wardName;
    String districtName;
    String provinceName;

    public String getFullAddress() {
        return String.format("%s, %s, %s, %s",
                addressDetail, wardName, districtName, provinceName);
    }
}
