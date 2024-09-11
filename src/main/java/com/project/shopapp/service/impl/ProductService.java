package com.project.shopapp.service.impl;

import com.project.shopapp.dtos.ProductDTO;
import com.project.shopapp.dtos.ProductImageDTO;
import com.project.shopapp.exception.DataNotFindException;
import com.project.shopapp.exception.InvalidParamException;
import com.project.shopapp.models.Category;
import com.project.shopapp.models.Product;
import com.project.shopapp.models.ProductImage;
import com.project.shopapp.repositories.CategoryRepository;
import com.project.shopapp.repositories.ProductImageRespository;
import com.project.shopapp.repositories.ProductRepository;
import com.project.shopapp.responses.ProductResponse;
import com.project.shopapp.service.IProductService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
public class ProductService implements IProductService {

    @Autowired
    ProductRepository productRepository;

    @Autowired
    CategoryRepository categoryRepository;

    @Autowired
    ProductImageRespository productImageRespository;

    @Override
    public Product createProduct(ProductDTO productDTO) throws DataNotFindException {

        Category existingCategory = categoryRepository.findById(productDTO.getCategoryId())
                .orElseThrow(()->new DataNotFindException("Category not found"));

        Product newProduct = Product
                .builder()
                .name(productDTO.getName())
                .price(productDTO.getPrice())
                .thumbnail(productDTO.getThumbnail())
                .description(productDTO.getDescription())
                .category(existingCategory)
                .build();

        

        return productRepository.save(newProduct);
    }

    @Override
    public Product getProductById(long id) throws DataNotFindException {

        return productRepository.findById(id)
                .orElseThrow(()->new DataNotFindException("Can't find product id:"+ id));

    }

    @Override
    public Page<ProductResponse> getAllProducts(String keyword, Long categoryId,PageRequest pageRequest) {
        //lấy danh sách sản phảm theo page và limit
    //    return productRepository.findAll(pageRequest).map(ProductResponse::fromProduct);
        Page<Product> productPage ;
        productPage = productRepository.searchProduct(keyword,categoryId,pageRequest);

        return productPage.map(ProductResponse::fromProduct);


    }

    @Override
    public Product updateProduct(long id, ProductDTO productDTO) throws DataNotFindException {

        Product existingProduct = getProductById(id);

        Category existingCategory = categoryRepository.findById(productDTO.getCategoryId())
                .orElseThrow(()->new DataNotFindException("Category not found"));

        //coppy các thuộc tính từ DTO sang entity
        // có thể sử dụng ModelMapper
        existingProduct.setName(productDTO.getName());
        existingProduct.setDescription(productDTO.getDescription());
        existingProduct.setThumbnail(productDTO.getThumbnail());
        existingProduct.setCategory(existingCategory);
        existingProduct.setPrice(productDTO.getPrice());

        return productRepository.save(existingProduct);
    }

    @Override
    public void deleteProduct(long id) {

        Optional<Product> existingProduct = productRepository.findById(id);
        existingProduct.ifPresent(product -> productRepository.delete(product));


    }

    @Override
    public boolean existsByName(String name) {
        return productRepository.existsByName(name);
    }

    @Override
    public ProductImage createProductImage(
            Long productId,
            ProductImageDTO productImageDTO
    ) throws  Exception {
        Product existingProduct = productRepository.findById(productId)
                .orElseThrow(()->new DataNotFindException("Can't Product with id:" + productId));

        ProductImage productImage = ProductImage
                .builder()
                .product(existingProduct)
                .imageUrl(productImageDTO.getImageUrl())
                .build();

        //khong cho insert qúa 5 ảnh cho 1 sản phẩm
         int size = productImageRespository.findByProductId(productImageDTO.getProductId()).size();
         if(size > ProductImage.MAXIMUM_IMAGES_PER_PRODUCTS){
             throw new InvalidParamException("Number of Image must be <="+ ProductImage.MAXIMUM_IMAGES_PER_PRODUCTS);

         }
        return productImageRespository.save(productImage);
    }
}
