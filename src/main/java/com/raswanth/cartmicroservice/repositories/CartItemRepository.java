package com.raswanth.cartmicroservice.repositories;

import com.raswanth.cartmicroservice.entity.CartItem;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface CartItemRepository extends JpaRepository<CartItem, Integer> {

@Query("SELECT ci FROM CartItem ci WHERE ci.cart.userId = :userId AND ci.productId=:productId")
    Optional<CartItem> findByuserIdAndproductId(@Param("userId") Integer userId, @Param("productId") Integer productId);
}
