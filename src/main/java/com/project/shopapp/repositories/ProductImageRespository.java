package com.project.shopapp.repositories;

import com.project.shopapp.models.ProductImage;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface ProductImageRespository extends JpaRepository<ProductImage,Long> {

    List<ProductImage> findByProductId (Long productId);
}
