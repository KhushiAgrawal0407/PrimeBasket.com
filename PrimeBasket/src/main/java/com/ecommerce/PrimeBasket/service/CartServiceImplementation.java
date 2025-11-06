package com.ecommerce.PrimeBasket.service;

import com.ecommerce.PrimeBasket.exceptions.APIException;
import com.ecommerce.PrimeBasket.exceptions.ResourceNotFoundException;
import com.ecommerce.PrimeBasket.model.Cart;
import com.ecommerce.PrimeBasket.model.CartItem;
import com.ecommerce.PrimeBasket.model.Product;
import com.ecommerce.PrimeBasket.payload.CartDTO;
import com.ecommerce.PrimeBasket.payload.CartItemDTO;
import com.ecommerce.PrimeBasket.payload.ProductDTO;
import com.ecommerce.PrimeBasket.repository.CartItemRepository;
import com.ecommerce.PrimeBasket.repository.CartRepository;
import com.ecommerce.PrimeBasket.repository.ProductRepository;
import com.ecommerce.PrimeBasket.util.AuthUtil;
import jakarta.transaction.Transactional;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;

@Service
public class CartServiceImplementation implements CartService {

    @Autowired
    CartRepository cartRepository;

    @Autowired
    AuthUtil authUtil;

    @Autowired
    ProductRepository productRepository;

    @Autowired
    CartItemRepository cartItemRepository;

    @Autowired
    ModelMapper modelMapper;

    @Override
    public CartDTO addProductToCart(Long productId, Integer quantity) {
        //find existing cart or create one
        Cart cart=createCart();

        //retrieve product details
        Product product=productRepository.findById(productId)
                .orElseThrow(()-> new ResourceNotFoundException("Product","productId",productId));

        //perform validations
        //if this product exists in this users cart only
        CartItem cartItem=cartItemRepository.findCartItemByProductIdAndCartId(
                cart.getCartId(),
                productId
        );
        if(cartItem!=null){
            throw new APIException("Product " + product.getProductName() + " already exists");
        }
        if(product.getQuantity()==0){
            throw new APIException(product.getProductName() + " is not available");
        }
        if(product.getQuantity()<quantity){
            throw new APIException("Please, make an order of the " + product.getProductName() + "less than or equal to the quantity " + product.getQuantity() + ".");
        }

        //create cart item
        CartItem newCartItem=new CartItem();

        newCartItem.setProduct(product);
        newCartItem.setCart(cart);
        newCartItem.setQuantity(quantity);
        newCartItem.setDiscount(product.getDiscount());
        newCartItem.setProductPrice(product.getSpecialPrice());

        //save cart item
        cartItemRepository.save(newCartItem);

        //return the updated cart
        product.setQuantity(product.getQuantity()); //we are not reducing the quantity when the item is added but rather when the item is ordered.
        cart.setTotalPrice(cart.getTotalPrice()+(product.getSpecialPrice()*quantity));
        cartRepository.save(cart);
        CartDTO cartDTO=modelMapper.map(cart, CartDTO.class);
        List<CartItem> cartItems = cart.getCartItems();
        Stream<ProductDTO> productDTOStream = cartItems.stream().map(item->{
            ProductDTO mp=modelMapper.map(item.getProduct(), ProductDTO.class);
            mp.setQuantity(item.getQuantity());
            return mp;
        });
        cartDTO.setProductDTO(productDTOStream.toList());
        return cartDTO;
    }

    @Override
    public List<CartDTO> getAllCarts() {
        List<Cart> carts = cartRepository.findAll();

        if(carts.size()==0){
            throw new APIException("No cart exists");
        }

        List<CartDTO> cartDTOS = carts.stream()
                .map(cart->{
                    CartDTO cartDTO = modelMapper.map(cart,CartDTO.class);
                    //Cart has something called CartItem, and the CartItem has Product in it, but CartDTO has ProductDTO
                    // so what we need to do is from this CartItem we need to get this Product and transform it to ProductDTO, and once we have the list of ProductDTO we'll send it to CartDTO
                    //ie we'll make a List of ProductDTOs.
                    List<ProductDTO> products = cart.getCartItems().stream().map(cartItem -> {
                        ProductDTO productDTO = modelMapper.map(cartItem.getProduct(), ProductDTO.class);
                        productDTO.setQuantity(cartItem.getQuantity()); // Set the quantity from CartItem
                        return productDTO;
                    }).collect(Collectors.toList());

                    cartDTO.setProductDTO(products);
                    return cartDTO;
                }).collect(Collectors.toList());

        return cartDTOS;
    }

