package com.stockcontrol.dto;

import com.stockcontrol.entity.RawMaterial;

public class RawMaterialRequest {
    public String code;
    public String name;
    public Integer stockQuantity;

    public RawMaterial toEntity() {
        RawMaterial rawMaterial = new RawMaterial();
        rawMaterial.code = this.code;
        rawMaterial.name = this.name;
        rawMaterial.stockQuantity = this.stockQuantity;
        return rawMaterial;
    }
}
