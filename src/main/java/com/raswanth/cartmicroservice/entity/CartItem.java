package com.raswanth.cartmicroservice.entity;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import lombok.Data;

@Data
@Entity
@Table(name = "cart_items")
public class CartItem {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @NotNull
    @Column(nullable = false)
    private Integer productId;

    @NotNull(message = "quantity cannot be null")
    @Positive(message = "quantity must be greater than 0")
    @Column(nullable = false)
    private Integer quantity;

}
