package com.project.shopapp.controllers;

import com.github.javafaker.Faker;
import com.project.shopapp.dtos.ProductDTO;
import com.project.shopapp.dtos.ProductImageDTO;
import com.project.shopapp.exception.DataNotFindException;
import com.project.shopapp.models.Product;
import com.project.shopapp.models.ProductImage;
import com.project.shopapp.responses.ProductListResponse;
import com.project.shopapp.responses.ProductResponse;
import com.project.shopapp.service.impl.ProductService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.UrlResource;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.util.StringUtils;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.UUID;

@RestController
@RequestMapping("${api.prefix}/products")
public class ProductController {
    @Autowired
    ProductService productService;

    @GetMapping("/{id}")
    public ResponseEntity<?> getProductById(@PathVariable("id") Long id){

        try {
            Product existingProduct = productService.getProductById(id);

            return ResponseEntity.ok(ProductResponse.fromProduct(existingProduct));
        } catch (DataNotFindException e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }


    @GetMapping("")
    public ResponseEntity<ProductListResponse> getProducts(
            @RequestParam(defaultValue = "") String keyword,
            @RequestParam(defaultValue = "0",name = "categoryId") Long categoryId,
            @RequestParam("page") int page, //số thứ tự của trang
            @RequestParam("limit") int limit // số lượng  bản ghi tối đa của một trang

    ) {
        //Tạo pageable từ thông tin trang và giới hạn ,Sắp xếp các bản ghi theo trường created_at theo thứ tự giảm dần (từ bản ghi mới nhất đến
        PageRequest pageRequest = PageRequest.of(page,limit,
              //  Sort.by("createdAt").descending());
                Sort.by("id").ascending());

        // chứa ds các đối tượng product tương ứng với trang được yêu cầu
        Page<ProductResponse> productPage = productService.getAllProducts(keyword,categoryId, pageRequest);
        // lấy tổng số  trang
        int totalPages = productPage.getTotalPages();

        //lấy ds các bản ghi trong trang hiện tại
        List<ProductResponse> productResponses = productPage.getContent();

        return ResponseEntity.ok(ProductListResponse
                .builder()
                        .productResponses(productResponses)
                        .total(totalPages)
                .build());
       // return ResponseEntity.ok(String.format("Lấy ds sách các sản phẩm page : %d và limit: %d", page, limit));
    }

    //Đoạn này xác định rằng phương thức này sẽ xử lý các yêu cầu có content type là multipart/form-data.
    // Đây là kiểu nội dung thường được sử dụng khi tải lên các tệp (file) thông qua một form HTML.
    @PostMapping(value = "")
    public ResponseEntity<?> createProduct(@Valid @RequestBody ProductDTO productDTO,
                                           BindingResult result) {
        try {
            if (result.hasErrors()) {
                List<String> messageErrors = result.getFieldErrors()
                        .stream()
                        .map(fieldError -> fieldError.getDefaultMessage())
                        .toList();
                return ResponseEntity.badRequest().body(messageErrors);
            }

            //lưu trước mới có id để thêm ảnh được
            Product newProduct = productService.createProduct(productDTO);

            return ResponseEntity.ok(newProduct);
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }


    }
    // kiểm tra có phải file ảnh hay không
    private boolean isImageFile(MultipartFile file){
        String contentType = file.getContentType();
        return contentType!=null && contentType.startsWith("image/");

    }

    @GetMapping("/images/{imageName}")
    private ResponseEntity<?> viewImage(@PathVariable("imageName") String imageName ){

        try{
            Path imagePath = Paths.get("uploads/"+imageName);
            UrlResource resource = new UrlResource(imagePath.toUri());

            if (resource.exists()){
                return ResponseEntity.ok()
                        .contentType(MediaType.IMAGE_JPEG)
                        .body(resource);
            }else{
                return ResponseEntity.notFound().build();
            }

        }catch (Exception e){
            return ResponseEntity.notFound().build();
        }

    }

    // hàm lưu file xong thì trả về tên file
    private String storeFile(MultipartFile file) throws IOException {

        if(!isImageFile(file) || file.getOriginalFilename()== null ){
            throw new IOException("Invalid image format.");
        }
        // làm sạch đường dẫn file tránh và lấy tên file gốc
        String fileName = StringUtils.cleanPath(Objects.requireNonNull(file.getOriginalFilename()));
        //thêm UUID.randomUUID() vào trước tên file để đảm bảo trên file là duy nhất
        String uniqueFileName = UUID.randomUUID().toString() + "_" + fileName;
        // đường dẫn bạn đến thư mục bạn muốn lưu file
        Path uploadDir = Paths.get("uploads");
        // kiểm tra có thứ mục uploads không ,nếu không thì tạo mới thư mục
        if (!Files.exists(uploadDir)) {
            Files.createDirectories(uploadDir);
        }
        // tạo ra đường dẫn đầy đủ bằng cách kết hợp đường dẫn thư mục đích (uploadDir) và tên tệp duy nhất (uniqueFileName).
        Path destination = Paths.get(uploadDir.toString(), uniqueFileName);

        // sao chép dữ liệu từ luồng đầu vào của tệp tải lên (file.getInputStream()) vào đường dẫn đích (destination).
        // Nếu tệp đích đã tồn tại, nó sẽ bị thay thế bởi tệp mới.
        Files.copy(file.getInputStream(), destination, StandardCopyOption.REPLACE_EXISTING);
        return uniqueFileName;
    }

    @PostMapping(value = "/uploads/{productId}",
            consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<?> uploadFiles(
            @PathVariable("productId") Long productId,
            @Valid @ModelAttribute("file") List<MultipartFile> files
    ) {
        try{
            if(files.size()>ProductImage.MAXIMUM_IMAGES_PER_PRODUCTS){
                return ResponseEntity.badRequest().body("You can only upload maxium 5 image.");
            }
            Product existingProduct = productService.getProductById(productId);
            List<ProductImage> productImages = new ArrayList<>();
            // List<MultipartFile> files = productDTO.getFiles();
            // nếu không truyền file vào thì tạo ra mảng rỗng ,ngược lại thì lấy files cũ
            files = files == null ? new ArrayList<MultipartFile>() : files;



            for (MultipartFile file : files) {

                //nếu files bằng "" thì bỏ qua
                if (file.getSize() == 0) {
                    continue;
                }
                // kiểm tra kích thước file và định dạng
                if (file.getSize() > 10 * 1024 * 1024) {//kichs thước
                    return ResponseEntity.status(HttpStatus.PAYLOAD_TOO_LARGE)
                            .body("File is too large, Maximum of file is 10M.");

                }
                // kiểm tra nó có phải là định dạng file ảnh không
                String contentType = file.getContentType();
                if (contentType == null || !contentType.startsWith("image/")) {
                    return ResponseEntity.status(HttpStatus.UNSUPPORTED_MEDIA_TYPE)
                            .body("File must be an image.");
                }
                // lưu file và cập nhật vào thumbnail
                //String fileName = storeFile(file);
                //-> sẽ lưu file vào đối tượng trong db

                ProductImage productImage = productService.createProductImage(productId,
                        ProductImageDTO
                                .builder()
                                .productId(productId)
                                .imageUrl(storeFile(file))
                                .build()
                );

                productImages.add(productImage);

            }
            return ResponseEntity.ok(productImages);

        }catch (Exception e){
            return ResponseEntity.badRequest().body(e.getMessage());
        }

    }

    @PutMapping("/{id}")
    public ResponseEntity<?> updateProduct(@PathVariable Long id,
                                           @Valid @RequestBody ProductDTO productDTO) {

//        try{
//
//        }catch (Exception e){
//            return ResponseEntity.badRequest().body(e.getMessage());
//        }

        return ResponseEntity.ok("Update success." + id);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<?> deleteProduct(@PathVariable Long id) {


        try {
            productService.deleteProduct(id);
            return ResponseEntity.ok("Delete success with productId: " + id);

        } catch (Exception e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }
    //@PostMapping("/generateFakeProducts")
    private ResponseEntity<?> generateFakeProducts(){

            try {

                Faker faker = new Faker();
                for (int i =0;i<1000;i++) {
                    String productName = faker.commerce().productName();
                    if (productService.existsByName(productName)) {
                        continue;
                    }
                    ProductDTO productDTO = ProductDTO
                            .builder()
                            .name(productName)
                            .price((float) faker.number().numberBetween(10, 90_000_000))
                            .description(faker.lorem().sentence())
                            .thumbnail("")
                            .categoryId((long) faker.number().numberBetween(3, 5))
                            .build();
                    productService.createProduct(productDTO);
                }
            } catch (DataNotFindException e) {
                return ResponseEntity.badRequest().body("Fake product create failed.");
            }


        return ResponseEntity.ok("Fake products created successfully.");
    }
}
