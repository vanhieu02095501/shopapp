package com.project.shopapp.service;

import com.project.shopapp.dtos.OrderDTO;
import com.project.shopapp.exception.DataNotFindException;
import com.project.shopapp.responses.OrderResponse;

import java.util.List;

public interface IOrderService {

    OrderResponse createOrder(OrderDTO orderDTO) throws DataNotFindException, Exception;
    OrderResponse updateOrder(Long id ,OrderDTO orderDTO) throws DataNotFindException;

    OrderResponse getByOrderId(Long id) throws DataNotFindException;

    void deleteOrder(Long id ) throws DataNotFindException;

    List<OrderResponse> findByUserId(Long userId) throws  Exception;
}
