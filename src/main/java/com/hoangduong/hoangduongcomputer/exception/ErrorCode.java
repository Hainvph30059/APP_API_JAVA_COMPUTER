package com.hoangduong.hoangduongcomputer.exception;

import org.springframework.http.HttpStatus;
import org.springframework.http.HttpStatusCode;

import lombok.Getter;

@Getter
public enum ErrorCode {
    UNCATEGORIZED_EXCEPTION(9999, "Uncategorized Error", HttpStatus.INTERNAL_SERVER_ERROR),
    USER_EXISTSED(1001, "User exsisted", HttpStatus.BAD_REQUEST),
    USERNAME_INVALID(1003, "User name must be at least {min}} characters", HttpStatus.BAD_REQUEST),
    INVALID_PASSWORD(1004, "Password must be at least {min} characters", HttpStatus.BAD_REQUEST),
    INVALID_KEY(1005, "Invalid Message Key", HttpStatus.BAD_REQUEST),
    USER_NOT_EXIT(1006, "User not exists", HttpStatus.NOT_FOUND),
    UNAUTHENTICATED(1007, "Unauthenticated", HttpStatus.UNAUTHORIZED),
    UNAUTHORIZED(1008, "you do not have permission", HttpStatus.FORBIDDEN),
    INVALID_DOB(1009, "You are must be at least {min}", HttpStatus.BAD_REQUEST),
    INVALID_ATTRIBUTE_TEMPLATE(1010, "Invalid attribute template", HttpStatus.BAD_REQUEST),
    PRODUCT_TYPE_EXISTED(1011, "Product type existed, change different name and retry", HttpStatus.BAD_REQUEST),
    IMAGE_NOT_EXIST(1012, "Image or file does not exist", HttpStatus.BAD_REQUEST),
    PRODUCT_NOT_EXISTED(1013, "Product not existed", HttpStatus.BAD_REQUEST),
    FILE_STORAGE_ERROR(1014, "Update image faile", HttpStatus.INTERNAL_SERVER_ERROR),
    PRODUCT_TYPE_NOT_EXISTED(1015, "Product type not existed", HttpStatus.BAD_REQUEST),
    ROLE_EXISTED(1016, "Role existed", HttpStatus.BAD_REQUEST),
    ROLE_NOT_EXISTED(1017, "Role not existed", HttpStatus.BAD_REQUEST),
    ATTRIBUTE_TEMPLATE_NOT_EXISTED(1018, "Attribute template not existed", HttpStatus.BAD_REQUEST),
    SHIPPING_ADDRESS_NOT_FOUND(1019, "Shipping address not found", HttpStatus.NOT_FOUND),
    INVALID_SHIPPING_ADDRESS(1020, "Invalid shipping address", HttpStatus.BAD_REQUEST),
    ORDER_NOT_FOUND(1021, "Order not found", HttpStatus.NOT_FOUND),
    ORDER_ACCESS_DENIED(1022, "Access denied to this order", HttpStatus.FORBIDDEN),
    INSUFFICIENT_STOCK(1023, "Insufficient stock for product", HttpStatus.BAD_REQUEST),
    INVALID_ORDER_STATUS(1024, "Cannot perform action in current status", HttpStatus.BAD_REQUEST),
    INVALID_ORDER_TRANSITION(1025, "Invalid order status transition", HttpStatus.BAD_REQUEST),
    ;
    private int code;
    private String message;
    private HttpStatusCode httpStatusCode;

    ErrorCode(int code, String message, HttpStatusCode httpStatusCode) {
        this.code = code;
        this.message = message;
        this.httpStatusCode = httpStatusCode;
    }
}
