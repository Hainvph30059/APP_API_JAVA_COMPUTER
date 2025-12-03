package com.hoangduong.hoangduongcomputer.controller;

import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.hoangduong.hoangduongcomputer.dto.ApiResponse;
import com.hoangduong.hoangduongcomputer.dto.request.AddToCartRequest;
import com.hoangduong.hoangduongcomputer.dto.response.CartResponse;
import com.hoangduong.hoangduongcomputer.service.CartService;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/cart")
@RequiredArgsConstructor
public class CartController {

    private final CartService cartService;

    /**
     * Thêm sản phẩm vào giỏ hàng
     * POST /api/cart
     */
    @PostMapping
    public ApiResponse<CartResponse> addToCart(
            @AuthenticationPrincipal String userId,
            @Valid @RequestBody AddToCartRequest request
    ) {
        CartResponse cart = cartService.addToCart(userId, request);

        return ApiResponse.<CartResponse>builder()
                .message("Product added to cart successfully")
                .result(cart)
                .build();
    }

    /**
     * Xóa sản phẩm khỏi giỏ hàng
     * DELETE /api/cart/{productId}
     */
    @DeleteMapping("/{productId}")
    public ApiResponse<CartResponse> removeFromCart(
            @AuthenticationPrincipal String userId,
            @PathVariable String productId
    ) {
        CartResponse cart = cartService.removeFromCart(userId, productId);

        return ApiResponse.<CartResponse>builder()
                .message("Product removed from cart successfully")
                .result(cart)
                .build();
    }

    /**
     * Lấy giỏ hàng hiện tại
     * GET /api/cart
     */
    @GetMapping
    public ApiResponse<CartResponse> getCart(
            @AuthenticationPrincipal String userId
    ) {
        CartResponse cart = cartService.getCart(userId);

        return ApiResponse.<CartResponse>builder()
                .result(cart)
                .build();
    }

    /**
     * Cập nhật số lượng sản phẩm trong giỏ hàng
     * PUT /api/cart/{productId}
     */
    @PutMapping("/{productId}")
    public ApiResponse<CartResponse> updateCartItemQuantity(
            @AuthenticationPrincipal String userId,
            @PathVariable String productId,
            @RequestParam Integer quantity
    ) {
        CartResponse cart = cartService.updateCartItemQuantity(userId, productId, quantity);

        return ApiResponse.<CartResponse>builder()
                .message("Cart item quantity updated successfully")
                .result(cart)
                .build();
    }

    /**
     * Xóa toàn bộ giỏ hàng
     * DELETE /api/cart
     */
    @DeleteMapping
    public ApiResponse<Void> clearCart(
            @AuthenticationPrincipal String userId
    ) {
        cartService.clearCart(userId);

        return ApiResponse.<Void>builder()
                .message("Cart cleared successfully")
                .build();
    }
}
