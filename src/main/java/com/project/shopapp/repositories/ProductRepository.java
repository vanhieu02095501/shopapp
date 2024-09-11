package com.project.shopapp.repositories;

import com.project.shopapp.models.Product;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

public interface ProductRepository extends JpaRepository<Product,Long> {

    //kiểm tra có tồn tại tên không
    boolean existsByName (String name);

    //phân trang sản phẩm
    Page<Product> findAll(Pageable pageable);

    @Query("SELECT p from Product p where " +
            "(:categoryId is null or :categoryId=0 or p.category.id = :categoryId)" +
            "and (:keyword is null or :keyword = '' or p.name like %:keyword% or p.description like %:keyword%)")
    Page<Product> searchProduct(@Param("keyword") String keyword,
                                @Param("categoryId") Long categoryId,
                                PageRequest pageRequest);
}
