package com.raswanth.cartmicroservice.controller;

import com.raswanth.cartmicroservice.dto.UpdateCartDto;
import com.raswanth.cartmicroservice.entity.Cart;
import com.raswanth.cartmicroservice.service.CartService;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.security.Principal;


@RestController
@RequestMapping("/api/cart")
@Validated
public class CartController {
    private final CartService cartService;

    public CartController(CartService cartService) {
        this.cartService = cartService;
    }

    @PostMapping("/update-cart")
    public ResponseEntity<String> updateCart(@Valid @RequestBody UpdateCartDto updateCartDto, Principal principal) {
        cartService.updateCart(updateCartDto, principal);
        return ResponseEntity.ok("Updated cart successfully!");
    }

    @GetMapping("/view-cart")
    public Cart viewCart(Principal signedInUser) {
        return cartService.viewCart(signedInUser);
    }

}
