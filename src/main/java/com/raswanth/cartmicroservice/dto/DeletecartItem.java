package com.raswanth.cartmicroservice.dto;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import lombok.Data;

@Data
public class DeletecartItem {
    @NotNull(message = "quantity to remove cannot be null")
    @Positive(message = "quantity to remove must be positive")
    Integer quantityToRemove;

    @NotBlank(message = "product id cannot be null")
    String productId;

    @NotNull(message = "userId cannot be null")
    @Min(1)
    Integer userId;
}
