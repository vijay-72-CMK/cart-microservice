package com.raswanth.cartmicroservice.dto;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import lombok.Data;

@Data
public class CartItemBodyDto {
    @NotBlank(message = "product Id cannot be null")
    private String productId;
    @NotNull(message = "quantity cannot be null")
    @Positive(message = "quantity must be positive")
    private Integer quantity;

    @NotNull(message = "userId cannot be null")
    @Min(1)
    Integer userId;
}
