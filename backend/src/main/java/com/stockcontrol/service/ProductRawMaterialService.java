package com.stockcontrol.service;

import com.stockcontrol.dto.ProductRawMaterialRequest;
import com.stockcontrol.dto.ProductRawMaterialResponse;
import com.stockcontrol.entity.Product;
import com.stockcontrol.entity.ProductRawMaterial;
import com.stockcontrol.entity.RawMaterial;
import com.stockcontrol.exception.ConflictException;
import com.stockcontrol.exception.NotFoundException;
import com.stockcontrol.exception.ValidationException;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.transaction.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@ApplicationScoped
public class ProductRawMaterialService {

    public List<ProductRawMaterialResponse> listByProduct(Long productId) {
        Product product = Product.findById(productId);
        if (product == null) {
            throw new NotFoundException("Produto não encontrado com id: " + productId);
        }

        List<ProductRawMaterial> rawMaterials = ProductRawMaterial.list("product.id", productId);
        return rawMaterials.stream()
            .map(ProductRawMaterialResponse::fromEntity)
            .collect(Collectors.toList());
    }

    @Transactional
    public ProductRawMaterialResponse addRawMaterial(Long productId, ProductRawMaterialRequest request) {
        Product product = Product.findById(productId);
        if (product == null) {
            throw new NotFoundException("Produto não encontrado com id: " + productId);
        }

        RawMaterial rawMaterial = RawMaterial.findById(request.rawMaterialId);
        if (rawMaterial == null) {
            throw new NotFoundException("Matéria prima não encontrada com id: " + request.rawMaterialId);
        }

        ProductRawMaterial existing = ProductRawMaterial.find(
            "product.id = ?1 and rawMaterial.id = ?2",
            productId,
            request.rawMaterialId
        ).firstResult();

        if (existing != null) {
            throw new ConflictException("Esta matéria prima já está associada a este produto");
        }

        ProductRawMaterial productRawMaterial = new ProductRawMaterial();
        productRawMaterial.product = product;
        productRawMaterial.rawMaterial = rawMaterial;
        productRawMaterial.requiredQuantity = request.requiredQuantity;
        productRawMaterial.persist();

        return ProductRawMaterialResponse.fromEntity(productRawMaterial);
    }

    @Transactional
    public ProductRawMaterialResponse updateRawMaterial(Long productId, Long associationId, ProductRawMaterialRequest request) {
        ProductRawMaterial association = ProductRawMaterial.findById(associationId);
        if (association == null) {
            throw new NotFoundException("Associação não encontrada com id: " + associationId);
        }

        if (!association.product.id.equals(productId)) {
            throw new ValidationException("Associação não pertence a este produto");
        }

        association.requiredQuantity = request.requiredQuantity;
        association.persist();

        return ProductRawMaterialResponse.fromEntity(association);
    }

    @Transactional
    public void removeRawMaterial(Long productId, Long associationId) {
        ProductRawMaterial association = ProductRawMaterial.findById(associationId);
        if (association == null) {
            throw new NotFoundException("Associação não encontrada com id: " + associationId);
        }

        if (!association.product.id.equals(productId)) {
            throw new ValidationException("Associação não pertence a este produto");
        }

        association.delete();
    }
}