    @Override
    public CartDTO getCart(String emailId, Long cartId) {
        Cart cart=cartRepository.findCartByEmailAndCartId(emailId, cartId);
        if(cart==null){
            throw new ResourceNotFoundException("Cart","cartId",cartId);
        }
        CartDTO cartDTO=modelMapper.map(cart, CartDTO.class);

        //cart.getCartItems().forEach(c->c.getProduct().setQuantity(c.getQuantity()));

        List<ProductDTO> products=cart.getCartItems().stream()
                .map(product->modelMapper.map(product.getProduct(), ProductDTO.class))
                .collect(Collectors.toList());
        cartDTO.setProductDTO(products);
        return cartDTO;
    }

    //when there are multiple operations, we use Transactional, as it ensures that either all operations are done or none are.
    //eg: in bank if there was problem in transferring the money, no operations will be held, as the credit has been done so it will become unfair.
    @Transactional
    @Override
    public CartDTO updateProductQuantityInCart(Long productId, Integer quantity) {
        String emailId=authUtil.loggedInEmail();
        Cart userCart = cartRepository.findCartByEmail(emailId);
        Long cartId=userCart.getCartId();

        //first validate if the cart exists?
        Cart cart = cartRepository.findById(cartId)
                .orElseThrow(()->new ResourceNotFoundException("Cart","cartId",cartId));

        //then validate if the product exists?
        Product product = productRepository.findById(productId)
                .orElseThrow(()->new ResourceNotFoundException("Product","productId",productId));

        //same validations as add product to cart
        if(product.getQuantity()==0){
            throw new APIException(product.getProductName() + " is not available");
        }
        if(product.getQuantity()<quantity){
            throw new APIException("Please, make an order of the " + product.getProductName() + "less than or equal to the quantity " + product.getQuantity() + ".");
        }

        //validate if the product exists in the cart
        CartItem cartItem = cartItemRepository.findCartItemByProductIdAndCartId(cartId,productId);
        if(cartItem==null){
            throw new APIException("Product " + product.getProductName() + " not available in the cart!");
        }

        int newQuantity=cartItem.getQuantity()+quantity;
        //validation to  prevent negative quantities
        if(newQuantity<0){
            throw new APIException("The resulting quantity cannot be negative!!");
        }
        if(newQuantity==0){
            deleteProductFromCart(cartId,productId);
        }
        else {
            cartItem.setProductPrice(product.getSpecialPrice());
            cartItem.setQuantity(cartItem.getQuantity() + quantity);
            cartItem.setDiscount(product.getDiscount());
            cart.setTotalPrice(cart.getTotalPrice() + (cartItem.getProductPrice() * quantity));
            cartRepository.save(cart);
        }

        CartItem updatedCartItem=cartItemRepository.save(cartItem);
        if(updatedCartItem.getQuantity()==0){
            cartItemRepository.deleteById(updatedCartItem.getCartItemId());
        }

        CartDTO cartDTO = modelMapper.map(cart, CartDTO.class);
        List<CartItem> cartItems = cart.getCartItems();
        //now we'll update the list of products
        Stream<ProductDTO> productDTOS = cartItems.stream()
                .map(item->{
                    ProductDTO prod=modelMapper.map(item.getProduct(), ProductDTO.class);
                    prod.setQuantity(item.getQuantity());
                    return prod;
                });

        cartDTO.setProductDTO(productDTOS.toList());
        return cartDTO;
    }

    @Transactional
    @Override
    public String deleteProductFromCart(Long cartId, Long productId) {
        //add validations
        //check whether cartId exists or not
        Cart cart=cartRepository.findById(cartId)
                .orElseThrow(()->new ResourceNotFoundException("Cart","cartId",cartId));
        CartItem cartItem = cartItemRepository.findCartItemByProductIdAndCartId(cartId,productId);
        if(cartItem==null){
            throw new ResourceNotFoundException("Product", "productId",productId);
        }
        cart.setTotalPrice(cart.getTotalPrice()-cartItem.getProductPrice()*cartItem.getQuantity());
        cartItemRepository.deleteCartItemByProductIdAndCartId(cartId, productId);

        return ("Product " + cartItem.getProduct().getProductName() + " has been removed from the cart.");
    }

