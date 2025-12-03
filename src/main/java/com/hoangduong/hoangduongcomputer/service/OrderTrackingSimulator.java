package com.hoangduong.hoangduongcomputer.service;

import com.hoangduong.hoangduongcomputer.entity.Order;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Async;
import org.springframework.scheduling.annotation.EnableAsync;
import org.springframework.stereotype.Service;

import java.util.concurrent.TimeUnit;

@Service
@RequiredArgsConstructor
@Slf4j
@EnableAsync
public class OrderTrackingSimulator {

    // ✅ Không còn phụ thuộc OrderService - dùng OrderStatusUpdater thay thế
    private final OrderStatusUpdater orderStatusUpdater;

    /**
     * Bắt đầu simulation tracking cho đơn hàng
     */
    @Async
    public void startSimulation(String orderId) {
        log.info("Starting order tracking simulation for order: {}", orderId);

        try {
            // Step 1: CONFIRMED (sau 2 phút)
            TimeUnit.MINUTES.sleep(2);
            orderStatusUpdater.updateOrderStatus(
                    orderId,
                    Order.OrderStatus.CONFIRMED.getValue(),
                    "Đơn hàng đã được xác nhận và đang chuẩn bị",
                    "Kho hàng",
                    "SYSTEM"
            );

            // Step 2: PROCESSING (sau 5 phút)
            TimeUnit.MINUTES.sleep(5);
            orderStatusUpdater.updateOrderStatus(
                    orderId,
                    Order.OrderStatus.PROCESSING.getValue(),
                    "Đang đóng gói sản phẩm",
                    "Kho hàng",
                    "SYSTEM"
            );

            // Step 3: SHIPPING (sau 10 phút)
            TimeUnit.MINUTES.sleep(10);
            orderStatusUpdater.updateOrderStatus(
                    orderId,
                    Order.OrderStatus.SHIPPING.getValue(),
                    "Shipper đã lấy hàng và đang trên đường giao",
                    "Trung tâm phân phối",
                    "SYSTEM"
            );

            // Step 4: Cập nhật vị trí giữa chừng (sau 15 phút)
            TimeUnit.MINUTES.sleep(15);
            orderStatusUpdater.updateOrderStatus(
                    orderId,
                    Order.OrderStatus.SHIPPING.getValue(),
                    "Hàng đang trên đường, dự kiến giao trong 30 phút",
                    "Đang di chuyển",
                    "SHIPPER"
            );

            // Step 5: DELIVERED (sau 20 phút)
            TimeUnit.MINUTES.sleep(20);
            orderStatusUpdater.updateOrderStatus(
                    orderId,
                    Order.OrderStatus.DELIVERED.getValue(),
                    "Đơn hàng đã được giao thành công",
                    "Địa chỉ giao hàng",
                    "SHIPPER"
            );

            // Step 6: COMPLETED (sau 1 ngày - giả lập)
            TimeUnit.MINUTES.sleep(1); // Trong thực tế là 1 ngày
            orderStatusUpdater.updateOrderStatus(
                    orderId,
                    Order.OrderStatus.COMPLETED.getValue(),
                    "Đơn hàng hoàn thành",
                    "Hệ thống",
                    "SYSTEM"
            );

            log.info("Order tracking simulation completed for order: {}", orderId);

        } catch (InterruptedException e) {
            log.error("Simulation interrupted for order: {}", orderId, e);
            Thread.currentThread().interrupt();
        } catch (Exception e) {
            log.error("Error during simulation for order: {}", orderId, e);
        }
    }

    /**
     * Simulation nhanh cho demo (tất cả trong vài giây)
     */
    @Async
    public void startQuickSimulation(String orderId) {
        log.info("Starting QUICK order tracking simulation for order: {}", orderId);

        try {
            TimeUnit.SECONDS.sleep(2);
            orderStatusUpdater.updateOrderStatus(orderId, Order.OrderStatus.CONFIRMED.getValue(),
                    "Đơn hàng đã được xác nhận", "Kho hàng", "SYSTEM");

            TimeUnit.SECONDS.sleep(3);
            orderStatusUpdater.updateOrderStatus(orderId, Order.OrderStatus.PROCESSING.getValue(),
                    "Đang đóng gói sản phẩm", "Kho hàng", "SYSTEM");

            TimeUnit.SECONDS.sleep(3);
            orderStatusUpdater.updateOrderStatus(orderId, Order.OrderStatus.SHIPPING.getValue(),
                    "Shipper đã lấy hàng", "Trung tâm phân phối", "SYSTEM");

            TimeUnit.SECONDS.sleep(5);
            orderStatusUpdater.updateOrderStatus(orderId, Order.OrderStatus.SHIPPING.getValue(),
                    "Hàng đang trên đường giao", "Đang di chuyển", "SHIPPER");

            TimeUnit.SECONDS.sleep(5);
            orderStatusUpdater.updateOrderStatus(orderId, Order.OrderStatus.DELIVERED.getValue(),
                    "Đã giao hàng thành công", "Địa chỉ giao hàng", "SHIPPER");

            TimeUnit.SECONDS.sleep(2);
            orderStatusUpdater.updateOrderStatus(orderId, Order.OrderStatus.COMPLETED.getValue(),
                    "Đơn hàng hoàn thành", "Hệ thống", "SYSTEM");

            log.info("QUICK simulation completed for order: {}", orderId);

        } catch (Exception e) {
            log.error("Error during quick simulation for order: {}", orderId, e);
        }
    }
}
