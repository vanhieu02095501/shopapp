package com.project.shopapp.responses;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class OrderDetailResponse extends BaseResponse{

    @JsonProperty("order_id")
    private Long orderId;


    @JsonProperty("product_id")
    private Long productId;


    private Float price;


    @JsonProperty("number_of_products")
    private int numberOfProducts;


    @JsonProperty("total_money")
    private Float totalMoney;

    private String color;
}
