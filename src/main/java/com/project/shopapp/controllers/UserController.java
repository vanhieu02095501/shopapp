package com.project.shopapp.controllers;

import com.project.shopapp.dtos.UserDTO;
import com.project.shopapp.dtos.UserLoginDTO;
import com.project.shopapp.models.User;
import com.project.shopapp.responses.LoginRespone;
import com.project.shopapp.service.impl.UserService;
import com.project.shopapp.components.LocalizationUtils;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("${api.prefix}/users")
public class UserController {

    @Autowired
    UserService userService;



    @Autowired
    LocalizationUtils localizationUtils;

    @PostMapping("/register")
    public ResponseEntity<?> register(@Valid @RequestBody UserDTO userDTO,
                                      BindingResult result) {
        try {
            if (result.hasErrors()) {
                List<String> messageErrors = result.getFieldErrors()
                        .stream()
                        .map(fieldError -> fieldError.getDefaultMessage())
                        .toList();
                return ResponseEntity.badRequest().body(messageErrors);
            }
            if (!userDTO.getPassword().equals(userDTO.getRetypePassword())) {
                return ResponseEntity.badRequest().body("Password doesn't match.");
            }
             User user =userService.createUser(userDTO);

            return ResponseEntity.ok(user);
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }

    }

    @PostMapping("/login")
    public ResponseEntity<LoginRespone> login(@Valid @RequestBody UserLoginDTO userLoginDTO

    ) {
        try {

            //kiểm tra thông tin đăng nhập và sinh token
            String token = userService.login(userLoginDTO.getPhoneNumber(),userLoginDTO.getPassword());


            return ResponseEntity.ok(LoginRespone.builder()
                    .token(token)
                    .message(localizationUtils.getLocalizedMessage("user.login.login_successfully"))
                    .build());

        } catch (Exception e) {
            return ResponseEntity.badRequest().body(
                    LoginRespone.builder()
                            .message(e.getMessage())
                            .build()
            );
        }
    }

}
