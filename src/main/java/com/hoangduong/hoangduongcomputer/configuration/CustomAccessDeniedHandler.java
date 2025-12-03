package com.hoangduong.hoangduongcomputer.configuration;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.hoangduong.hoangduongcomputer.dto.ApiResponse;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.http.HttpStatus;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.web.access.AccessDeniedHandler;
import org.springframework.stereotype.Component;

import java.io.IOException;

@Component
public class CustomAccessDeniedHandler implements AccessDeniedHandler {

    private final ObjectMapper objectMapper;

    public CustomAccessDeniedHandler(ObjectMapper objectMapper) {
        this.objectMapper = objectMapper;
    }

    @Override
    public void handle(
            HttpServletRequest request,
            HttpServletResponse response,
            AccessDeniedException accessDeniedException
    ) throws IOException {

        response.setContentType("application/json;charset=UTF-8");
        response.setStatus(HttpStatus.FORBIDDEN.value());

        ApiResponse<Object> apiResponse = ApiResponse.builder()
                .code(HttpStatus.FORBIDDEN.value())
                .message("You don't have permission to access this resource!")
                .build();

        response.getWriter().write(objectMapper.writeValueAsString(apiResponse));
    }
}
