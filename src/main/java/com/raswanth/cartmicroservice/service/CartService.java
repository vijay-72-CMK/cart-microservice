package com.raswanth.cartmicroservice.service;

import com.raswanth.cartmicroservice.dto.UpdateCartDto;
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
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Mono;

import java.security.Principal;
import java.util.ArrayList;
import java.util.Objects;
import java.util.Optional;


@Service
@Transactional
public class CartService {

    private final CartRepository cartRepository;
    private final WebClient productWebClient;
    private final CartItemRepository cartItemRepository;


    public CartService(CartRepository cartRepository, WebClient productWebClient, CartItemRepository cartItemRepository) {
        this.cartRepository = cartRepository;
        this.productWebClient = productWebClient;
        this.cartItemRepository = cartItemRepository;
    }

    public void updateCart(UpdateCartDto updateCartDto, Principal signedInUser) {
        try {
            Integer userID = Integer.valueOf(signedInUser.getName());
            ProductDto product = productWebClient
                    .get().uri("/get-quantity/{productId}", updateCartDto.getProductId())
                    .retrieve()
                    .onStatus(HttpStatusCode::is4xxClientError, error ->
                            Mono.error(new GeneralInternalException("Invalid product Id", HttpStatus.BAD_REQUEST)))
                    .onStatus(HttpStatusCode::is5xxServerError, error ->
                            Mono.error(new GeneralInternalException("Something went wrong in database when retreiving quantity, try again")))
                    .bodyToMono(ProductDto.class)
                    .block();

            if (product == null) {
                throw new GeneralInternalException("Could not retrieve product quantity in update cart");
            }

            int availableQuantity = product.getAvailableQuantity();
            Integer updatedQuantity = updateCartDto.getUpdatedQuantity();
            if (availableQuantity < updatedQuantity) {
                throw new GeneralInternalException("Updated quantity cannot be greater than " + availableQuantity, HttpStatus.BAD_REQUEST);
            }

            Cart cart = cartRepository.findByUserId(userID)
                    .orElseGet(() -> createNewCart(userID));

            Optional<CartItem> existingCartItem = cartItemRepository.
                    findByuserIdAndproductId(userID, updateCartDto.getProductId());


            if (existingCartItem.isPresent()) {
                CartItem curCartItem = existingCartItem.get();
                if (Objects.equals(curCartItem.getQuantity(), updatedQuantity)) {
                    throw new GeneralInternalException("Updated quantity and previous quantity are same in update cart",
                            HttpStatus.BAD_REQUEST);
                }
                curCartItem.setQuantity(updatedQuantity);
                if (updatedQuantity == 0) {
                    cartItemRepository.deleteById(curCartItem.getId());
                } else {
                    curCartItem.setQuantity(updatedQuantity);
                }
            } else {
                if (updatedQuantity == 0) {
                    throw new GeneralInternalException("Cannot add product with id " + updateCartDto.getProductId() +
                            " to cart whose quantity is zero", HttpStatus.BAD_REQUEST);
                }
                CartItem cartItem = new CartItem();
                cartItem.setQuantity(updatedQuantity);
                cartItem.setProductId(updateCartDto.getProductId());
                cart.getCartItems().add(cartItem);
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
}
