package com.stockcontrol.dto;

import com.stockcontrol.entity.RawMaterial;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

public class RawMaterialRequest {

    @NotBlank(message = "Código do material é obrigatório")
    public String code;

    @NotBlank(message = "Nome do material é obrigatório")
    public String name;

    @NotNull(message = "Quantidade em estoque é obrigatória")
    @Min(value = 0, message = "A quantidade em estoque deve ser zero ou maior")
    public Integer stockQuantity;

    public RawMaterial toEntity() {
        RawMaterial rawMaterial = new RawMaterial();
        rawMaterial.code = this.code;
        rawMaterial.name = this.name;
        rawMaterial.stockQuantity = this.stockQuantity;
        return rawMaterial;
    }
}
