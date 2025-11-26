package com.hoangduong.hoangduongcomputer.security;

import com.hoangduong.hoangduongcomputer.entity.User;
import com.hoangduong.hoangduongcomputer.exception.ApiError;
import com.hoangduong.hoangduongcomputer.reponsitory.UserRepository;
import com.hoangduong.hoangduongcomputer.service.JwtService;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.ExpiredJwtException;
import io.jsonwebtoken.MalformedJwtException;
import io.jsonwebtoken.UnsupportedJwtException;
import io.jsonwebtoken.security.SignatureException;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

@Component
@RequiredArgsConstructor
@Slf4j
public class JwtAuthenticationFilter extends OncePerRequestFilter {

    private final JwtService jwtService;
    private final UserRepository userRepository;

    @Override
    protected void doFilterInternal(
            HttpServletRequest request,
            HttpServletResponse response,
            FilterChain filterChain
    ) throws ServletException, IOException {

        String path = request.getRequestURI();
        String accessToken = getTokenFromCookie(request, "accessToken");

        // Nếu không có token, cho qua để endpoint public hoặc AuthenticationEntryPoint xử lý
        if (accessToken == null || accessToken.isEmpty()) {
            filterChain.doFilter(request, response);
            return;
        }

        try {
            // Verify token
            Claims claims = jwtService.verifyAccessToken(accessToken);

            String userId = claims.get("_id", String.class);
            String userEmail = claims.get("email", String.class);

            // Load user từ DB để lấy role và validate account status
            User user = userRepository.findById(userId)
                    .orElseThrow(() -> new ApiError(HttpStatus.NOT_FOUND, "Account not found!"));

            // Kiểm tra account status
            if (!Boolean.TRUE.equals(user.getIsActive())) {
                setErrorAttributes(request, HttpStatus.FORBIDDEN.value(), "Account is disabled!");
                filterChain.doFilter(request, response);
                return;
            }

            // Set attributes vào request để controller có thể dùng
            request.setAttribute("userId", userId);
            request.setAttribute("userEmail", userEmail);

            // Convert roles từ DB sang Spring Security authorities
            GrantedAuthority authority =
                    new SimpleGrantedAuthority("ROLE_" + user.getRole().toUpperCase());

            List<GrantedAuthority> authorities = List.of(authority);


            // Tạo Authentication object với đầy đủ thông tin
            UsernamePasswordAuthenticationToken authentication =
                    new UsernamePasswordAuthenticationToken(
                            userId, // principal
                            null,   // credentials
                            authorities // authorities
                    );

            authentication.setDetails(new WebAuthenticationDetailsSource().buildDetails(request));
            SecurityContextHolder.getContext().setAuthentication(authentication);

            filterChain.doFilter(request, response);

        } catch (ExpiredJwtException e) {
            // Đặc biệt cho endpoint refresh-token
            if (path.contains("/refresh-token")) {
                filterChain.doFilter(request, response);
                return;
            }

            // Set error code 410 cho token expired
            setErrorAttributes(request, 410, "Token has expired!");
            filterChain.doFilter(request, response);

        } catch (MalformedJwtException | SignatureException | UnsupportedJwtException e) {
            log.error("Invalid JWT token: {}", e.getMessage());
            setErrorAttributes(request, HttpStatus.FORBIDDEN.value(), "Invalid token!");
            filterChain.doFilter(request, response);

        } catch (ApiError e) {
            log.error("API Error: {}", e.getMessage());
            setErrorAttributes(request, e.getStatus().value(), e.getMessage());
            filterChain.doFilter(request, response);

        } catch (Exception e) {
            log.error("Authentication error: {}", e.getMessage());
            setErrorAttributes(request, HttpStatus.UNAUTHORIZED.value(), "Please sign in!");
            filterChain.doFilter(request, response);
        }
    }

    /**
     * Set error attributes để AuthenticationEntryPoint xử lý
     */
    private void setErrorAttributes(HttpServletRequest request, int code, String message) {
        request.setAttribute("error_code", code);
        request.setAttribute("error_message", message);
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
        boolean shouldSkip = path.startsWith("/api/v1/users/register") ||
                path.startsWith("/api/v1/users/login") ||
                path.startsWith("/api/v1/users/logout") ||
                path.startsWith("/api/v1/users/verify") ||
                path.startsWith("/api/v1/users/refresh-token") ||
                path.startsWith("/api/product") ||
                path.startsWith("/api/product-type") ||
                path.startsWith("/api/attribute-template");

        return shouldSkip;
    }
}