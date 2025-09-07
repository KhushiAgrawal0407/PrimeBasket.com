package com.ecommerce.PrimeBasket.repository;

import com.ecommerce.PrimeBasket.model.Address;
import org.springframework.data.jpa.repository.JpaRepository;

public interface AddressRepository extends JpaRepository<Address, Long> {
}
