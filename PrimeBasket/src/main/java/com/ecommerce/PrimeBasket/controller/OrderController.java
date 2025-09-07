package com.ecommerce.PrimeBasket.controller;

import com.ecommerce.PrimeBasket.payload.OrderDTO;
import com.ecommerce.PrimeBasket.payload.OrderRequestDTO;
import com.ecommerce.PrimeBasket.service.OrderService;
import com.ecommerce.PrimeBasket.util.AuthUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api")
public class OrderController {
    @Autowired
    OrderService orderService;

    @Autowired
    AuthUtil authUtil;

    @PostMapping("/orders/users/payment/{paymentMethod}")
    public ResponseEntity<OrderDTO> placeOrder(@PathVariable String paymentMethod, @RequestBody OrderRequestDTO orderRequestDTO) {
        String email = authUtil.loggedInEmail();
        OrderDTO orderDTO = orderService.placeOrder(
                email,
                orderRequestDTO.getAddressId(),
                paymentMethod,
                orderRequestDTO.getPgName(),
                orderRequestDTO.getPgPaymentId(),
                orderRequestDTO.getPgStatus(),
                orderRequestDTO.getPgResponseMessage()
        );
        return new ResponseEntity<>(orderDTO, HttpStatus.CREATED);
    }
}
