package com.ecommerce.PrimeBasket.sevice;

import com.ecommerce.PrimeBasket.payload.ProductDTO;
import com.ecommerce.PrimeBasket.payload.ProductResponse;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;

public interface ProductService {
    ProductDTO addProduct(Long categoryId, ProductDTO productDTO);

    ProductResponse getAllProducts(Integer pageNo, Integer pageSize, String sortBy, String sortOrder);

    ProductResponse getAllProductsByCatgory(Long categoryId, Integer pageNo, Integer pageSize, String sortBy, String sortOrder);

    ProductResponse getAllProductsByKeyword(String keyword, Integer pageNo, Integer pageSize, String sortBy, String sortOrder);

    ProductDTO updateProduct(ProductDTO productDTO, Long productId);

    ProductDTO deleteProduct(Long productId);

    ProductDTO updateImage(Long productId, MultipartFile image) throws IOException;
}
