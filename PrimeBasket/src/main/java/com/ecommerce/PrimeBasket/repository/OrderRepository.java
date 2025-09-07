package com.ecommerce.PrimeBasket.repository;

import com.ecommerce.PrimeBasket.model.Order;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface OrderRepository extends JpaRepository<Order, Long> {
}
