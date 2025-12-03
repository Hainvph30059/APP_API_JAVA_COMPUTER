package com.hoangduong.hoangduongcomputer.dto.request;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class CreateOrderRequest {
    String shippingAddressId;
    List<OrderItemRequest> items;
    String paymentMethod;
    String voucherCode;
    String note;
    Boolean autoSimulate;
}
