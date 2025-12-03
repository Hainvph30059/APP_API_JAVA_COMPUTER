package com.hoangduong.hoangduongcomputer.entity;

import lombok.*;
import lombok.experimental.FieldDefaults;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;
import org.springframework.data.mongodb.core.mapping.Field;

import java.time.Instant;

@Data
@Document(collection = "shipping_addresses")
@Builder
@NoArgsConstructor
@AllArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)
public class ShippingAddress {

    @Id
    String id;

    @Field("userId")
    String userId;

    String recipientName;
    String phoneNumber;
    String email;
    String addressDetail;

    @Field("provinceId")
    Integer provinceId;
    String provinceName;

    @Field("districtId")
    Integer districtId;
    String districtName;

    @Field("wardCode")
    String wardCode;
    String wardName;

    @Builder.Default
    @Field("isDefault")
    Boolean isDefault = false;

    @Builder.Default
    @Field("createdAt")
    Instant createdAt = Instant.now();

    @Field("updatedAt")
    Instant updatedAt;

    public String getFullAddress() {
        return String.format("%s, %s, %s, %s",
                addressDetail, wardName, districtName, provinceName);
    }

    public AddressSnapshot toSnapshot() {
        return AddressSnapshot.builder()
                .recipientName(this.recipientName)
                .phoneNumber(this.phoneNumber)
                .email(this.email)
                .addressDetail(this.addressDetail)
                .provinceName(this.provinceName)
                .districtName(this.districtName)
                .wardName(this.wardName)
                .build();
    }
}
