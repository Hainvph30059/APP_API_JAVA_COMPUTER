package com.hoangduong.hoangduongcomputer.security;

import com.hoangduong.hoangduongcomputer.service.JwtService;
import io.jsonwebtoken.Claims;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;

@Component
@RequiredArgsConstructor
public class JwtAuthenticationFilter extends OncePerRequestFilter {

    private final JwtService jwtService;

    @Override
    protected void doFilterInternal(
            HttpServletRequest request,
            HttpServletResponse response,
            FilterChain filterChain
    ) throws ServletException, IOException {

        String accessToken = getTokenFromCookie(request, "accessToken");

        // Nếu không có token, cho phép request đi tiếp (Spring Security sẽ xử lý authorization)
        if (accessToken == null || accessToken.isEmpty()) {
            filterChain.doFilter(request, response);
            return;
        }

        try {
            Claims claims = jwtService.verifyAccessToken(accessToken);

            request.setAttribute("userId", claims.get("_id", String.class));
            request.setAttribute("userEmail", claims.get("email", String.class));

            filterChain.doFilter(request, response);

        } catch (Exception e) {
            if (e.getMessage().contains("expired")) {
                sendErrorResponse(response, HttpStatus.GONE, "Need to refresh token");
                return;
            }
            sendErrorResponse(response, HttpStatus.UNAUTHORIZED, "Unauthorized!");
        }
    }

    private String getTokenFromCookie(HttpServletRequest request, String cookieName) {
        Cookie[] cookies = request.getCookies();
        if (cookies != null) {
            for (Cookie cookie : cookies) {
                if (cookieName.equals(cookie.getName())) {
                    return cookie.getValue();
                }
            }
        }
        return null;
    }

    private void sendErrorResponse(HttpServletResponse response, HttpStatus status, String message) throws IOException {
        response.setStatus(status.value());
        response.setContentType("application/json");
        response.getWriter().write(String.format(
                "{\"status\": %d, \"error\": \"%s\", \"message\": \"%s\"}",
                status.value(),
                status.getReasonPhrase(),
                message
        ));
    }

    @Override
    protected boolean shouldNotFilter(HttpServletRequest request) {
        String path = request.getRequestURI();
        // Bỏ qua bộ lọc nếu đường dẫn bắt đầu bằng các endpoint public
        // Context-path /api đã được loại bỏ trong request.getRequestURI()
        return path.startsWith("/v1/users/register") ||
                path.startsWith("/v1/users/login") ||
                path.startsWith("/v1/users/verify") ||
                path.startsWith("/v1/users/refresh-token") ||
                path.startsWith("/product") ||
                path.startsWith("/product-type") ||
                path.startsWith("/attribute-template");
    }
}
