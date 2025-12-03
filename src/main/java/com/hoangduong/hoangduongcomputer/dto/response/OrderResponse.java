package com.hoangduong.hoangduongcomputer.dto.response;

import com.hoangduong.hoangduongcomputer.entity.AddressSnapshot;
import com.hoangduong.hoangduongcomputer.entity.OrderItem;
import com.hoangduong.hoangduongcomputer.entity.OrderTracking;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.Instant;
import java.util.List;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class OrderResponse {
    String id;
    String orderNumber;
    String userId;
    List<OrderItem> items;
    AddressSnapshot shippingAddress;
    Double subtotal;
    Double shippingFee;
    Double discount;
    Double totalAmount;
    String status;
    String paymentMethod;
    String paymentStatus;
    String note;
    List<OrderTracking> trackingHistory;
    Instant createdAt;
}
