package com.hoangduong.hoangduongcomputer.exception;

import lombok.Getter;
import org.springframework.http.HttpStatus;

@Getter
public class ApiError extends RuntimeException {

    private final HttpStatus status;
    private final String message;

    public ApiError(HttpStatus status, String message) {
        super(message);
        this.status = status;
        this.message = message;
    }

    public ApiError(int statusCode, String message) {
        super(message);
        this.status = HttpStatus.valueOf(statusCode);
        this.message = message;
    }
}
