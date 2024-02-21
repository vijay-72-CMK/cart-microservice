package com.raswanth.cartmicroservice.controller;

import com.raswanth.cartmicroservice.dto.CartItemBodyDto;
import com.raswanth.cartmicroservice.service.CartService;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;


@RestController
@RequestMapping("/api/cart")
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
}
