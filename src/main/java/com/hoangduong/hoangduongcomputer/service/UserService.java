package com.hoangduong.hoangduongcomputer.service;

import com.hoangduong.hoangduongcomputer.dto.request.LoginRequest;
import com.hoangduong.hoangduongcomputer.dto.request.RegisterRequest;
import com.hoangduong.hoangduongcomputer.dto.request.UpdateUserRequest;
import com.hoangduong.hoangduongcomputer.dto.request.VerifyAccountRequest;
import com.hoangduong.hoangduongcomputer.dto.response.AuthResponse;
import com.hoangduong.hoangduongcomputer.dto.response.RefreshTokenResponse;
import com.hoangduong.hoangduongcomputer.dto.response.UserResponse;

import com.hoangduong.hoangduongcomputer.entity.Cart;
import com.hoangduong.hoangduongcomputer.entity.User;
import com.hoangduong.hoangduongcomputer.exception.ApiError;
import com.hoangduong.hoangduongcomputer.reponsitory.CartRepository;
import com.hoangduong.hoangduongcomputer.reponsitory.UserRepository;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.ExpiredJwtException;
import io.jsonwebtoken.MalformedJwtException;
import io.jsonwebtoken.UnsupportedJwtException;
import io.jsonwebtoken.security.SignatureException;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.time.Instant;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class UserService {

    private final UserRepository userRepository;
    private final CartRepository cartRepository;
    private final JwtService jwtService;
    private final EmailService emailService;
    private final CloudinaryService cloudinaryService;
    private final BCryptPasswordEncoder passwordEncoder = new BCryptPasswordEncoder(8);

    @Value("${website.domain}")
    private String websiteDomain;

    public UserResponse createNew(RegisterRequest request) {
        try {
            if (userRepository.existsByEmail(request.getEmail())) {
                throw new ApiError(HttpStatus.CONFLICT, "Email already exists!");
            }

            String nameFromEmail = request.getEmail().split("@")[0];

            User newUser = User.builder()
                    .email(request.getEmail())
                    .password(passwordEncoder.encode(request.getPassword()))
                    .username(nameFromEmail)
                    .displayName(nameFromEmail)
                    .verifyToken(UUID.randomUUID().toString())
                    .build();

            User savedUser = userRepository.save(newUser);
            Cart newCart = Cart.builder()
                    .userId(savedUser.getId())
                    .build();
            cartRepository.save(newCart);

            String verificationLink = String.format(
                    "%s/HoangDuongComputer/htmldemo.net/rozer/rozer/verification.html?email=%s&token=%s",
                    websiteDomain,
                    savedUser.getEmail(),
                    savedUser.getVerifyToken()
            );
            emailService.sendVerificationEmail(savedUser.getEmail(), verificationLink);

            return mapToUserResponse(savedUser);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    public UserResponse verifyAccount(VerifyAccountRequest request) {
        User existUser = userRepository.findByEmail(request.getEmail())
                .orElseThrow(() -> new ApiError(HttpStatus.NOT_FOUND, "Account not found!"));

        if (existUser.getIsActive()) {
            throw new ApiError(HttpStatus.NOT_ACCEPTABLE, "Your account already active!");
        }

        if (!request.getToken().equals(existUser.getVerifyToken())) {
            throw new ApiError(HttpStatus.NOT_ACCEPTABLE, "Token is invalid!");
        }

        existUser.setIsActive(true);
        existUser.setVerifyToken(null);
        existUser.setUpdatedAt(Instant.now());

        User updatedUser = userRepository.save(existUser);
        return mapToUserResponse(updatedUser);
    }

    public AuthResponse login(LoginRequest request) {
        User existUser = userRepository.findByEmail(request.getEmail())
                .orElseThrow(() -> new ApiError(HttpStatus.NOT_FOUND, "Account not found!"));

        if (!existUser.getIsActive()) {
            throw new ApiError(HttpStatus.NOT_ACCEPTABLE, "Your account not active!");
        }

        if (!passwordEncoder.matches(request.getPassword(), existUser.getPassword())) {
            throw new ApiError(HttpStatus.NOT_ACCEPTABLE, "Your email or password is incorrect!");
        }

        String accessToken = jwtService.generateAccessToken(existUser.getId(), existUser.getEmail());
        String refreshToken = jwtService.generateRefreshToken(existUser.getId(), existUser.getEmail());

        return AuthResponse.builder()
                .accessToken(accessToken)
                .refreshToken(refreshToken)
                .id(existUser.getId())
                .email(existUser.getEmail())
                .username(existUser.getUsername())
                .displayName(existUser.getDisplayName())
                .avatar(existUser.getAvatar())
                .role(existUser.getRole())
                .isActive(existUser.getIsActive())
                .createdAt(existUser.getCreatedAt())
                .build();
    }

    public RefreshTokenResponse refreshToken(String clientRefreshToken) {
        if (clientRefreshToken == null || clientRefreshToken.isEmpty()) {
            throw new ApiError(HttpStatus.NOT_FOUND, "Token not found!");
        }
        try {
            Claims claims = jwtService.verifyRefreshToken(clientRefreshToken);
            String userId = claims.get("_id", String.class);
            String email = claims.get("email", String.class);
            String newAccessToken = jwtService.generateAccessToken(userId, email);

            return RefreshTokenResponse.builder()
                    .accessToken(newAccessToken)
                    .build();
        } catch (Exception e) {
            throw new ApiError(HttpStatus.FORBIDDEN, "Please sign in!");
        }
    }

    public UserResponse update(String userId, UpdateUserRequest request, MultipartFile userAvatar) {
        User existUser = userRepository.findById(userId)
                .orElseThrow(() -> new ApiError(HttpStatus.NOT_FOUND, "Account not found!"));

        if (!existUser.getIsActive()) {
            throw new ApiError(HttpStatus.NOT_ACCEPTABLE, "Your account is not active!");
        }

        try {
            if (request.getCurrent_password() != null && request.getNew_password() != null) {
                if (!passwordEncoder.matches(request.getCurrent_password(), existUser.getPassword())) {
                    throw new ApiError(HttpStatus.NOT_ACCEPTABLE, "Your password is incorrect!");
                }
                existUser.setPassword(passwordEncoder.encode(request.getNew_password()));
            }
            else if (userAvatar != null && !userAvatar.isEmpty()) {
                String avatarUrl = cloudinaryService.uploadAvatar(userAvatar);
                existUser.setAvatar(avatarUrl);
            }
            else {
                if (request.getDisplayName() != null) {
                    existUser.setDisplayName(request.getDisplayName());
                }
            }

            existUser.setUpdatedAt(Instant.now());
            User updatedUser = userRepository.save(existUser);
            return mapToUserResponse(updatedUser);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    public UserResponse introspecs(String userId) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new ApiError(HttpStatus.NOT_FOUND, "Account not found!"));

        // Không cần check isActive vì filter đã check rồi
        return mapToUserResponse(user);
    }


    private UserResponse mapToUserResponse(User user) {
        return UserResponse.builder()
                .id(user.getId())
                .email(user.getEmail())
                .username(user.getUsername())
                .displayName(user.getDisplayName())
                .avatar(user.getAvatar())
                .role(user.getRole())
                .isActive(user.getIsActive())
                .createdAt(user.getCreatedAt())
                .updatedAt(user.getUpdatedAt())
                .build();
    }
}