    @Override
    public void updateProductInCarts(Long cartId, Long productId) {
        //first validate if the cart exists?
        Cart cart = cartRepository.findById(cartId)
                .orElseThrow(()->new ResourceNotFoundException("Cart","cartId",cartId));

        //then validate if the product exists?
        Product product = productRepository.findById(productId)
                .orElseThrow(()->new ResourceNotFoundException("Product","productId",productId));

        CartItem cartItem=cartItemRepository.findCartItemByProductIdAndCartId(cartId,productId);
        if(cartItem==null){
            throw new APIException("Product " + product.getProductName() + " not available in the cart!");
        }

        double cartPrice = cart.getTotalPrice()-(cartItem.getProductPrice()*cartItem.getQuantity());
        //if the price of the product has increased or decreased you set the price for the cartItem product
        cartItem.setProductPrice(product.getSpecialPrice());
        //you are re-setting the total price of the cart.
        cart.setTotalPrice(cartPrice + cartItem.getProductPrice()*cartItem.getQuantity());
        cartItem=cartItemRepository.save(cartItem);
    }

    @Transactional
    @Override
    public CartDTO createOrUpdateCartWithItems(List<CartItemDTO> cartItems) {
        System.out.println("Backend received this payload: " + cartItems);
        //get user's email
        String emailId=authUtil.loggedInEmail();

        //check if an existing cart is available or create a new one
        Cart existingCart = cartRepository.findCartByEmail(emailId);
        if(existingCart==null){
            existingCart=new Cart();
            existingCart.setTotalPrice(0.0);
            existingCart.setUser(authUtil.loggedInUser());
            existingCart=cartRepository.save(existingCart);
        }
        else{
            //clear all the current items in the existing cart
            cartItemRepository.deleteAllByCartId(existingCart.getCartId());
        }

        //process each item in the list to add to the cart
        double totalPrice=0.0;
        for(CartItemDTO cartItemDTO:cartItems){
            Long productId=cartItemDTO.getProductId();
            Integer quantity=cartItemDTO.getQuantity();

            //find the product by id
            Product product = productRepository.findById(productId)
                    .orElseThrow(()->new ResourceNotFoundException("Product", "productId",productId));

            //directly update product stock and total price
            if (product.getQuantity() < quantity) {
                throw new APIException("Insufficient stock for product " + product.getProductName());
            }
            totalPrice+=product.getSpecialPrice()*quantity;

            //create and save cart item
            CartItem cartItem=new CartItem();
            cartItem.setProduct(product);
            cartItem.setCart(existingCart);
            cartItem.setQuantity(quantity);
            cartItem.setProductPrice(product.getSpecialPrice());
            cartItem.setDiscount(product.getDiscount());
            cartItemRepository.save(cartItem);
        }

        //update the cart's total price and save
        existingCart.setTotalPrice(totalPrice);
        cartRepository.save(existingCart);

        List<CartItem> savedCartItems = cartItemRepository.findByCart(existingCart);

        CartDTO cartDTO = modelMapper.map(existingCart, CartDTO.class);
        //now we'll update the list of products
        List<ProductDTO> productDTOS = savedCartItems.stream()
                .map(item->{
                    ProductDTO prod=modelMapper.map(item.getProduct(), ProductDTO.class);
                    prod.setQuantity(item.getQuantity());
                    return prod;
                }).collect(Collectors.toList());;

        cartDTO.setProductDTO(productDTOS);
        System.out.println("Backend is sending this CartDTO: " + cartDTO);
        return cartDTO;
    }

    private Cart createCart(){
        Cart userCart=cartRepository.findCartByEmail((authUtil.loggedInEmail()));
        if(userCart!=null){
            return userCart;
        }

        Cart cart=new Cart();
        cart.setTotalPrice(0.00);
        cart.setUser(authUtil.loggedInUser());

        Cart newCart=cartRepository.save(cart);
        return newCart;
    }
}
