package com.hoangduong.hoangduongcomputer.service;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

import com.hoangduong.hoangduongcomputer.reponsitory.CartRepository;
import com.hoangduong.hoangduongcomputer.reponsitory.ProductRepository;
import lombok.experimental.FieldDefaults;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.hoangduong.hoangduongcomputer.dto.request.AddToCartRequest;
import com.hoangduong.hoangduongcomputer.dto.response.CartItemResponse;
import com.hoangduong.hoangduongcomputer.dto.response.CartResponse;
import com.hoangduong.hoangduongcomputer.entity.Cart;
import com.hoangduong.hoangduongcomputer.entity.CartItem;
import com.hoangduong.hoangduongcomputer.entity.Product;
import com.hoangduong.hoangduongcomputer.exception.ApiError;


import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Service
@RequiredArgsConstructor
@FieldDefaults(level = lombok.AccessLevel.PRIVATE, makeFinal = true)
@Slf4j
public class CartService {

    private final CartRepository cartRepository;
    private final ProductRepository productRepository;

    /**
     * Thêm sản phẩm vào giỏ hàng
     */
    @Transactional
    public CartResponse addToCart(String userId, AddToCartRequest request) {
        // Kiểm tra sản phẩm có tồn tại không
        Product product = productRepository.findById(request.getProductId())
                .orElseThrow(() -> new ApiError(HttpStatus.NOT_FOUND, "Product not found!"));

        // Kiểm tra số lượng tồn kho
        if (product.getStock() < request.getQuantity()) {
            throw new ApiError(HttpStatus.BAD_REQUEST, "Not enough stock! Available: " + product.getStock());
        }

        // Lấy hoặc tạo mới giỏ hàng
        Cart cart = cartRepository.findByUserId(userId)
                .orElseGet(() -> Cart.builder()
                        .userId(userId)
                        .dateCreated(LocalDateTime.now())
                        .items(new ArrayList<>())
                        .build());

        // Kiểm tra sản phẩm đã có trong giỏ hàng chưa
        CartItem existingItem = cart.getItems().stream()
                .filter(item -> item.getProductId().equals(request.getProductId()))
                .findFirst()
                .orElse(null);

        if (existingItem != null) {
            // Cập nhật số lượng nếu sản phẩm đã có
            int newQuantity = existingItem.getQuantity() + request.getQuantity();

            // Kiểm tra lại tồn kho
            if (product.getStock() < newQuantity) {
                throw new ApiError(HttpStatus.BAD_REQUEST,
                        "Not enough stock! Available: " + product.getStock() +
                                ", Current in cart: " + existingItem.getQuantity());
            }

            existingItem.setQuantity(newQuantity);
        } else {
            // Thêm sản phẩm mới vào giỏ hàng
            CartItem newItem = new CartItem();
            newItem.setProductId(request.getProductId());
            newItem.setQuantity(request.getQuantity());
            cart.getItems().add(newItem);
        }

        // Lưu giỏ hàng
        Cart savedCart = cartRepository.save(cart);

        log.info("Added product {} to cart for user {}", request.getProductId(), userId);

        return mapToCartResponse(savedCart);
    }

    /**
     * Xóa sản phẩm khỏi giỏ hàng
     */
    @Transactional
    public CartResponse removeFromCart(String userId, String productId) {
        // Lấy giỏ hàng
        Cart cart = cartRepository.findByUserId(userId)
                .orElseThrow(() -> new ApiError(HttpStatus.NOT_FOUND, "Cart not found!"));

        // Tìm và xóa sản phẩm
        boolean removed = cart.getItems().removeIf(item -> item.getProductId().equals(productId));

        if (!removed) {
            throw new ApiError(HttpStatus.NOT_FOUND, "Product not found in cart!");
        }

        // Lưu giỏ hàng
        Cart savedCart = cartRepository.save(cart);

        log.info("Removed product {} from cart for user {}", productId, userId);

        return mapToCartResponse(savedCart);
    }

