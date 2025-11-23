package com.hoangduong.hoangduongcomputer.dto.request;

import com.hoangduong.hoangduongcomputer.utils.ValidationPattern;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class LoginRequest {

    @NotBlank(message = ValidationPattern.FIELD_REQUIRED_MESSAGE)
    @Pattern(regexp = ValidationPattern.EMAIL_RULE,
            message = ValidationPattern.EMAIL_RULE_MESSAGE)
    private String email;

    @NotBlank(message = ValidationPattern.FIELD_REQUIRED_MESSAGE)
    @Pattern(regexp = ValidationPattern.PASSWORD_RULE,
            message = ValidationPattern.PASSWORD_RULE_MESSAGE)
    private String password;
}