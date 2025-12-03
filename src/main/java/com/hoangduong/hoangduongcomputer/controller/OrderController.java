package com.hoangduong.hoangduongcomputer.controller;

import com.hoangduong.hoangduongcomputer.dto.ApiResponse;
import com.hoangduong.hoangduongcomputer.dto.request.CancelOrderRequest;
import com.hoangduong.hoangduongcomputer.dto.request.CreateOrderRequest;
import com.hoangduong.hoangduongcomputer.dto.request.UpdateOrderStatusRequest;
import com.hoangduong.hoangduongcomputer.dto.response.OrderResponse;
import com.hoangduong.hoangduongcomputer.service.OrderService;
import com.hoangduong.hoangduongcomputer.service.OrderTrackingSimulator;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;
import java.util.List;


@RestController
@RequestMapping("/orders")
@RequiredArgsConstructor
@Slf4j
public class OrderController {

    private final OrderService orderService;
    private final OrderTrackingSimulator orderTrackingSimulator;


    @PostMapping
    ApiResponse<OrderResponse> createOrder(
            @AuthenticationPrincipal String userId,
            @RequestBody CreateOrderRequest request) {

        return ApiResponse.<OrderResponse>builder()
                .result(orderService.createOrder(userId, request))
                .build();
    }

    @GetMapping
    ApiResponse<List<OrderResponse>> getUserOrders(
            @AuthenticationPrincipal String userId) {

        return ApiResponse.<List<OrderResponse>>builder()
                .result(orderService.getUserOrders(userId))
                .build();
    }

    @GetMapping("/{orderId}")
    ApiResponse<OrderResponse> getOrderById(
            @AuthenticationPrincipal String userId,
            @PathVariable String orderId) {

        return ApiResponse.<OrderResponse>builder()
                .result(orderService.getOrderById(userId, orderId))
                .build();
    }

    @PostMapping("/{orderId}/cancel")
    ApiResponse<OrderResponse> cancelOrder(
            @AuthenticationPrincipal String userId,
            @PathVariable String orderId,
            @RequestBody(required = false) CancelOrderRequest request) {

        String reason = (request != null && request.getReason() != null)
                ? request.getReason()
                : "User cancelled";

        return ApiResponse.<OrderResponse>builder()
                .result(orderService.cancelOrder(userId, orderId, reason))
                .build();
    }

    /**
     * Cập nhật trạng thái đơn hàng (ADMIN ONLY)
     */
    @PostMapping("/{orderId}/status")
    ApiResponse<OrderResponse> updateOrderStatus(
            @AuthenticationPrincipal String userId,
            @PathVariable String orderId,
            @RequestBody UpdateOrderStatusRequest request) {

        return ApiResponse.<OrderResponse>builder()
                .result(orderService.updateOrderStatus(
                        orderId,
                        request.getStatus(),
                        request.getDescription(),
                        request.getLocation(),
                        userId
                ))
                .build();
    }

    /**
     * Trigger simulation tracking thủ công (dành cho testing/demo)
     */
    @PostMapping("/{orderId}/simulate")
    ApiResponse<String> simulateTracking(
            @PathVariable String orderId,
            @RequestParam(value = "quick", required = false, defaultValue = "false") Boolean quick) {

        String message;
        if (Boolean.TRUE.equals(quick)) {
            orderTrackingSimulator.startQuickSimulation(orderId);
            message = "Quick simulation started (completed in ~20 seconds)";
        } else {
            orderTrackingSimulator.startSimulation(orderId);
            message = "Simulation started (completed in ~52 minutes)";
        }

        return ApiResponse.<String>builder()
                .result(message)
                .build();
    }
}
