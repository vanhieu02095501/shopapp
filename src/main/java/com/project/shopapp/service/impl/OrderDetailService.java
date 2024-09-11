package com.project.shopapp.service.impl;

import com.project.shopapp.dtos.OrderDetailDTO;
import com.project.shopapp.exception.DataNotFindException;
import com.project.shopapp.models.Order;
import com.project.shopapp.models.OrderDetail;
import com.project.shopapp.models.Product;
import com.project.shopapp.repositories.OrderDetailRepository;
import com.project.shopapp.repositories.OrderRepository;
import com.project.shopapp.repositories.ProductRepository;
import com.project.shopapp.responses.OrderDetailResponse;
import com.project.shopapp.responses.OrderResponse;
import com.project.shopapp.service.IOrderDetailService;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class OrderDetailService implements IOrderDetailService {

    @Autowired
    OrderDetailRepository orderDetailRepository;

    @Autowired
    OrderRepository orderRepository;

    @Autowired
    ProductRepository productRepository;

    @Autowired
    ModelMapper modelMapper;

    @Override
    public OrderDetailResponse createOrderDetail(OrderDetailDTO orderDetailDTO) throws Exception {

        Order existingOrder = orderRepository.findById(orderDetailDTO.getOrderId())
                .orElseThrow(()->new DataNotFindException("Can't find Order with id:"+orderDetailDTO.getOrderId()));

        Product existingProduct = productRepository.findById(orderDetailDTO.getProductId())
                .orElseThrow(()->new DataNotFindException("Can't find Product with id "+orderDetailDTO.getProductId()));

        OrderDetail orderDetail = OrderDetail.builder()
                .order(existingOrder)
                .product(existingProduct)
                .price(orderDetailDTO.getPrice())
                .numberOfProducts(orderDetailDTO.getNumberOfProducts())
                .color(orderDetailDTO.getColor())
                .totalMoney(orderDetailDTO.getTotalMoney())
                .build();



        OrderDetail orderDetail1 = orderDetailRepository.save(orderDetail);

        OrderDetailResponse orderDetailResponse = modelMapper.map(orderDetail1,OrderDetailResponse.class);
        orderDetailResponse.setOrderId(orderDetail1.getOrder().getId());
        orderDetailResponse.setProductId(orderDetail1.getProduct().getId());



        return  modelMapper.map(orderDetail1,OrderDetailResponse.class);

    }

    @Override
    public OrderDetailResponse getOrderDetail(Long id) throws Exception{
        return orderDetailRepository.findById(id).map(orderDetail -> modelMapper.map(orderDetail,OrderDetailResponse.class))
                .orElseThrow(()->new DataNotFindException("Can't find OrderDetail with id"+id));

    }

    @Override
    public OrderDetailResponse updateOrderDetail(Long id, OrderDetailDTO orderDetailDTO) throws Exception {

        OrderDetail orderDetail = orderDetailRepository.findById(id)
                .orElseThrow(()->new DataNotFindException("Can't find OrderDetail with id "+id));
        Order order = orderRepository.findById(orderDetailDTO.getOrderId())
                .orElseThrow(()-> new DataNotFindException("Can't find order with id :"+ orderDetailDTO.getOrderId()));

        Product product = productRepository.findById(orderDetailDTO.getProductId())
                .orElseThrow(()->new DataNotFindException("Can't find product with id "+orderDetailDTO.getProductId()));

        orderDetail.setOrder(order);
        orderDetail.setProduct(product);
        orderDetail.setPrice(orderDetailDTO.getPrice());
        orderDetail.setColor(orderDetailDTO.getColor());
        orderDetail.setTotalMoney(orderDetailDTO.getTotalMoney());
        orderDetail.setNumberOfProducts(orderDetailDTO.getNumberOfProducts());

// Lưu đối tượng OrderDetail đã được cập nhật
        orderDetailRepository.save(orderDetail);

        OrderDetailResponse orderDetailResponse = new OrderDetailResponse();
        modelMapper.map(orderDetail,orderDetailResponse);
        orderDetailResponse.setOrderId(orderDetail.getOrder().getId());
        orderDetailResponse.setProductId(orderDetail.getProduct().getId());



        return orderDetailResponse;
    }

    @Override
    public void deleteOrderDetail(Long id) {
        orderDetailRepository.deleteById(id);

    }

    @Override
    public List<OrderDetailResponse> getOrderDetails(Long orderId) {

      List<OrderDetail> orderDetails = orderDetailRepository.findByOrderId(orderId);

        return orderDetails.stream()
              .map(orderDetail -> modelMapper.map(orderDetail,OrderDetailResponse.class))
              .toList();
    }
}
