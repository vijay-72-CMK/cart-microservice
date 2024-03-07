package com.raswanth.cartmicroservice.service;

import com.raswanth.cartmicroservice.dto.CartItemAdjustmentDto;
import com.raswanth.cartmicroservice.dto.ProductDto;
import com.raswanth.cartmicroservice.entity.Cart;
import com.raswanth.cartmicroservice.entity.CartItem;
import com.raswanth.cartmicroservice.exception.GeneralInternalException;
import com.raswanth.cartmicroservice.repositories.CartItemRepository;
import com.raswanth.cartmicroservice.repositories.CartRepository;
import jakarta.transaction.Transactional;
import org.springframework.dao.DataAccessException;
import org.springframework.http.HttpStatus;
import org.springframework.http.HttpStatusCode;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.parameters.P;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Mono;

import java.security.Principal;
import java.util.ArrayList;
import java.util.Optional;


@Service
public class CartService {

    private final CartRepository cartRepository;
    private final WebClient webClient;
    private final CartItemRepository cartItemRepository;


    public CartService(CartRepository cartRepository, WebClient.Builder webClientBuilder, CartItemRepository cartItemRepository) {
        this.cartRepository = cartRepository;
        this.cartItemRepository = cartItemRepository;
        this.webClient = webClientBuilder.baseUrl("http://localhost:8081/api/products").build();
    }

    @Transactional
    public void updateCart(CartItemAdjustmentDto cartItemAdjustmentDto, Principal signedInUser) {
        try {
            Integer requestedQuantity = cartItemAdjustmentDto.getQuantity();

            if (requestedQuantity == 0) {
                throw new GeneralInternalException("Cannot add or remove zero items",
                        HttpStatus.BAD_REQUEST);
            }
            Integer userID = Integer.valueOf(signedInUser.getName());
            ProductDto product = webClient
                    .get().uri("/get-quantity/{productId}", cartItemAdjustmentDto.getProductId())
                    .retrieve()
                    .onStatus(HttpStatusCode::is4xxClientError, error ->
                            Mono.error(new GeneralInternalException("Invalid product Id", HttpStatus.BAD_REQUEST)))
                    .onStatus(HttpStatusCode::is5xxServerError, error ->
                            Mono.error(new GeneralInternalException("Something went wrong in database when retrieving quantity, try again")))
                    .bodyToMono(ProductDto.class)
                    .block();

            if (product == null) {
                throw new GeneralInternalException("Could not retrieve product quantity in update cart");
            }

            int availableQuantity = product.getAvailableQuantity();
            if (requestedQuantity > 0 && availableQuantity < requestedQuantity) {
                throw new GeneralInternalException("Cannot add as max quantity available is " + availableQuantity, HttpStatus.BAD_REQUEST);
            }


            Cart cart = cartRepository.findByUserId(userID)
                    .orElseGet(() -> createNewCart(userID));

            Optional<CartItem> existingCartItem = cartItemRepository.
                    findByuserIdAndproductId(userID, cartItemAdjustmentDto.getProductId());


            if (existingCartItem.isPresent()) {
                CartItem curCartItem = existingCartItem.get();
                int newQuantity = curCartItem.getQuantity() + requestedQuantity;
                if (newQuantity < 0) {
                    throw new GeneralInternalException("Cannot remove more items than what exists in the cart" + curCartItem.getQuantity(), HttpStatus.BAD_REQUEST);
                }
                if (newQuantity > availableQuantity) {
                    throw new GeneralInternalException("Not enough stock. Maximum available quantity is " + availableQuantity, HttpStatus.BAD_REQUEST);
                }
                if (newQuantity == 0) {
                    cartItemRepository.deleteById(curCartItem.getId());
                } else {
                    curCartItem.setQuantity(newQuantity);
                    cartItemRepository.save(curCartItem);
                }
            } else {
                if (requestedQuantity < 0) {
                    throw new GeneralInternalException("Cannot remove product that does not exist in cart", HttpStatus.BAD_REQUEST);
                }
                CartItem newCartItem = new CartItem();
                newCartItem.setProductId(cartItemAdjustmentDto.getProductId());
                newCartItem.setQuantity(requestedQuantity);
                cart.getCartItems().add(newCartItem);
            }
            cartRepository.save(cart);
        } catch (DataAccessException ex) {
            throw new GeneralInternalException("Database error while adding to cart");
        }
    }

    private Cart createNewCart(Integer userId) {
        try {
            Cart cart = new Cart();
            cart.setUserId(userId);
            cart.setCartItems(new ArrayList<>());
            return cartRepository.save(cart);
        } catch (DataAccessException e) {
            throw new GeneralInternalException("Database error when trying to make new cart");
        }
    }

    public Cart viewCart(Principal singedInUser) {
        try {
            Integer userId = Integer.valueOf(singedInUser.getName());
            return cartRepository.findByUserId(userId)
                    .orElseGet(() -> createNewCart(userId));
        } catch (DataAccessException e) {
            throw new GeneralInternalException("Database error while viewing cart");
        }
    }

    @Transactional
    public void deleteCart(Principal signedInUser) {
        try {
            Integer userId = Integer.valueOf(signedInUser.getName());
            cartRepository.deleteByUserId(userId);
        } catch (DataAccessException e) {
            throw new GeneralInternalException("Database error while deleting cart");
        }
    }
}
