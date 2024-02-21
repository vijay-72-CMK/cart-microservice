package com.raswanth.cartmicroservice.service;

import com.raswanth.cartmicroservice.dto.CartItemBodyDto;
import com.raswanth.cartmicroservice.entity.Cart;
import com.raswanth.cartmicroservice.entity.CartItem;
import com.raswanth.cartmicroservice.exception.GeneralInternalException;
import com.raswanth.cartmicroservice.repositories.CartItemRepository;
import com.raswanth.cartmicroservice.repositories.CartRepository;
import jakarta.transaction.Transactional;
import org.springframework.dao.DataAccessException;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.Optional;


@Service
@Transactional
public class CartService {

    private final CartRepository cartRepository;

    private final CartItemRepository cartItemRepository;


    public CartService(CartRepository cartRepository, CartItemRepository cartItemRepository) {
        this.cartRepository = cartRepository;
        this.cartItemRepository = cartItemRepository;
    }

    public void addToCart(CartItemBodyDto cartItemBodyDto) {
        try {
            Optional<Cart> cartExists = cartRepository.findByUserId(cartItemBodyDto.getUserId());
            Cart cart;
            cart = cartExists.orElseGet(() -> createNewCart(cartItemBodyDto.getUserId()));

            Optional<CartItem> existingCartItem = cartItemRepository.findByuserIdAndproductId(cartItemBodyDto.getUserId(),cartItemBodyDto.getProductId());
            if (existingCartItem.isPresent()) {
                CartItem curCartItem = existingCartItem.get();
                curCartItem.setQuantity(cartItemBodyDto.getQuantity());
            } else {
                CartItem cartItem = new CartItem();
                cartItem.setQuantity(cartItemBodyDto.getQuantity());
                cartItem.setProductId(cartItemBodyDto.getProductId());
                cart.getCartItems().add(cartItem);
            }
            cartRepository.save(cart);

        } catch (DataAccessException ex) {
            throw new GeneralInternalException("Database error while adding to cart");
        }
    }

    private Cart createNewCart(Integer userId) {
        Cart cart = new Cart();
        cart.setUserId(userId);
        cart.setCartItems(new ArrayList<>());
        return cartRepository.save(cart);
    }

}
