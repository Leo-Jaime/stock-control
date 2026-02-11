package com.stockcontrol.dto;

import java.math.BigDecimal;

import com.stockcontrol.entity.Product;

public class ProductRequest {
    public String code;
    public String name;
    public BigDecimal value;

    public Product toEntity() {
        Product product = new Product();
        product.code = this.code;
        product.name = this.name;
        product.value = this.value;
        return product;
    }
}
