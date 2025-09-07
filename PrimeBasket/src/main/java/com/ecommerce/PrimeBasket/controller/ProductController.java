package com.ecommerce.PrimeBasket.controller;

import com.ecommerce.PrimeBasket.config.AppConstants;
import com.ecommerce.PrimeBasket.payload.ProductDTO;
import com.ecommerce.PrimeBasket.payload.ProductResponse;
import com.ecommerce.PrimeBasket.service.ProductService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;

@RestController
@RequestMapping("/api")
public class ProductController {

    @Autowired
    ProductService productService;

    @PostMapping("/admin/categories/{categoryId}/product")
    public ResponseEntity<ProductDTO> addProduct(@Valid @RequestBody ProductDTO productDTO, @PathVariable Long categoryId){
        ProductDTO newProductDTO = productService.addProduct(categoryId, productDTO);
        return new ResponseEntity<>(newProductDTO, HttpStatus.CREATED);
    }

    @GetMapping("/public/product")
    public ResponseEntity<ProductResponse> getAllProducts(
            @RequestParam(name = "pageNo", defaultValue = AppConstants.PAGE_NUMBER, required = false) Integer pageNo,
            @RequestParam(name = "pageSize", defaultValue = AppConstants.PAGE_SIZE, required = false) Integer pageSize,
            @RequestParam(name = "sortBy", defaultValue = AppConstants.SORT_PRODUCTS_BY, required = false) String sortBy,
            @RequestParam(name = "sortOrder", defaultValue = AppConstants.SORT_DIR, required = false) String sortOrder
    ){
        ProductResponse productResponse = productService.getAllProducts(pageNo, pageSize, sortBy, sortOrder);
        return new ResponseEntity<>(productResponse, HttpStatus.OK);
    }

    @GetMapping("/public/categories/{categoryId}/product")
    public ResponseEntity<ProductResponse> getAllProductsByCategory(
            @PathVariable Long categoryId,
            @RequestParam(name = "pageNo", defaultValue = AppConstants.PAGE_NUMBER, required = false) Integer pageNo,
            @RequestParam(name = "pageSize", defaultValue = AppConstants.PAGE_SIZE, required = false) Integer pageSize,
            @RequestParam(name = "sortBy", defaultValue = AppConstants.SORT_PRODUCTS_BY, required = false) String sortBy,
            @RequestParam(name = "sortOrder", defaultValue = AppConstants.SORT_DIR, required = false) String sortOrder
    ){
        ProductResponse productResponse = productService.getAllProductsByCatgory(categoryId, pageNo, pageSize, sortBy, sortOrder);
        return new ResponseEntity<>(productResponse, HttpStatus.OK);
    }

    @GetMapping("public/product/keyword/{keyword}")
    public ResponseEntity<ProductResponse> getAllProductsByKeyword(
            @PathVariable String keyword,
            @RequestParam(name = "pageNo", defaultValue = AppConstants.PAGE_NUMBER, required = false) Integer pageNo,
            @RequestParam(name = "pageSize", defaultValue = AppConstants.PAGE_SIZE, required = false) Integer pageSize,
            @RequestParam(name = "sortBy", defaultValue = AppConstants.SORT_PRODUCTS_BY, required = false) String sortBy,
            @RequestParam(name = "sortOrder", defaultValue = AppConstants.SORT_DIR, required = false) String sortOrder
    ){
        ProductResponse productResponse = productService.getAllProductsByKeyword(keyword, pageNo, pageSize, sortBy, sortOrder);
        return new ResponseEntity<>(productResponse, HttpStatus.FOUND);
    }

    @PutMapping("admin/product/{productId}")
    public ResponseEntity<ProductDTO> updateProduct(@Valid @RequestBody ProductDTO productDTO, @PathVariable Long productId){
        ProductDTO newProductDTO = productService.updateProduct(productDTO, productId);
        return new ResponseEntity<>(newProductDTO, HttpStatus.OK);
    }

    @DeleteMapping("/admin/product/{productId}")
    public ResponseEntity<ProductDTO> deleteProduct(@PathVariable Long productId){
        ProductDTO productDTO = productService.deleteProduct(productId);
        return new ResponseEntity<>(productDTO, HttpStatus.OK);
    }

    @PutMapping("admin/product/{productId}/image")
    public ResponseEntity<ProductDTO> updateImage(@PathVariable Long productId, @RequestParam("image")MultipartFile image) throws IOException {
        ProductDTO productDTO = productService.updateImage(productId, image);
        return new ResponseEntity<>(productDTO, HttpStatus.OK);
    }
}
