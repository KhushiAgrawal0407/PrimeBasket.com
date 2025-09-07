package com.ecommerce.PrimeBasket.service;

import com.ecommerce.PrimeBasket.payload.OrderDTO;
import jakarta.transaction.Transactional;

public interface OrderService {
    @Transactional
    public OrderDTO placeOrder(String email, Long addressId, String paymentMethod, String pgName, String pgPaymentId, String pgStatus, String pgResponseMessage);
}
