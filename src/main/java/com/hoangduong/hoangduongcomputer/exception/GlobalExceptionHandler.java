package com.hoangduong.hoangduongcomputer.exception;

import org.springframework.security.access.AccessDeniedException;
import java.time.Instant;
import java.util.HashMap;
import java.util.Map;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;

import com.hoangduong.hoangduongcomputer.dto.ApiResponse;
import org.springframework.web.bind.annotation.RestControllerAdvice;

@ControllerAdvice
@RestControllerAdvice
public class GlobalExceptionHandler {
    private static String MIN_ATTRIBUTE = "min";

    @ExceptionHandler(value = RuntimeException.class)
    ResponseEntity<ApiResponse> handlingRunTimeException(
            RuntimeException exception) { // bắt ngoại lệ với runtimeException
        exception.printStackTrace(); // Log chi tiết exception
        ApiResponse apiResponse = new ApiResponse();
        apiResponse.setCode(ErrorCode.UNCATEGORIZED_EXCEPTION.getCode());
        apiResponse.setMessage(exception.getMessage() != null ? exception.getMessage() : ErrorCode.UNCATEGORIZED_EXCEPTION.getMessage());

        return ResponseEntity.badRequest().body(apiResponse);
    }

    //    @ExceptionHandler(value = MethodArgumentNotValidException.class) // bắt ngoại lệ validation
    //    ResponseEntity<ApiReponse> handlingValidation(MethodArgumentNotValidException exception) {
    //        String enumKey = exception.getFieldError().getDefaultMessage();
    //        ErrorCode errorCode = ErrorCode.INVALID_KEY;
    //
    //        Map<String, Object> attributes = null;
    //        try {
    //            errorCode = ErrorCode.valueOf(enumKey);
    //            var constraintViolations =
    //                    exception.getBindingResult().getAllErrors().getFirst().unwrap(ConstraintViolation.class);
    //
    //            attributes = constraintViolations.getConstraintDescriptor().getAttributes();
    //        } catch (IllegalArgumentException e) {
    //
    //        }
    //
    //        ApiReponse apiReponse = new ApiReponse();
    //
    //        apiReponse.setCode(errorCode.getCode());
    //        apiReponse.setMessage(
    //                Objects.nonNull(attributes)
    //                        ? mapAttribute(errorCode.getMessage(), attributes)
    //                        : errorCode.getMessage());
    //
    //        return ResponseEntity.badRequest().body(apiReponse);
    //    }

    @ExceptionHandler(value = AppException.class)
    ResponseEntity<ApiResponse> handlingAppException(AppException exception) { // bắt ngoại lệ với class tự định nghĩa
        ErrorCode errorCode = exception.getErrorCode();
        ApiResponse apiResponse = new ApiResponse();
        apiResponse.setCode(errorCode.getCode());
        apiResponse.setMessage(errorCode.getMessage());

        return ResponseEntity.status(errorCode.getHttpStatusCode()).body(apiResponse);
    }

    @ExceptionHandler(value = AccessDeniedException.class)
    ResponseEntity<ApiResponse> handlingAccessDeniedException(AccessDeniedException exception) {
        ErrorCode errorCode = ErrorCode.UNAUTHORIZED;

        return ResponseEntity.status(errorCode.getHttpStatusCode())
                .body(ApiResponse.builder()
                        .code(errorCode.getCode())
                        .message(errorCode.getMessage())
                        .build());
    }
    // method map giá trị min vào message, ví dụ dùng annotaion có field min = 16, thì message tự động trả ra là yêu cầu
    // chứa min 16 (tuổi phải lớn hơn 16...)

    private String mapAttribute(String mesage, Map<String, Object> attributes) {
        String minValue = attributes.get(MIN_ATTRIBUTE).toString();
        return mesage.replace("{" + MIN_ATTRIBUTE + "}", minValue);
    }

    @ExceptionHandler(ApiError.class)
    public ResponseEntity<ErrorResponse> handleApiError(ApiError ex) {
        ErrorResponse error = ErrorResponse.builder()
                .timestamp(Instant.now())
                .status(ex.getStatus().value())
                .error(ex.getStatus().getReasonPhrase())
                .message(ex.getMessage())
                .build();
        return ResponseEntity.status(ex.getStatus()).body(error);
    }

    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<ErrorResponse> handleValidationExceptions(MethodArgumentNotValidException ex) {
        Map<String, String> errors = new HashMap<>();
        ex.getBindingResult().getAllErrors().forEach(error -> {
            String fieldName = ((FieldError) error).getField();
            String errorMessage = error.getDefaultMessage();
            errors.put(fieldName, errorMessage);
        });

        ErrorResponse error = ErrorResponse.builder()
                .timestamp(Instant.now())
                .status(HttpStatus.UNPROCESSABLE_ENTITY.value())
                .error("Validation Failed")
                .message(errors.toString())
                .build();
        return ResponseEntity.status(HttpStatus.UNPROCESSABLE_ENTITY).body(error);
    }

    @ExceptionHandler(Exception.class)
    public ResponseEntity<ErrorResponse> handleGenericException(Exception ex) {
        ErrorResponse error = ErrorResponse.builder()
                .timestamp(Instant.now())
                .status(HttpStatus.INTERNAL_SERVER_ERROR.value())
                .error("Internal Server Error")
                .message(ex.getMessage())
                .build();
        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(error);
    }

    @lombok.Builder
    @lombok.Data
    static class ErrorResponse {
        private Instant timestamp;
        private int status;
        private String error;
        private String message;
    }
}
