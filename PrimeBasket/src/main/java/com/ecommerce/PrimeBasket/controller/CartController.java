package com.ecommerce.PrimeBasket.controller;

import com.ecommerce.PrimeBasket.model.Cart;
import com.ecommerce.PrimeBasket.payload.CartDTO;
import com.ecommerce.PrimeBasket.payload.ProductDTO;
import com.ecommerce.PrimeBasket.repository.CartRepository;
import com.ecommerce.PrimeBasket.service.CartService;
import com.ecommerce.PrimeBasket.util.AuthUtil;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api")
public class CartController {

    @Autowired
    private CartService cartService;

    @Autowired
    private CartRepository cartRepository;

    @Autowired
    private AuthUtil authUtil;

    @Autowired
    private ModelMapper modelMapper;

    @PostMapping("/carts/products/{productId}/quantity/{quantity}")
    public ResponseEntity<CartDTO> addProductToCart(@PathVariable Long productId, @PathVariable Integer quantity) {
        CartDTO cartDTO = cartService.addProductToCart(productId, quantity);
        return new ResponseEntity<CartDTO>(cartDTO, HttpStatus.CREATED);
    }

    @GetMapping("/carts")
    public ResponseEntity<List<CartDTO>> getAllCarts() {
        List<CartDTO> cartDTOS = cartService.getAllCarts();
        return new ResponseEntity<List<CartDTO>>(cartDTOS, HttpStatus.FOUND);
    }

    @GetMapping("/carts/users/cart")
    public ResponseEntity<CartDTO> getCartsByUser() {
        String emailId=authUtil.loggedInEmail();
        Cart cart=cartRepository.findCartByEmail(emailId);
        Long cartId=cart.getCartId();
        CartDTO cartDTO = cartService.getCart(emailId, cartId);
        return new ResponseEntity<CartDTO>(cartDTO, HttpStatus.OK);
    }

    @PutMapping("/cart/products/{productId}/quantity/{operation}")
    public ResponseEntity<CartDTO> updateProductQuantity(@PathVariable Long productId, @PathVariable String operation) {
        CartDTO cartDTO = cartService.updateProductQuantityInCart(productId, operation.equalsIgnoreCase("delete")?-1:1);
        return new ResponseEntity<CartDTO>(cartDTO, HttpStatus.OK);
    }

    @DeleteMapping("/carts/{cartId}/product/{productId}")
    public ResponseEntity<String> deleteProductFromCart(@PathVariable Long cartId, @PathVariable Long productId) {
        String status = cartService.deleteProductFromCart(cartId, productId);
        return new ResponseEntity<String>(status,HttpStatus.OK);
    }
}
