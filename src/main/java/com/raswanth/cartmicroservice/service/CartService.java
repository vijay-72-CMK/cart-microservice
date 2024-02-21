package com.raswanth.cartmicroservice.service;

import com.raswanth.cartmicroservice.dto.AddCartItemBodyDto;
import com.raswanth.cartmicroservice.dto.DeletecartItem;
import com.raswanth.cartmicroservice.entity.Cart;
import com.raswanth.cartmicroservice.entity.CartItem;
import com.raswanth.cartmicroservice.exception.GeneralInternalException;
import com.raswanth.cartmicroservice.repositories.CartItemRepository;
import com.raswanth.cartmicroservice.repositories.CartRepository;
import jakarta.transaction.Transactional;
import org.springframework.dao.DataAccessException;
import org.springframework.http.HttpStatus;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Service;

import java.security.Principal;
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

    public void addToCart(AddCartItemBodyDto addCartItemBodyDto, Principal signedInUser) {
        try {
            Integer userID = Integer.valueOf(signedInUser.getName());
            Cart cart = cartRepository.findByUserId(userID)
                    .orElseGet(() -> createNewCart(userID));

            Optional<CartItem> existingCartItem = cartItemRepository.findByuserIdAndproductId(userID, addCartItemBodyDto.getProductId());
            if (existingCartItem.isPresent()) {
                CartItem curCartItem = existingCartItem.get();
                curCartItem.setQuantity(addCartItemBodyDto.getQuantity());
            } else {
                CartItem cartItem = new CartItem();
                cartItem.setQuantity(addCartItemBodyDto.getQuantity());
                cartItem.setProductId(addCartItemBodyDto.getProductId());
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

    public Cart viewCart(Integer userId) {
        try {
            return cartRepository.findByUserId(userId)
                    .orElseGet(() -> createNewCart(userId));
        } catch (DataAccessException e) {
            throw new GeneralInternalException("Database error while viewing cart");
        }
    }

    public void deleteCartItem(DeletecartItem req) {
        try {
            Integer userId = req.getUserId();
            Cart cart = cartRepository.findByUserId(userId)
                    .orElseGet(() -> createNewCart(userId));
            CartItem existingCartItem = cartItemRepository.findByuserIdAndproductId(userId, req.getProductId())
                    .orElseThrow(() -> new GeneralInternalException("Cannot delete as product id does not exist", HttpStatus.BAD_REQUEST));

            if (existingCartItem.getQuantity() < req.getQuantityToRemove()) {
                throw new GeneralInternalException("Cannot delete more than " + existingCartItem.getQuantity() + " items", HttpStatus.BAD_REQUEST);
            }
            int newQuantity = existingCartItem.getQuantity() - req.getQuantityToRemove();
            if (newQuantity == 0) {
                cartItemRepository.deleteById(existingCartItem.getId());
            } else {
                existingCartItem.setQuantity(newQuantity);
            }
            cartRepository.save(cart);
        } catch (DataAccessException e) {
            throw new GeneralInternalException("Database error while deleting cartItem of user" + req.getUserId());
        }
    }
}
