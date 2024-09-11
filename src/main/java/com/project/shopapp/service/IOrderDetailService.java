package com.project.shopapp.service;

import com.project.shopapp.dtos.OrderDetailDTO;
import com.project.shopapp.exception.DataNotFindException;
import com.project.shopapp.models.OrderDetail;
import com.project.shopapp.responses.OrderDetailResponse;

import java.util.List;

public interface IOrderDetailService {

    OrderDetailResponse createOrderDetail(OrderDetailDTO orderDetailDTO) throws Exception;
    OrderDetailResponse getOrderDetail(Long id) throws Exception;
    OrderDetailResponse updateOrderDetail(Long id , OrderDetailDTO orderDetailDTO) throws DataNotFindException, Exception;

    void deleteOrderDetail(Long id);
    List<OrderDetailResponse> getOrderDetails(Long OrderId);

}
