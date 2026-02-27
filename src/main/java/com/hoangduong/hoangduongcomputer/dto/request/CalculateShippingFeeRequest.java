package com.hoangduong.hoangduongcomputer.dto.request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class CalculateShippingFeeRequest {
    
    @NotBlank(message = "Shipping address ID is required")
    private String shippingAddressId;
    
    @NotNull(message = "Subtotal is required")
    @Positive(message = "Subtotal must be positive")
    private Double subtotal;
}
