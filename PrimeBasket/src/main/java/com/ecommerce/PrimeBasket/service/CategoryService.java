package com.ecommerce.PrimeBasket.service;

import com.ecommerce.PrimeBasket.payload.CategoryDTO;
import com.ecommerce.PrimeBasket.payload.CategoryResponse;

public interface CategoryService {
    CategoryResponse getCategories(Integer pageNo, Integer pageSize, String sortBy, String sortOrder);
    CategoryDTO createCategory(CategoryDTO categoryDTO);

    CategoryDTO deleteCategory(Long categoryId);

    CategoryDTO updateCategory(CategoryDTO categoryDTO, Long categoryId);
}
