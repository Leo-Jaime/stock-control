package com.stockcontrol.service;

import com.stockcontrol.dto.ProductionReport;
import com.stockcontrol.dto.ProductionSuggestion;
import com.stockcontrol.entity.Product;
import com.stockcontrol.entity.ProductRawMaterial;
import com.stockcontrol.entity.RawMaterial;
import jakarta.enterprise.context.ApplicationScoped;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;

@ApplicationScoped
@ApplicationScoped
public class ProductionService {

    public ProductionReport calculateProduction() {
        // 1. Buscar todos os produtos ordenados por valor (maior para menor)
        List<Product> products = Product.listAll();
        products.sort(Comparator.comparing((Product p) -> p.value).reversed());

        List<ProductionSuggestion> suggestions = new ArrayList<>();

        // 2. Para cada produto, calcular quantas unidades podem ser produzidas
        for (Product product : products) {
            List<ProductRawMaterial> requiredMaterials = ProductRawMaterial.list("product.id", product.id);

            // Se o produto não tem matérias-primas associadas, consideramos que não pode ser produzido
            if (requiredMaterials.isEmpty()) {
                continue;
            }

            // Calcular a quantidade máxima que pode ser produzida
            Integer maxQuantity = calculateMaxQuantity(requiredMaterials);
            
            // Se pode produzir pelo menos 1 unidade, adiciona na lista
            if (maxQuantity > 0) {
                ProductionSuggestion suggestion = new ProductionSuggestion(
                    product.id,
                    product.code,
                    product.name,
                    product.value,
                    maxQuantity
                );
                suggestions.add(suggestion);
            }
        }
        
        return new ProductionReport(suggestions);
    }

    /**
     * Calcula a quantidade maxima de produtos que podem ser produzidos
     * baseado no estoque disponível de matérias-primas
     */
    private Integer calculateMaxQuantity(List<ProductRawMaterial> requiredMaterials) {
        if (requiredMaterials == null || requiredMaterials.isEmpty()) {
            return 0;
        }

        Integer minQuantity = Integer.MAX_VALUE;

        for (ProductRawMaterial material : requiredMaterials) {
            RawMaterial rawMaterial = material.rawMaterial;
            
            RawMaterial freshRm = RawMaterial.findById(rawMaterial.id);
            if (freshRm != null) {
                rawMaterial = freshRm;
            }

            Integer stock = rawMaterial.stockQuantity != null ? rawMaterial.stockQuantity : 0;
            Integer required = material.requiredQuantity;

            if (required == null || required <= 0) {
                continue; 
            }

            Integer possibleQuantity = stock / required;
            
            if (possibleQuantity < minQuantity) {
                minQuantity = possibleQuantity;
            }
        }
        
        // Se minQuantity ainda for MAX_VALUE (nenhuma matéria válida processada), retorna 0
        if (minQuantity == Integer.MAX_VALUE) {
            return 0;
        }

        return minQuantity;
    }
}
