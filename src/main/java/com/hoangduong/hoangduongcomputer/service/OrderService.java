package com.hoangduong.hoangduongcomputer.service;

import com.hoangduong.hoangduongcomputer.dto.request.CreateOrderRequest;
import com.hoangduong.hoangduongcomputer.dto.request.OrderItemRequest;
import com.hoangduong.hoangduongcomputer.dto.response.OrderResponse;
import com.hoangduong.hoangduongcomputer.entity.*;
import com.hoangduong.hoangduongcomputer.exception.AppException;
import com.hoangduong.hoangduongcomputer.exception.ErrorCode;
import com.hoangduong.hoangduongcomputer.reponsitory.OrderRepository;
import com.hoangduong.hoangduongcomputer.reponsitory.ProductRepository;
import com.hoangduong.hoangduongcomputer.reponsitory.ShippingAddressRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Instant;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Slf4j
public class OrderService {

    private final OrderRepository orderRepository;
    private final ProductRepository productRepository;
    private final ShippingAddressRepository shippingAddressRepository;
    private final OrderTrackingSimulator orderTrackingSimulator;
    private final GHNShippingService ghnShippingService;
    private final OrderStatusUpdater orderStatusUpdater; // ✅ Thêm dependency mới

    /**
     * Tạo đơn hàng mới
     */
    @Transactional
    public OrderResponse createOrder(String userId, CreateOrderRequest request) {
        log.info("Creating order for user: {}", userId);

        // 1. Validate và lấy địa chỉ giao hàng
        ShippingAddress shippingAddress = shippingAddressRepository
                .findById(request.getShippingAddressId())
                .orElseThrow(() -> new AppException(ErrorCode.SHIPPING_ADDRESS_NOT_FOUND));

        if (!shippingAddress.getUserId().equals(userId)) {
            throw new AppException(ErrorCode.INVALID_SHIPPING_ADDRESS);
        }

        // 2. Validate và tạo OrderItems
        List<OrderItem> orderItems = request.getItems().stream()
                .map(this::createOrderItem)
                .collect(Collectors.toList());

        // 3. Tính toán giá
        double subtotal = orderItems.stream()
                .mapToDouble(OrderItem::getSubtotal)
                .sum();

        double shippingFee = calculateShippingFee(shippingAddress, subtotal);
        double discount = calculateDiscount(request.getVoucherCode(), subtotal);
        double totalAmount = subtotal + shippingFee - discount;

        // 4. Tạo tracking history ban đầu
        List<OrderTracking> trackingHistory = new ArrayList<>();
        trackingHistory.add(OrderTracking.builder()
                .status(Order.OrderStatus.PENDING.getValue())
                .description("Đơn hàng đã được tạo và đang chờ xác nhận")
                .location("Hệ thống")
                .updatedBy("SYSTEM")
                .build());

        // 5. Tạo order
        Order order = Order.builder()
                .orderNumber(generateOrderNumber())
                .userId(userId)
                .items(orderItems)
                .shippingAddressId(shippingAddress.getId())
                .shippingAddressSnapshot(shippingAddress.toSnapshot())
                .subtotal(subtotal)
                .shippingFee(shippingFee)
                .discount(discount)
                .totalAmount(totalAmount)
                .paymentMethod(request.getPaymentMethod())
                .paymentStatus(Order.PaymentStatus.UNPAID.getValue())
                .voucherCode(request.getVoucherCode())
                .note(request.getNote())
                .trackingHistory(trackingHistory)
                .build();

        // 6. Trừ stock cho từng sản phẩm
        updateProductStock(orderItems);

        // 7. Lưu order
        Order savedOrder = orderRepository.save(order);

        // 8. Bắt đầu simulation tracking (nếu được yêu cầu)
        if (request.getAutoSimulate() != null && request.getAutoSimulate()) {
            orderTrackingSimulator.startSimulation(savedOrder.getId());
        }

        log.info("Order created successfully: {}", savedOrder.getOrderNumber());

        return toOrderResponse(savedOrder);
    }

    /**
     * Tạo OrderItem từ request
     */
    private OrderItem createOrderItem(OrderItemRequest itemRequest) {
        Product product = productRepository.findById(itemRequest.getProductId())
                .orElseThrow(() -> new AppException(ErrorCode.PRODUCT_NOT_EXISTED));

        if (product.getStock() < itemRequest.getQuantity()) {
            throw new AppException(ErrorCode.INSUFFICIENT_STOCK);
        }

        String imageUrl = (product.getUrls() != null && !product.getUrls().isEmpty())
                ? product.getUrls().get(0)
                : null;

        return OrderItem.builder()
                .productId(product.getId())
                .productName(product.getName())
                .productTypeName(product.getProductTypeName())
                .price(product.getPrice())
                .quantity(itemRequest.getQuantity())
                .imageUrl(imageUrl)
                .attributes(itemRequest.getAttributes())
                .build();
    }

    /**
     * Cập nhật stock cho các sản phẩm
     */
    private void updateProductStock(List<OrderItem> orderItems) {
        for (OrderItem item : orderItems) {
            Product product = productRepository.findById(item.getProductId())
                    .orElseThrow(() -> new AppException(ErrorCode.PRODUCT_NOT_EXISTED));

            int newStock = product.getStock() - item.getQuantity();
            if (newStock < 0) {
                throw new AppException(ErrorCode.INSUFFICIENT_STOCK);
            }

            product.setStock(newStock);
            productRepository.save(product);
        }
    }

