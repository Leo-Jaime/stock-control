package com.stockcontrol.dto;

import java.math.BigDecimal;

import com.stockcontrol.entity.Product;

public class ProductResponse {
    public Long id;
    public String code;
    public String name;
    public BigDecimal value;

    public static ProductResponse fromEntity(Product product) {
        ProductResponse response = new ProductResponse();
        response.id = product.id;
        response.code = product.code;
        response.name = product.name;
        response.value = product.value;
        return response;
    }
}
