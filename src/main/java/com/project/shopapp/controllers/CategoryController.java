package com.project.shopapp.controllers;

import com.project.shopapp.dtos.CategoryDTO;
import com.project.shopapp.models.Category;
import com.project.shopapp.service.impl.CategoryService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("${api.prefix}/categories")
@RequiredArgsConstructor
public class CategoryController {

    private final CategoryService categoryService;



    @PostMapping("")
    // nếu request truyền về là một object ?=> data transfer object = request object
    //?  có thế trả về String or List<String>
    public ResponseEntity<?> createCategory(@Valid @RequestBody CategoryDTO categoryDTO,
                                            BindingResult result) {

        if(result.hasErrors()){
            //nếu trong result có list message error => thì get ds trường lỗi nó ra
            // ta muốn chuyển các trường lỗi thành ds chuỗi message ? => biến chúng thành đối tượng Stream() trong java 8
            // và duyệt qua ds trong object stream đó chỉ lấy một một field nào đó (message)=> ánh xạ field đó sang một array khác
            List<String> errorMessages =  result.getFieldErrors()
                    .stream()
                    .map(fieldError -> fieldError.getDefaultMessage())
                    //c2: .map(fieldError::getDefaultMessage)
                    .toList();
            return ResponseEntity.badRequest().body(errorMessages);
        }

        categoryService.createCategory(categoryDTO);

        return ResponseEntity.ok("insert thanh cong");
    }


    //Hiển thị tất các cả categories
    @GetMapping("")//http://localhost:8088/api/v1/categories?page=1&limit=10
    public ResponseEntity<List<Category>> getAllCategories(
            @RequestParam("page") int page,// đầu vào trang số mấy ,lấy bao nhiêu bản gi
            @RequestParam("limit") int limit
    ) {

        List<Category> result = categoryService.getAllCategory();
        return ResponseEntity.ok(result);
    }


    @PutMapping("/{id}")
    public ResponseEntity<String> updateCategory(@PathVariable Long id,
                                                @Valid @RequestBody CategoryDTO categoryDTO
                                             ) {


       categoryService.updateCategory(id,categoryDTO);
        return ResponseEntity.ok("Update Success " + id);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<String> deleteCategory(@PathVariable Long id) {
        categoryService.deleteCategoy(id);
        return ResponseEntity.ok("Delete success " + id);
    }

}
