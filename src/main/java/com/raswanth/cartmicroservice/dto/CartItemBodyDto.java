package com.raswanth.cartmicroservice.dto;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import lombok.Data;

@Data
public class CartItemBodyDto {
    @NotNull(message = "product Id cannot be null")
    private Integer productId;
    @NotNull(message = "quantity cannot be null")
    @Positive(message = "quantity must be positive")
    private Integer quantity;

    @NotNull(message = "userId cannot be null")
    Integer userId;
}
