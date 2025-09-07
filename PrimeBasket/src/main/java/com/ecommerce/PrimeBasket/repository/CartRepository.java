package com.ecommerce.PrimeBasket.repository;

import com.ecommerce.PrimeBasket.model.Cart;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface CartRepository extends JpaRepository<Cart, Long> {
    @Query("SELECT c from Cart c WHERE c.user.email=?1")
    Cart findCartByEmail(String email);

    //it might sometimes work without query this one
    @Query("SELECT c from Cart c WHERE c.user.email=?1 and c.id=?2")
    Cart findCartByEmailAndCartId(String emailId, Long cartId);

    @Query("SELECT c from Cart c JOIN FETCH c.cartItems ci JOIN FETCH ci.product p WHERE p.id=?1")
    List<Cart> findCartsByProductId(Long productId);
}
