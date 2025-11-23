package com.hoangduong.hoangduongcomputer.dto.response;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.Instant;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@JsonInclude(JsonInclude.Include.NON_NULL)
public class AuthResponse {

    private String accessToken;
    private String refreshToken;

    private String id;
    private String email;
    private String username;
    private String displayName;
    private String avatar;
    private String role;
    private Boolean isActive;
    private Instant createdAt;
}
