package com.ecommerce.PrimeBasket.service;

import com.ecommerce.PrimeBasket.exceptions.APIException;
import com.ecommerce.PrimeBasket.exceptions.ResourceNotFoundException;
import com.ecommerce.PrimeBasket.model.*;
import com.ecommerce.PrimeBasket.payload.OrderDTO;
import com.ecommerce.PrimeBasket.payload.OrderItemDTO;
import com.ecommerce.PrimeBasket.payload.PaymentDTO;
import com.ecommerce.PrimeBasket.payload.ProductDTO;
import com.ecommerce.PrimeBasket.repository.*;
import jakarta.transaction.Transactional;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

@Service
public class OrderServiceImplementation implements OrderService{

    @Autowired
    CartRepository cartRepository;

    @Autowired
    AddressRepository addressRepository;

    @Autowired
    PaymentRepository paymentRepository;

    @Autowired
    OrderRepository orderRepository;

    @Autowired
    OrderItemRepository orderItemRepository;

    @Autowired
    ProductRepository productRepository;

    @Autowired
    CartService cartService;

    @Autowired
    ModelMapper modelMapper;

    @Transactional
    @Override
    public OrderDTO placeOrder(String email, Long addressId, String paymentMethod, String pgName, String pgPaymentId, String pgStatus, String pgResponseMessage) {
        //getting user cart
        Cart cart = cartRepository.findCartByEmail(email);
        if(cart==null){
            throw new ResourceNotFoundException("Cart", "email", email);
        }

        Address address = addressRepository.findById(addressId)
                .orElseThrow(() -> new ResourceNotFoundException("Address", "addressId", addressId));

        //creating a new order with payment info
        Order order = new Order();
        order.setEmail(email);
        order.setOrderDate(LocalDate.now());
        order.setAddress(address);
        order.setTotalAmount(cart.getTotalPrice());
        order.setOrderStatus("Order Accepted!");

        Payment payment = new Payment(paymentMethod, pgPaymentId, pgStatus, pgResponseMessage, pgName);
        payment.setOrder(order);
        payment=paymentRepository.save(payment);
        PaymentDTO paymentDTO = modelMapper.map(payment, PaymentDTO.class);

        order.setPayment(payment);

        Order savedOrder=orderRepository.save(order);

        //get items from the cart into orderItems
        List<CartItem> cartItems = cart.getCartItems();
        if(cartItems.isEmpty()){
            throw new APIException("Cart is empty!");
        }

        List<OrderItem> orderItems = new ArrayList<>();
        for(CartItem cartItem : cartItems){
            OrderItem orderItem = new OrderItem();
            orderItem.setProduct(cartItem.getProduct());
            orderItem.setQuantity(cartItem.getQuantity());
            orderItem.setDiscount(cartItem.getDiscount());
            orderItem.setOrderedProductPrice(cartItem.getProductPrice());
            orderItem.setOrder(savedOrder);
            orderItems.add(orderItem);
        }

        orderItems = orderItemRepository.saveAll(orderItems);


        //POST ORDER:
        //update product stock
        cart.getCartItems().forEach(item -> {
            int quantity = item.getQuantity();
            Product product = item.getProduct();
            product.setQuantity(product.getQuantity() - quantity);
            productRepository.save(product);

            //clear the cart
            cartService.deleteProductFromCart(cart.getCartId(), item.getProduct().getProductId());
        });

        //send back the order summary
        OrderDTO orderDTO = modelMapper.map(savedOrder, OrderDTO.class);
        orderItems.forEach(item-> {
                    OrderItemDTO orderItemDTO=(modelMapper.map(item, OrderItemDTO.class));
                    ProductDTO productDTO = modelMapper.map(item.getProduct(), ProductDTO.class);
                    orderItemDTO.setProductDTO(productDTO);
                    orderDTO.getOrderItems().add(orderItemDTO);
                }
        );
        orderDTO.setAddressId(addressId);
        orderDTO.setPaymentDTO(paymentDTO);

        return orderDTO;
    }
}
