package com.stockcontrol.dto;

import com.stockcontrol.entity.ProductRawMaterial;

public class ProductRawMaterialResponse {
    public Long id;
    public Long productId;
    public Long rawMaterialId;
    public String rawMaterialName;
    public String rawMaterialCode;
    public Integer requiredQuantity;

    public static ProductRawMaterialResponse fromEntity(ProductRawMaterial entity) {
        ProductRawMaterialResponse response = new ProductRawMaterialResponse();
        response.id = entity.id;
        response.productId = entity.product.id;
        response.rawMaterialId = entity.rawMaterial.id;
        response.rawMaterialName = entity.rawMaterial.name;
        response.rawMaterialCode = entity.rawMaterial.code;
        response.requiredQuantity = entity.requiredQuantity;
        return response;
    }
}
