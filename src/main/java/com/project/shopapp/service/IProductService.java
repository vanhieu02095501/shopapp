package com.project.shopapp.service;

import com.project.shopapp.dtos.ProductDTO;
import com.project.shopapp.dtos.ProductImageDTO;
import com.project.shopapp.exception.DataNotFindException;
import com.project.shopapp.exception.InvalidParamException;
import com.project.shopapp.models.Product;
import com.project.shopapp.models.ProductImage;
import com.project.shopapp.responses.ProductListResponse;
import com.project.shopapp.responses.ProductResponse;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;

public interface IProductService {

    Product createProduct (ProductDTO productDTO) throws DataNotFindException;
    Product getProductById(long id) throws DataNotFindException;
    Page<ProductResponse> getAllProducts(String keyword,Long categoryId,PageRequest pageRequest);

    Product updateProduct(long id ,ProductDTO productDTO) throws DataNotFindException;

    void deleteProduct (long id);
    boolean existsByName(String name);

    ProductImage createProductImage( Long productId, ProductImageDTO productImageDTO
    )throws Exception;
}
