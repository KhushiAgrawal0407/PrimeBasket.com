package com.ecommerce.PrimeBasket.service;

import com.ecommerce.PrimeBasket.payload.CartDTO;
import jakarta.transaction.Transactional;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public interface CartService {
    CartDTO addProductToCart(Long productId, Integer quantity);

    List<CartDTO> getAllCarts();

    CartDTO getCart(String emailId, Long cartId);

    @Transactional
    CartDTO updateProductQuantityInCart(Long productId, Integer quantity);

    String deleteProductFromCart(Long cartId, Long productId);

    //method where when product is updated we want to update the cart also.
    void updateProductInCarts(Long cartId, Long productId);
}
