package com.hoangduong.hoangduongcomputer.controller;

import com.hoangduong.hoangduongcomputer.dto.ApiResponse;
import com.hoangduong.hoangduongcomputer.dto.request.LoginRequest;
import com.hoangduong.hoangduongcomputer.dto.request.RegisterRequest;
import com.hoangduong.hoangduongcomputer.dto.request.UpdateUserRequest;
import com.hoangduong.hoangduongcomputer.dto.request.VerifyAccountRequest;
import com.hoangduong.hoangduongcomputer.dto.response.AuthResponse;
import com.hoangduong.hoangduongcomputer.dto.response.LogoutResponse;
import com.hoangduong.hoangduongcomputer.dto.response.RefreshTokenResponse;
import com.hoangduong.hoangduongcomputer.dto.response.UserResponse;

import com.hoangduong.hoangduongcomputer.service.UserService;
import com.hoangduong.hoangduongcomputer.security.CookieUtil;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.client.RestClient;
import org.springframework.web.multipart.MultipartFile;

@RestController
@RequestMapping("/v1/users")
@RequiredArgsConstructor
@Slf4j
public class UserController {

    private final UserService userService;

    @PostMapping("/register")
    public ResponseEntity<UserResponse> register(@Valid @RequestBody RegisterRequest request) {
        log.info("Test");
        UserResponse response = userService.createNew(request);
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    @PutMapping("/verify")
    public ResponseEntity<UserResponse> verifyAccount(@Valid @RequestBody VerifyAccountRequest request) {
        UserResponse response = userService.verifyAccount(request);
        return ResponseEntity.ok(response);
    }

    @PostMapping("/login")
    public ResponseEntity<AuthResponse> login(
            @Valid @RequestBody LoginRequest request,
            HttpServletResponse httpResponse
    ) {
        AuthResponse response = userService.login(request);

        CookieUtil.addCookie(httpResponse, "accessToken", response.getAccessToken(), CookieUtil.FOURTEEN_DAYS);
        CookieUtil.addCookie(httpResponse, "refreshToken", response.getRefreshToken(), CookieUtil.FOURTEEN_DAYS);

        return ResponseEntity.ok(response);
    }

    @DeleteMapping("/logout")
    public ResponseEntity<LogoutResponse> logout(HttpServletResponse httpResponse) {
        CookieUtil.deleteCookie(httpResponse, "accessToken");
        CookieUtil.deleteCookie(httpResponse, "refreshToken");

        return ResponseEntity.ok(LogoutResponse.builder().loggedOut(true).build());
    }

    @GetMapping("/refresh-token")
    public ResponseEntity<RefreshTokenResponse> refreshToken(
            HttpServletRequest httpRequest,
            HttpServletResponse httpResponse
    ) {
        String refreshToken = getTokenFromCookie(httpRequest, "refreshToken");

        RefreshTokenResponse response = userService.refreshToken(refreshToken);

        CookieUtil.addCookie(httpResponse, "accessToken", response.getAccessToken(), CookieUtil.FOURTEEN_DAYS);

        return ResponseEntity.ok(response);
    }

    @GetMapping("/me")
    public ApiResponse<UserResponse> introspecs(
            @AuthenticationPrincipal String userId
    ) {
        UserResponse user = userService.introspecs(userId);
        return ApiResponse.<UserResponse>builder()
                .result(user)
                .build();
    }

    @PutMapping("/update")
    public ResponseEntity<UserResponse> update(
            @Valid @RequestPart(required = false) UpdateUserRequest request,
            @RequestPart(required = false) MultipartFile avatar,
            HttpServletRequest httpRequest
    ) {
        String userId = (String) httpRequest.getAttribute("userId");

        UserResponse response = userService.update(userId, request, avatar);
        return ResponseEntity.ok(response);
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
}
