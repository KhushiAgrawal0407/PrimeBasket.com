package com.ecommerce.PrimeBasket.sevice;

import com.ecommerce.PrimeBasket.exceptions.APIException;
import com.ecommerce.PrimeBasket.exceptions.ResourceNotFoundException;
import com.ecommerce.PrimeBasket.model.Category;
import com.ecommerce.PrimeBasket.model.Product;
import com.ecommerce.PrimeBasket.payload.ProductDTO;
import com.ecommerce.PrimeBasket.payload.ProductResponse;
import com.ecommerce.PrimeBasket.repository.CategoryRepository;
import com.ecommerce.PrimeBasket.repository.ProductRepository;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.List;

@Service
public class ProductServiceImplementation implements ProductService {

    @Autowired
    private ProductRepository productRepository;

    @Autowired
    private CategoryRepository categoryRepository;

    @Autowired
    private ModelMapper modelMapper;

    @Autowired
    private FileService fileService;

    @Value("${project.image}")
    String path;

    public ProductDTO addProduct(Long categoryId, ProductDTO productDTO){
        //validation for if product already exists

        Category category = categoryRepository.findById(categoryId)
                .orElseThrow(()-> new ResourceNotFoundException("category", "categoryId", categoryId));

        Product product = modelMapper.map(productDTO, Product.class);
        Product productExists = productRepository.findByProductName(product.getProductName());
        if(productExists!=null){
            throw new APIException("Product with the name "+product.getProductName()+" already exists!");
        }
        else {
            product.setCategory(category);
            product.setImageURL("default.png");
            double specialPrice = product.getPrice() - ((product.getDiscount() * 0.01) * product.getPrice());
            product.setSpecialPrice(specialPrice);
            Product savedProduct = productRepository.save(product);
            return modelMapper.map(savedProduct, ProductDTO.class);
        }
    }

    public ProductResponse getAllProducts(Integer pageNo, Integer pageSize, String sortBy, String sortOrder){
        Sort sortByAndOrder = sortOrder.equalsIgnoreCase("asc")
                ?Sort.by(sortBy).ascending()
                :Sort.by(sortBy).descending();

        Pageable pageDetails = PageRequest.of(pageNo, pageSize, sortByAndOrder);
        Page<Product> productPage = productRepository.findAll(pageDetails);

        List<Product> products = productPage.getContent();
        List<ProductDTO> productDTOS = products.stream().map(product-> modelMapper.map(product,ProductDTO.class)).toList();

        ProductResponse productResponse = new ProductResponse();
        productResponse.setContent(productDTOS);
        productResponse.setPageNo(productPage.getNumber());
        productResponse.setPageSize(productPage.getSize());
        productResponse.setTotalElements(productPage.getTotalElements());
        productResponse.setTotalPages(productPage.getTotalPages());
        productResponse.setLastPage(productPage.isLast());

        return productResponse;
    }

    public ProductResponse getAllProductsByCatgory(Long categoryId, Integer pageNo, Integer pageSize, String sortBy, String sortOrder) {
        Category category = categoryRepository.findById(categoryId)
                .orElseThrow(()-> new ResourceNotFoundException("category", "categoryId", categoryId));

        Sort sortByAndOrder = sortOrder.equalsIgnoreCase("asc")
                ?Sort.by(sortBy).ascending():Sort.by(sortBy).descending();

        Pageable pageDetails = PageRequest.of(pageNo, pageSize, sortByAndOrder);
        Page<Product> productPage = productRepository.findByCategoryOrderByPriceAsc(category, pageDetails);
        List<Product> products = productPage.getContent();

        if(products.isEmpty()){
            throw new APIException(category.getCategoryName() + " category does not have any products!");
        }

        List<ProductDTO> productDTOS = products.stream().map(product-> modelMapper.map(product,ProductDTO.class)).toList();
        ProductResponse productResponse = new ProductResponse();
        productResponse.setContent(productDTOS);
        productResponse.setPageNo(productPage.getNumber());
        productResponse.setPageSize(productPage.getSize());
        productResponse.setTotalElements(productPage.getTotalElements());
        productResponse.setTotalPages(productPage.getTotalPages());
        productResponse.setLastPage(productPage.isLast());
        return productResponse;
    }

    public ProductResponse getAllProductsByKeyword(String keyword, Integer pageNo, Integer pageSize, String sortBy, String sortOrder) {
        Sort sortByAndOrder = sortOrder.equalsIgnoreCase("asc")
                ?Sort.by(sortBy).ascending():Sort.by(sortBy).descending();

        Pageable pageDetails = PageRequest.of(pageNo, pageSize, sortByAndOrder);
        Page<Product> productPage = productRepository.findByProductNameLikeIgnoreCase('%'+keyword+'%', pageDetails);
        List<Product> products = productPage.getContent();

        if(products.isEmpty()){
            throw new APIException("Products with keyword "+keyword+" not found!");
        }

        List<ProductDTO> productDTOS = products.stream().map(product-> modelMapper.map(product,ProductDTO.class)).toList();
        ProductResponse productResponse = new ProductResponse();
        productResponse.setContent(productDTOS);
        productResponse.setPageNo(productPage.getNumber());
        productResponse.setPageSize(productPage.getSize());
        productResponse.setTotalElements(productPage.getTotalElements());
        productResponse.setTotalPages(productPage.getTotalPages());
        productResponse.setLastPage(productPage.isLast());
        return productResponse;
    }

    public ProductDTO updateProduct(ProductDTO productDTO, Long productId) {
        //get the existing product from database
        //update the product info with the one you got through request body(we are going to separately update productImage)
        //save the product back to database.

        Product existingProduct = productRepository.findById(productId)
                .orElseThrow(()-> new ResourceNotFoundException("product", "productId", productId));
        Product product = modelMapper.map(productDTO, Product.class);
        existingProduct.setProductName(product.getProductName());
        existingProduct.setDescription(product.getDescription());
        existingProduct.setQuantity(product.getQuantity());
        existingProduct.setDiscount(product.getDiscount());
        existingProduct.setPrice(product.getPrice());
        existingProduct.setSpecialPrice(product.getSpecialPrice());

        Product updatedProduct = productRepository.save(existingProduct);
        return modelMapper.map(updatedProduct, ProductDTO.class);

    }

    public ProductDTO deleteProduct(Long productId) {
        Product product = productRepository.findById(productId)
                .orElseThrow(() -> new ResourceNotFoundException("product","productId", productId));
        productRepository.delete(product);
        return modelMapper.map(product, ProductDTO.class);
    }

    public ProductDTO updateImage(Long productId, MultipartFile image) throws IOException {
        //right now we are going to make a folder /image. but when we deploy it we can use S3 bucket to integrate images from internet.

        //get the product form the database.
        Product product = productRepository.findById(productId)
                .orElseThrow(()-> new ResourceNotFoundException("product", "productId", productId));
        //upload image to server (right now a /image directory)
        //get the file name of the updated image
        String fileName = fileService.uploadImage(path, image);
        //updating the new file name to the product
        product.setImageURL(fileName);
        //save updated product
        Product updatedProduct = productRepository.save(product);
        //return dto after mapping product to dto
        return modelMapper.map(updatedProduct, ProductDTO.class);
    }
}
