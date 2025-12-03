package com.hoangduong.hoangduongcomputer.entity;

import lombok.*;
import lombok.experimental.FieldDefaults;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;
import org.springframework.data.mongodb.core.mapping.Field;
import java.time.Instant;
import java.util.ArrayList;
import java.util.List;
import java.util.HashMap;
import java.util.Map;

// ==================== ORDER ENTITY ====================
@Data
@Document(collection = "orders")
@Builder
@NoArgsConstructor
@AllArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)
public class Order {

    @Id
    String id;

    @Field("orderNumber")
    String orderNumber;

    @Field("userId")
    String userId;

    @Builder.Default
    List<OrderItem> items = new ArrayList<>();

    @Field("shippingAddressId")
    String shippingAddressId;

    @Field("shippingAddressSnapshot")
    AddressSnapshot shippingAddressSnapshot;

    Double subtotal;
    Double shippingFee;
    Double discount;
    Double totalAmount;

    @Builder.Default
    @Field("status")
    String status = OrderStatus.PENDING.getValue();

    @Field("paymentMethod")
    String paymentMethod;

    @Field("paymentStatus")
    @Builder.Default
    String paymentStatus = PaymentStatus.UNPAID.getValue();

    @Field("transactionId")
    String transactionId;

    @Field("voucherCode")
    String voucherCode;

    String note;

    @Builder.Default
    @Field("trackingHistory")
    List<OrderTracking> trackingHistory = new ArrayList<>();

    @Builder.Default
    @Field("createdAt")
    Instant createdAt = Instant.now();

    @Field("updatedAt")
    Instant updatedAt;

    @Field("confirmedAt")
    Instant confirmedAt;

    @Field("paidAt")
    Instant paidAt;

    @Field("shippedAt")
    Instant shippedAt;

    @Field("deliveredAt")
    Instant deliveredAt;

    @Field("completedAt")
    Instant completedAt;

    @Field("cancelledAt")
    Instant cancelledAt;

    String cancelReason;

    @Field("cancelledBy")
    String cancelledBy;

    @Getter
    public enum OrderStatus {
        PENDING("pending"),
        CONFIRMED("confirmed"),
        PROCESSING("processing"),
        SHIPPING("shipping"),
        DELIVERED("delivered"),
        COMPLETED("completed"),
        CANCELLED("cancelled"),
        RETURNED("returned"),
        REFUNDED("refunded");

        private final String value;
        OrderStatus(String value) { this.value = value; }
    }

    @Getter
    public enum PaymentStatus {
        UNPAID("unpaid"),
        PENDING("pending"),
        PAID("paid"),
        FAILED("failed"),
        REFUNDED("refunded");

        private final String value;
        PaymentStatus(String value) { this.value = value; }
    }

    @Getter
    public enum PaymentMethod {
        COD("cod"),
        MOMO("momo"),
        VNPAY("vnpay"),
        ZALOPAY("zalopay"),
        BANK_TRANSFER("bank_transfer");

        private final String value;
        PaymentMethod(String value) { this.value = value; }
    }
}
