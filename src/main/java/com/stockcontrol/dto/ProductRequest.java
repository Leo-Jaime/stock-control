package com.stockcontrol.dto;

import java.math.BigDecimal;

import com.stockcontrol.entity.Product;
import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

public class ProductRequest {

    @NotBlank(message = "Código do produto é obrigatório")
    public String code;

    @NotBlank(message = "Nome do produto é obrigatório")
    public String name;

    @NotNull(message = "Valor do produto é obrigatório")
    @DecimalMin(value = "0.01", message = "O valor do produto deve ser maior que zero")
    public BigDecimal value;

    public Product toEntity() {
        Product product = new Product();
        product.code = this.code;
        product.name = this.name;
        product.value = this.value;
        return product;
    }
}