    /**
     * Tính phí ship từ GHN hoặc fallback
     */
    private double calculateShippingFee(ShippingAddress address, double subtotal) {
        try {
            return ghnShippingService.calculateShippingFee(address, subtotal);
        } catch (Exception e) {
            log.error("Error calculating shipping fee from GHN, using default", e);
            // Fallback: phí ship mặc định
            if (address.getProvinceId() != null &&
                    (address.getProvinceId() == 201 || address.getProvinceId() == 202)) {
                // Hà Nội (201) hoặc HCM (202) free ship
                return 0.0;
            }
            return 30000.0;
        }
    }

    /**
     * Tính discount từ voucher
     */
    private double calculateDiscount(String voucherCode, double subtotal) {
        if (voucherCode == null || voucherCode.isEmpty()) {
            return 0.0;
        }
        // TODO: Implement voucher logic
        return 0.0;
    }

    /**
     * Generate order number duy nhất
     */
    private String generateOrderNumber() {
        String timestamp = LocalDateTime.now()
                .format(DateTimeFormatter.ofPattern("yyyyMMddHHmmss"));
        return "ORD" + timestamp;
    }

    /**
     * Lấy danh sách đơn hàng của user
     */
    public List<OrderResponse> getUserOrders(String userId) {
        return orderRepository.findByUserIdOrderByCreatedAtDesc(userId)
                .stream()
                .map(this::toOrderResponse)
                .collect(Collectors.toList());
    }

    /**
     * Lấy chi tiết đơn hàng
     */
    public OrderResponse getOrderById(String userId, String orderId) {
        Order order = orderRepository.findById(orderId)
                .orElseThrow(() -> new AppException(ErrorCode.ORDER_NOT_FOUND));

        if (!order.getUserId().equals(userId)) {
            throw new AppException(ErrorCode.ORDER_ACCESS_DENIED);
        }

        return toOrderResponse(order);
    }

    /**
     * Hủy đơn hàng
     */
    @Transactional
    public OrderResponse cancelOrder(String userId, String orderId, String reason) {
        Order order = orderRepository.findById(orderId)
                .orElseThrow(() -> new AppException(ErrorCode.ORDER_NOT_FOUND));

        if (!order.getUserId().equals(userId)) {
            throw new AppException(ErrorCode.ORDER_ACCESS_DENIED);
        }

        // Chỉ cho phép hủy đơn ở trạng thái PENDING hoặc CONFIRMED
        if (!order.getStatus().equals(Order.OrderStatus.PENDING.getValue()) &&
                !order.getStatus().equals(Order.OrderStatus.CONFIRMED.getValue())) {
            throw new AppException(ErrorCode.INVALID_ORDER_STATUS);
        }

        // Hoàn lại stock
        restoreProductStock(order.getItems());

        // Cập nhật trạng thái
        order.setStatus(Order.OrderStatus.CANCELLED.getValue());
        order.setCancelReason(reason);
        order.setCancelledAt(Instant.now());
        order.setCancelledBy("USER");
        order.setUpdatedAt(Instant.now());

        // Thêm tracking
        order.getTrackingHistory().add(OrderTracking.builder()
                .status(Order.OrderStatus.CANCELLED.getValue())
                .description("Đơn hàng đã bị hủy: " + reason)
                .location("Hệ thống")
                .updatedBy("USER")
                .build());

        Order savedOrder = orderRepository.save(order);

        return toOrderResponse(savedOrder);
    }

    /**
     * Hoàn lại stock khi hủy đơn
     */
    private void restoreProductStock(List<OrderItem> orderItems) {
        for (OrderItem item : orderItems) {
            Product product = productRepository.findById(item.getProductId())
                    .orElse(null);

            if (product != null) {
                product.setStock(product.getStock() + item.getQuantity());
                productRepository.save(product);
            }
        }
    }

    /**
     * Cập nhật trạng thái đơn hàng (dành cho ADMIN hoặc SYSTEM)
     * Delegate sang OrderStatusUpdater để tránh circular dependency
     */
    @Transactional
    public OrderResponse updateOrderStatus(String orderId, String newStatus,
                                           String description, String location, String updatedBy) {
        // ✅ Delegate sang OrderStatusUpdater thay vì tự xử lý
        Order savedOrder = orderStatusUpdater.updateOrderStatus(
                orderId, newStatus, description, location, updatedBy
        );
        return toOrderResponse(savedOrder);
    }

    /**
     * Convert Order sang OrderResponse
     */
    private OrderResponse toOrderResponse(Order order) {
        return OrderResponse.builder()
                .id(order.getId())
                .orderNumber(order.getOrderNumber())
                .userId(order.getUserId())
                .items(order.getItems())
                .shippingAddress(order.getShippingAddressSnapshot())
                .subtotal(order.getSubtotal())
                .shippingFee(order.getShippingFee())
                .discount(order.getDiscount())
                .totalAmount(order.getTotalAmount())
                .status(order.getStatus())
                .paymentMethod(order.getPaymentMethod())
                .paymentStatus(order.getPaymentStatus())
                .note(order.getNote())
                .trackingHistory(order.getTrackingHistory())
                .createdAt(order.getCreatedAt())
                .build();
    }
}
