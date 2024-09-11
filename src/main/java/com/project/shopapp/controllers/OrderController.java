package com.project.shopapp.controllers;

import com.project.shopapp.dtos.OrderDTO;
import com.project.shopapp.exception.DataNotFindException;
import com.project.shopapp.responses.OrderResponse;
import com.project.shopapp.service.IOrderService;
import com.project.shopapp.service.impl.OrderService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("${api.prefix}/orders")
public class OrderController {

    @Autowired
    IOrderService orderService;

    @PostMapping("")
    public ResponseEntity<?> createOrder(@Valid @RequestBody OrderDTO orderDTO,
                                       BindingResult result){

         try{
             if(result.hasErrors()){
                 List<String> messageErrors = result.getFieldErrors()
                         .stream()
                         .map(fieldError -> fieldError.getDefaultMessage())
                         .toList();
                 return ResponseEntity.badRequest().body("Create Order failed");
             }
             OrderResponse orderResponse = orderService.createOrder(orderDTO);

             return ResponseEntity.ok(orderResponse);
         }catch (Exception e){
             return ResponseEntity.badRequest().body(e.getMessage());
         }

    }

    @GetMapping("users/{user_id}")
    public ResponseEntity<?> getOrders(@Valid @PathVariable("user_id") Long userId ){

        try {
            List<OrderResponse> orderResponses =  orderService.findByUserId(userId);

            return ResponseEntity.ok(orderResponses);
        }catch (Exception e){

            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }

    @GetMapping("/{id}") //lấy ra chi tiết của một order
    public ResponseEntity<?> getOrder(@Valid @PathVariable("id") Long id ){

        try {
            OrderResponse orderResponse =  orderService.getByOrderId(id);
            return ResponseEntity.ok(orderResponse);
        }catch (Exception e){

            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }

    @PutMapping("/{id}")
    //công việc của admin
    public ResponseEntity<?> updateOrder(
                    @Valid @PathVariable Long id,
                    @Valid @RequestBody OrderDTO orderDTO
    ){
        try {
            OrderResponse orderResponse = orderService.updateOrder(id,orderDTO);
            return ResponseEntity.ok(orderResponse);

        }catch (Exception e){
            return ResponseEntity.badRequest().body(e.getMessage());
        }



    }

    @DeleteMapping("/{id}")
    //xóa mềm => cập nhật lại field is_active = false
    public ResponseEntity<?> deleteOrder(@Valid @PathVariable Long id ) throws DataNotFindException {

        orderService.deleteOrder(id);

        return ResponseEntity.ok("Deleted order success");
    }

}
