package com.raswanth.cartmicroservice.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.PositiveOrZero;
import lombok.Data;

@Data
public class UpdateCartDto {
    @NotBlank(message = "product Id cannot be null")
    private String productId;
    @NotNull(message = "quantity cannot be null")
    @PositiveOrZero(message = "quantity must be positive")
    private Integer updatedQuantity;
}
