package com.hoangduong.hoangduongcomputer.service;

import com.hoangduong.hoangduongcomputer.entity.Order;
import com.hoangduong.hoangduongcomputer.entity.OrderTracking;
import com.hoangduong.hoangduongcomputer.exception.AppException;
import com.hoangduong.hoangduongcomputer.exception.ErrorCode;
import com.hoangduong.hoangduongcomputer.reponsitory.OrderRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Instant;

/**
 * Service tập trung xử lý việc cập nhật trạng thái đơn hàng
 * Được sử dụng bởi cả OrderService và OrderTrackingSimulator
 * để tránh circular dependency
 */
@Service
@RequiredArgsConstructor
@Slf4j
public class OrderStatusUpdater {

    private final OrderRepository orderRepository;

    /**
     * Cập nhật trạng thái đơn hàng
     */
    @Transactional
    public Order updateOrderStatus(String orderId, String newStatus,
                                   String description, String location, String updatedBy) {
        log.debug("Updating order {} to status {}", orderId, newStatus);

        Order order = orderRepository.findById(orderId)
                .orElseThrow(() -> new AppException(ErrorCode.ORDER_NOT_FOUND));

        // Validate transition
        if (!isValidStatusTransition(order.getStatus(), newStatus)) {
            throw new AppException(ErrorCode.INVALID_ORDER_TRANSITION);
        }

        order.setStatus(newStatus);
        order.setUpdatedAt(Instant.now());

        // Update specific timestamps
        updateOrderTimestamps(order, newStatus);

        // Add tracking
        order.getTrackingHistory().add(OrderTracking.builder()
                .status(newStatus)
                .description(description)
                .location(location)
                .updatedBy(updatedBy)
                .build());

        Order savedOrder = orderRepository.save(order);
        log.info("Order {} status updated to {}", orderId, newStatus);
        
        return savedOrder;
    }

    /**
     * Kiểm tra transition hợp lệ
     */
    private boolean isValidStatusTransition(String currentStatus, String newStatus) {
        // Nếu đang ở cùng trạng thái, cho phép (để update tracking)
        if (currentStatus.equals(newStatus)) {
            return true;
        }

        if (currentStatus.equals(Order.OrderStatus.PENDING.getValue())) {
            return newStatus.equals(Order.OrderStatus.CONFIRMED.getValue()) ||
                    newStatus.equals(Order.OrderStatus.CANCELLED.getValue());
        }
        if (currentStatus.equals(Order.OrderStatus.CONFIRMED.getValue())) {
            return newStatus.equals(Order.OrderStatus.PROCESSING.getValue()) ||
                    newStatus.equals(Order.OrderStatus.CANCELLED.getValue());
        }
        if (currentStatus.equals(Order.OrderStatus.PROCESSING.getValue())) {
            return newStatus.equals(Order.OrderStatus.SHIPPING.getValue());
        }
        if (currentStatus.equals(Order.OrderStatus.SHIPPING.getValue())) {
            return newStatus.equals(Order.OrderStatus.DELIVERED.getValue());
        }
        if (currentStatus.equals(Order.OrderStatus.DELIVERED.getValue())) {
            return newStatus.equals(Order.OrderStatus.COMPLETED.getValue()) ||
                    newStatus.equals(Order.OrderStatus.RETURNED.getValue());
        }
        return false;
    }

    /**
     * Cập nhật timestamps theo status
     */
    private void updateOrderTimestamps(Order order, String status) {
        Instant now = Instant.now();
        if (status.equals(Order.OrderStatus.CONFIRMED.getValue())) {
            order.setConfirmedAt(now);
        } else if (status.equals(Order.OrderStatus.SHIPPING.getValue())) {
            order.setShippedAt(now);
        } else if (status.equals(Order.OrderStatus.DELIVERED.getValue())) {
            order.setDeliveredAt(now);
        } else if (status.equals(Order.OrderStatus.COMPLETED.getValue())) {
            order.setCompletedAt(now);
        }
    }
}
