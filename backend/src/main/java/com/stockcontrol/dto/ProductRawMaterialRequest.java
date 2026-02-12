package com.stockcontrol.dto;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;

public class ProductRawMaterialRequest {

    @NotNull(message = "ID da matéria prima é obrigatório")
    public Long rawMaterialId;

    @NotNull(message = "Quantidade necessária é obrigatória")
    @Min(value = 1, message = "Quantidade necessária deve ser maior que zero")
    public Integer requiredQuantity;
}
