package com.project.shopapp.dtos;

import com.fasterxml.jackson.annotation.JsonProperty;
import jakarta.validation.constraints.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class ProductDTO {
    @NotBlank(message = "Name is required.")
    @Size(min = 3,max = 200, message = "Title must be from 3 to 200 characters.")
    private String name;

    @Min(value = 0, message = "Price must be greater than or equal to 0")
    @Max(value=100000, message = "Price must be less than or equal to 100000")
    private Float price;

    //@NotBlank(message = "Thumbnail is required")
    private String thumbnail;


    @NotBlank(message = "Description is required.")
    private String description;


    @JsonProperty("category_id")
    private Long categoryId;

    private List<MultipartFile> files;

//    @NotNull(message = "Color ID is required.")
//    @JsonProperty("color_id")
//    private Long colorId;

    //    @Min(value = 0 , message = "Discount must be greater thanh or equal to 0")
//    @Max(value=100, message = "Discount must be less than or equal to 100")
//    private String discount;
}
