package com.raswanth.cartmicroservice.controller;

import com.raswanth.cartmicroservice.dto.CartItemBodyDto;
import com.raswanth.cartmicroservice.entity.Cart;
import com.raswanth.cartmicroservice.service.CartService;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;


@RestController
@RequestMapping("/api/cart")
@Validated
public class CartController {
    private final CartService cartService;

    public CartController(CartService cartService) {
        this.cartService = cartService;
    }

    @PostMapping("/add-product")
    public ResponseEntity<String> registerUser(@Valid @RequestBody CartItemBodyDto cartItemBodyDto) {
        cartService.addToCart(cartItemBodyDto);
        return ResponseEntity.ok("Added product to cart");
    }

    @GetMapping("/view-cart/{userId}")
    public Cart viewCart(@Valid @PathVariable Integer userId) {
        return cartService.viewCart(userId);
    }
}