    /**
     * Lấy giỏ hàng của user
     */
    public CartResponse getCart(String userId) {
        Cart cart = cartRepository.findByUserId(userId)
                .orElseGet(() -> Cart.builder()
                        .userId(userId)
                        .dateCreated(LocalDateTime.now())
                        .items(new ArrayList<>())
                        .build());

        return mapToCartResponse(cart);
    }

    /**
     * Xóa toàn bộ giỏ hàng
     */
    @Transactional
    public void clearCart(String userId) {
        Cart cart = cartRepository.findByUserId(userId)
                .orElseThrow(() -> new ApiError(HttpStatus.NOT_FOUND, "Cart not found!"));

        cart.getItems().clear();
        cartRepository.save(cart);

        log.info("Cleared cart for user {}", userId);
    }

    /**
     * Cập nhật số lượng sản phẩm trong giỏ hàng
     */
    @Transactional
    public CartResponse updateCartItemQuantity(String userId, String productId, Integer quantity) {
        if (quantity < 1) {
            throw new ApiError(HttpStatus.BAD_REQUEST, "Quantity must be at least 1");
        }

        // Kiểm tra sản phẩm và tồn kho
        Product product = productRepository.findById(productId)
                .orElseThrow(() -> new ApiError(HttpStatus.NOT_FOUND, "Product not found!"));

        if (product.getStock() < quantity) {
            throw new ApiError(HttpStatus.BAD_REQUEST, "Not enough stock! Available: " + product.getStock());
        }

        // Lấy giỏ hàng
        Cart cart = cartRepository.findByUserId(userId)
                .orElseThrow(() -> new ApiError(HttpStatus.NOT_FOUND, "Cart not found!"));

        // Tìm và cập nhật số lượng
        CartItem cartItem = cart.getItems().stream()
                .filter(item -> item.getProductId().equals(productId))
                .findFirst()
                .orElseThrow(() -> new ApiError(HttpStatus.NOT_FOUND, "Product not found in cart!"));

        cartItem.setQuantity(quantity);

        // Lưu giỏ hàng
        Cart savedCart = cartRepository.save(cart);

        log.info("Updated quantity of product {} to {} for user {}", productId, quantity, userId);

        return mapToCartResponse(savedCart);
    }

    /**
     * Map Cart entity sang CartResponse DTO
     */
    private CartResponse mapToCartResponse(Cart cart) {
        List<CartItemResponse> itemResponses = cart.getItems().stream()
                .map(this::mapToCartItemResponse)
                .collect(Collectors.toList());

        // Tính tổng
        int totalItems = itemResponses.stream()
                .mapToInt(CartItemResponse::getQuantity)
                .sum();

        double totalPrice = itemResponses.stream()
                .mapToDouble(CartItemResponse::getTotalPrice)
                .sum();

        return CartResponse.builder()
                .cartId(cart.getId())
                .userId(cart.getUserId())
                .dateCreated(cart.getDateCreated())
                .items(itemResponses)
                .totalItems(totalItems)
                .totalPrice(totalPrice)
                .build();
    }

    /**
     * Map CartItem sang CartItemResponse với thông tin product
     */
    private CartItemResponse mapToCartItemResponse(CartItem cartItem) {
        Product product = productRepository.findById(cartItem.getProductId())
                .orElseThrow(() -> new ApiError(HttpStatus.NOT_FOUND,
                        "Product not found: " + cartItem.getProductId()));

        // Lấy ảnh đầu tiên
        String imageUrl = (product.getUrls() != null && !product.getUrls().isEmpty())
                ? product.getUrls().get(0)
                : null;

        double totalPrice = product.getPrice() * cartItem.getQuantity();

        return CartItemResponse.builder()
                .productId(product.getId())
                .name(product.getName())
                .price(product.getPrice())
                .imageUrl(imageUrl)
                .quantity(cartItem.getQuantity())
                .totalPrice(totalPrice)
                .build();
    }
}
