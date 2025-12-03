package com.hoangduong.hoangduongcomputer.configuration;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.hoangduong.hoangduongcomputer.dto.ApiResponse;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.web.AuthenticationEntryPoint;
import org.springframework.stereotype.Component;
import java.io.IOException;

@Component
public class JwtAuthenticationEntryPoint implements AuthenticationEntryPoint {

    private final ObjectMapper objectMapper;

    public JwtAuthenticationEntryPoint(ObjectMapper objectMapper) {
        this.objectMapper = objectMapper;
    }

    @Override
    public void commence(
            HttpServletRequest request,
            HttpServletResponse response,
            AuthenticationException authException
    ) throws IOException {

        response.setContentType("application/json;charset=UTF-8");

        // Lấy error code và message từ filter
        Integer statusCode = (Integer) request.getAttribute("error_code");
        String errorMessage = (String) request.getAttribute("error_message");

        if (statusCode == null) {
            statusCode = HttpStatus.UNAUTHORIZED.value();
            errorMessage = "Unauthorized";
        }

        response.setStatus(statusCode);

        ApiResponse<Object> apiResponse = ApiResponse.builder()
                .code(statusCode)
                .message(errorMessage)
                .build();

        response.getWriter().write(objectMapper.writeValueAsString(apiResponse));
    }
}