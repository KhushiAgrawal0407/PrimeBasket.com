package com.ecommerce.PrimeBasket.controller;

import com.ecommerce.PrimeBasket.config.AppConstants;
import com.ecommerce.PrimeBasket.payload.CategoryDTO;
import com.ecommerce.PrimeBasket.payload.CategoryResponse;
import com.ecommerce.PrimeBasket.service.CategoryService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api")
public class CategoryController {

    @Autowired
    private CategoryService categoryService;

    @GetMapping("/public/categories")
    public ResponseEntity<CategoryResponse> getCategories(
            @RequestParam(name = "pageNo", defaultValue = AppConstants.PAGE_NUMBER,required = false) Integer pageNo,
            @RequestParam(name = "pageSize", defaultValue = AppConstants.PAGE_SIZE,required = false) Integer pageSize,
            @RequestParam(name = "sortBy", defaultValue = AppConstants.SORT_CATEGORY_BY, required = false) String sortBy,
            @RequestParam(name = "sortOrder", defaultValue = AppConstants.SORT_DIR, required = false) String sortOrder) {
        CategoryResponse categoryResponse = categoryService.getCategories(pageNo, pageSize, sortBy, sortOrder);
        return new ResponseEntity<>(categoryResponse, HttpStatus.OK);
    }

    @PostMapping("/public/categories")
    public ResponseEntity<CategoryDTO> createCategory(@Valid @RequestBody CategoryDTO category) {
        CategoryDTO savedCategoryDTO = categoryService.createCategory(category);
        return new ResponseEntity<>(savedCategoryDTO, HttpStatus.CREATED);
    }


    @DeleteMapping("/admin/categories/{categoryId}")
    public ResponseEntity<CategoryDTO> deleteCategory(@PathVariable Long categoryId) {
        CategoryDTO deletedCategoryDTO = categoryService.deleteCategory(categoryId);
        return new ResponseEntity<>(deletedCategoryDTO,HttpStatus.OK);
    }

    @PutMapping("/public/categories/{categoryId}")
    public ResponseEntity<CategoryDTO> updateCategory(@Valid @PathVariable Long categoryId, @RequestBody CategoryDTO categoryDTO){
        CategoryDTO savedCategoryDTO = categoryService.updateCategory(categoryDTO, categoryId);
        return new ResponseEntity<>(savedCategoryDTO, HttpStatus.OK);
    }
}
