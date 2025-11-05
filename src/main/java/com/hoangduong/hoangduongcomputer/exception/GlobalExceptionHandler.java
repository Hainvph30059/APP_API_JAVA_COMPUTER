package com.hoangduong.hoangduongcomputer.exception;

import java.nio.file.AccessDeniedException;
import java.util.Map;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;

import com.hoangduong.hoangduongcomputer.dto.ApiReponse;
import org.springframework.web.bind.annotation.RestControllerAdvice;

@ControllerAdvice
@RestControllerAdvice
public class GlobalExceptionHandler {
    private static String MIN_ATTRIBUTE = "min";

    @ExceptionHandler(value = RuntimeException.class)
    ResponseEntity<ApiReponse> handlingRunTimeException(
            RuntimeException exception) { // bắt ngoại lệ với runtimeException
        ApiReponse apiResponse = new ApiReponse();
        apiResponse.setCode(ErrorCode.UNCATEGORIZED_EXCEPTION.getCode());
        apiResponse.setMessage(ErrorCode.UNCATEGORIZED_EXCEPTION.getMessage());

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
    ResponseEntity<ApiReponse> handlingAppException(AppException exception) { // bắt ngoại lệ với class tự định nghĩa
        ErrorCode errorCode = exception.getErrorCode();
        ApiReponse apiReponse = new ApiReponse();
        apiReponse.setCode(errorCode.getCode());
        apiReponse.setMessage(errorCode.getMessage());

        return ResponseEntity.status(errorCode.getHttpStatusCode()).body(apiReponse);
    }

    @ExceptionHandler(value = AccessDeniedException.class)
    ResponseEntity<ApiReponse> handlingAccessDeniedException(AccessDeniedException exception) {
        ErrorCode errorCode = ErrorCode.UNAUTHORIZED;

        return ResponseEntity.status(errorCode.getHttpStatusCode())
                .body(ApiReponse.builder()
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
}
