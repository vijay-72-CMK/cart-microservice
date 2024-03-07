package com.raswanth.cartmicroservice.controller;

import com.raswanth.cartmicroservice.dto.CartItemAdjustmentDto;
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

    @PostMapping("/modify-cart")
    public ResponseEntity<String> modifyCart(@Valid @RequestBody CartItemAdjustmentDto cartItemAdjustmentDto, Principal principal) {
        cartService.updateCart(cartItemAdjustmentDto, principal);
        return ResponseEntity.ok("Modified cart successfully!");
    }

    @GetMapping("/view-cart")
    public Cart viewCart(Principal signedInUser) {
        return cartService.viewCart(signedInUser);
    }

    @DeleteMapping("/delete-cart")
    public ResponseEntity<String> deleteCart(Principal principal) {
        cartService.deleteCart(principal);
        return ResponseEntity.ok("Deleted cart successfully!");
    }

}
