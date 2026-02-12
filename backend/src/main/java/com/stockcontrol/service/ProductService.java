package com.stockcontrol.service;

import com.stockcontrol.dto.ProductRequest;
import com.stockcontrol.dto.ProductResponse;
import com.stockcontrol.entity.Product;
import com.stockcontrol.exception.ConflictException;
import com.stockcontrol.exception.NotFoundException;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.transaction.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@ApplicationScoped
public class ProductService {

    public List<ProductResponse> findAll() {
        List<Product> products = Product.listAll();
        return products.stream()
            .map(ProductResponse::fromEntity)
            .collect(Collectors.toList());
    }

    public ProductResponse findById(Long id) {
        Product product = Product.findById(id);
        if (product == null) {
            throw new NotFoundException("Produto não encontrado com id: " + id);
        }
        return ProductResponse.fromEntity(product);
    }

    @Transactional
    public ProductResponse create(ProductRequest request) {
        Product existingProduct = Product.find("code", request.code).firstResult();
        if (existingProduct != null) {
            throw new ConflictException("Produto com o código '" + request.code + "' já existe");
        }

        Product product = request.toEntity();
        product.persist();

        return ProductResponse.fromEntity(product);
    }

    @Transactional
    public ProductResponse update(Long id, ProductRequest request) {
        Product product = Product.findById(id);
        if (product == null) {
            throw new NotFoundException("Produto não encontrado com id: " + id);
        }

        if (!product.code.equals(request.code)) {
            Product existingProduct = Product.find("code", request.code).firstResult();
            if (existingProduct != null) {
                throw new ConflictException("Produto com o código: '" + request.code + "' já existe");
            }
        }

        product.code = request.code;
        product.name = request.name;
        product.value = request.value;
        product.persist();

        return ProductResponse.fromEntity(product);
    }

    @Transactional
    public void delete(Long id) {
        boolean deleted = Product.deleteById(id);
        if (!deleted) {
            throw new NotFoundException("Produto não encontrado com id: " + id);
        }
        // deleta as associações primeiro 
        ProductRawMaterial.delete("product.id", id);
        
        //depois deleta o produto
        product.delete();
    }
}
