package com.hoangduong.hoangduongcomputer.dto.request;


import com.hoangduong.hoangduongcomputer.utils.ValidationPattern;
import jakarta.validation.constraints.Pattern;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class UpdateUserRequest {

    private String displayName;

    @Pattern(regexp = ValidationPattern.PASSWORD_RULE,
            message = "Current Password: " + ValidationPattern.PASSWORD_RULE_MESSAGE)
    private String current_password;

    @Pattern(regexp = ValidationPattern.PASSWORD_RULE,
            message = "New Password: " + ValidationPattern.PASSWORD_RULE_MESSAGE)
    private String new_password;
}
