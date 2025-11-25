package com.hoangduong.hoangduongcomputer.service;

import io.jsonwebtoken.*;
import io.jsonwebtoken.security.Keys;
import io.jsonwebtoken.security.SignatureException;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import javax.crypto.SecretKey;
import java.nio.charset.StandardCharsets;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

@Service
public class JwtService {

    @Value("${jwt.access.secret}")
    private String accessTokenSecret;

    @Value("${jwt.access.life}")
    private String accessTokenLife;

    @Value("${jwt.refresh.secret}")
    private String refreshTokenSecret;

    @Value("${jwt.refresh.life}")
    private String refreshTokenLife;

    public String generateToken(String userId, String email, String secretSignature, String tokenLife) {
        Map<String, Object> claims = new HashMap<>();
        claims.put("_id", userId);
        claims.put("email", email);

        SecretKey key = Keys.hmacShaKeyFor(secretSignature.getBytes(StandardCharsets.UTF_8));
        long expirationMillis = parseTokenLife(tokenLife);

        return Jwts.builder()
                .claims(claims)
                .issuedAt(new Date(System.currentTimeMillis()))
                .expiration(new Date(System.currentTimeMillis() + expirationMillis))
                .signWith(key)
                .compact();
    }

    public String generateAccessToken(String userId, String email) {
        return generateToken(userId, email, accessTokenSecret, accessTokenLife);
    }

    public String generateRefreshToken(String userId, String email) {
        return generateToken(userId, email, refreshTokenSecret, refreshTokenLife);
    }

//    public Claims verifyToken(String token, String secretSignature) throws Exception {
//        try {
//            SecretKey key = Keys.hmacShaKeyFor(secretSignature.getBytes(StandardCharsets.UTF_8));
//            return Jwts.parser()
//                    .verifyWith(key)
//                    .build()
//                    .parseSignedClaims(token)
//                    .getPayload();
//        } catch (Exception e) {
//            throw new Exception(e.getMessage());
//        }
//    }
    public Claims verifyToken(String token, String secretSignature) throws Exception {
        try {
            SecretKey key = Keys.hmacShaKeyFor(secretSignature.getBytes(StandardCharsets.UTF_8));
            return Jwts.parser()
                    .verifyWith(key)
                    .build()
                    .parseSignedClaims(token)
                    .getPayload();
        } catch (ExpiredJwtException e) {
            throw e; // Throw lại exception gốc
        } catch (MalformedJwtException | SignatureException | UnsupportedJwtException e) {
            throw e; // Throw lại exception gốc
        } catch (Exception e) {
            throw e;
        }
    }

    public Claims verifyAccessToken(String token) throws Exception {
        return verifyToken(token, accessTokenSecret);
    }

    public Claims verifyRefreshToken(String token) throws Exception {
        return verifyToken(token, refreshTokenSecret);
    }

    private long parseTokenLife(String tokenLife) {
        if (tokenLife.endsWith("ms")) {
            return Long.parseLong(tokenLife.substring(0, tokenLife.length() - 2));
        } else if (tokenLife.endsWith("s")) {
            return Long.parseLong(tokenLife.substring(0, tokenLife.length() - 1)) * 1000;
        } else if (tokenLife.endsWith("m")) {
            return Long.parseLong(tokenLife.substring(0, tokenLife.length() - 1)) * 60 * 1000;
        } else if (tokenLife.endsWith("h")) {
            return Long.parseLong(tokenLife.substring(0, tokenLife.length() - 1)) * 60 * 60 * 1000;
        } else if (tokenLife.endsWith("d")) {
            return Long.parseLong(tokenLife.substring(0, tokenLife.length() - 1)) * 24 * 60 * 60 * 1000;
        }
        return Long.parseLong(tokenLife);
    }
}
