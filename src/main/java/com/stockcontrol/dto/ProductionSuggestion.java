package com.stockcontrol.dto;

import java.math.BigDecimal;

public class ProductionSuggestion {
    public Long productId;
    public String productCode;
    public String productName;
    public BigDecimal productValue;
    public Integer quantityCanProduce;
    public BigDecimal totalValue;

    public ProductionSuggestion() {
    }

    public ProductionSuggestion(Long productId, String productCode, String productName, BigDecimal productValue, Integer quantityCanProduce) {
        this.productId = productId;
        this.productCode = productCode;
        this.productName = productName;
        this.productValue = productValue;
        this.quantityCanProduce = quantityCanProduce;
        this.totalValue = productValue.multiply(BigDecimal.valueOf(quantityCanProduce));
    }
}