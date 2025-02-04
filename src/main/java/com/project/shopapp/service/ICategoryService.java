package com.project.shopapp.service;

import com.project.shopapp.dtos.CategoryDTO;
import com.project.shopapp.models.Category;
import org.springframework.stereotype.Service;

import java.util.List;


public interface ICategoryService {
    Category createCategory (CategoryDTO categoryDTO);
    Category getCategoryById (long id);

    List<Category> getAllCategory();
    Category updateCategory(long id,CategoryDTO categoryDTO);
    void deleteCategoy(long id);
}
