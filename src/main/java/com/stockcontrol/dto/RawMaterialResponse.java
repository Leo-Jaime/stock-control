package com.stockcontrol.dto;

import com.stockcontrol.entity.RawMaterial;

public class RawMaterialResponse {
    public Long id;
    public String code;
    public String name;
    public Integer stockQuantity;

    public static RawMaterialResponse fromEntity(RawMaterial rawMaterial) {
        RawMaterialResponse response = new RawMaterialResponse();
        response.id = rawMaterial.id;
        response.code = rawMaterial.code;
        response.name = rawMaterial.name;
        response.stockQuantity = rawMaterial.stockQuantity;
        return response;
    }
}
