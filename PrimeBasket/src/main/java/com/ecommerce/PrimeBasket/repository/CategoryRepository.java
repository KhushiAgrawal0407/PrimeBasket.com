package com.ecommerce.PrimeBasket.repository;

import com.ecommerce.PrimeBasket.model.Category;
import org.springframework.data.jpa.repository.JpaRepository;

public interface CategoryRepository extends JpaRepository<Category, Long> {

}
