package com.raswanth.cartmicroservice.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

@Data
public class CartItemAdjustmentDto {
    @NotBlank(message = "product Id cannot be null")
    private String productId;
    @NotNull(message = "quantity cannot be null")
    private Integer quantity;
}
