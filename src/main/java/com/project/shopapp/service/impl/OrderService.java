package com.project.shopapp.service.impl;

import com.project.shopapp.dtos.OrderDTO;
import com.project.shopapp.exception.DataNotFindException;
import com.project.shopapp.models.Order;
import com.project.shopapp.models.OrderStatus;
import com.project.shopapp.models.User;
import com.project.shopapp.repositories.OrderRepository;
import com.project.shopapp.repositories.UserRepository;
import com.project.shopapp.responses.OrderResponse;
import com.project.shopapp.service.IOrderService;
import lombok.RequiredArgsConstructor;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.Date;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service

public class OrderService implements IOrderService {


    @Autowired
    OrderRepository orderRepository;

    @Autowired
    UserRepository userRepository;

    @Autowired
    ModelMapper modelMapper;


    @Override
    public OrderResponse createOrder(OrderDTO orderDTO) throws Exception {
        User user = userRepository.findById(orderDTO.getUserId())
                .orElseThrow(()-> new DataNotFindException("Can't find user with user id"+ orderDTO.getUserId()));
        //convert orderDTO sang order
        // dùng thư viện model mapper
        // tạo mọt luồng bằng ánh xạ riêng để kểm soát việt ánh xạ
        modelMapper.typeMap(OrderDTO.class, Order.class)
                .addMappings(mapper->mapper.skip(Order::setId));
        //câp nhật các trường của đơn hàng từ orderDTO
        Order order = new Order();
        modelMapper.map(orderDTO,order);
        order.setUser(user);
        order.setOrderDate(LocalDateTime.now());
        order.setStatus(OrderStatus.PENDING);
        //kiểm tra shipping date phải lớn hơn ngày hôm nay
        LocalDate shipingDate = orderDTO.getShippingDate() == null
                ? LocalDate.now() : orderDTO.getShippingDate();
        order.setShippingDate(shipingDate);
        order.setActive(true);
        orderRepository.save(order);

        // Chuyển đổi từ Order sang OrderResponse để trả về
        OrderResponse orderResponse = modelMapper.map(order, OrderResponse.class);

        return orderResponse;
    }

    @Override
    public OrderResponse updateOrder(Long id, OrderDTO orderDTO) throws DataNotFindException {

        Order order = orderRepository.findById(id)
                .orElseThrow(()->new DataNotFindException("Can't find order with id"));

        User user = userRepository.findById(orderDTO.getUserId())
                .orElseThrow(()->new DataNotFindException("Can't find user with id"+orderDTO.getUserId()));

        modelMapper.typeMap(OrderDTO.class,Order.class)
                .addMappings(mapper -> mapper.skip(Order::setId));


              modelMapper.map(orderDTO,order);
              order.setUser(user);



        return modelMapper.map(orderRepository.save(order),OrderResponse.class);

    }

    @Override
    public OrderResponse getByOrderId(Long id) throws DataNotFindException {
        return orderRepository.findById(id)
                .map(order -> modelMapper.map(order, OrderResponse.class))
                .orElseThrow(() -> new DataNotFindException("Order not found with id: " + id));


    }

    @Override
    public void deleteOrder(Long id) throws DataNotFindException {

        Order order = orderRepository.findById(id).orElseThrow(
                ()-> new DataNotFindException("Can't find order with id:"+id));

        order.setActive(false);
        orderRepository.save(order);


    }

    @Override
    public List<OrderResponse> findByUserId(Long userId) throws Exception {
        User user = userRepository.findById(userId)
                .orElseThrow(()->new DataNotFindException("Can't find user."));

        List<OrderResponse> orders = orderRepository.findByUserId(userId).stream()
                .map(order -> modelMapper.map(order,OrderResponse.class))
                .collect(Collectors.toList());


        return orders;
    }
}
