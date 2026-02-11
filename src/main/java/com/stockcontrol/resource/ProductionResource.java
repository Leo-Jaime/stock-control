package com.stockcontrol.resource;

import com.stockcontrol.dto.ProductionReport;
import com.stockcontrol.dto.ProductionSuggestion;
import com.stockcontrol.entity.Product;
import com.stockcontrol.entity.ProductRawMaterial;
import com.stockcontrol.entity.RawMaterial;
import jakarta.ws.rs.GET;
import jakarta.ws.rs.Path;
import jakarta.ws.rs.Produces;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;

@Path("/production")
@Produces(MediaType.APPLICATION_JSON)
public class ProductionResource {

    @GET
    @Path("/calculate")
    public Response calculateProduction() {
        // 1. Buscar todos os produtos ordenados por valor (maior para menor)
        List<Product> products = Product.listAll();
        products.sort(Comparator.comparing((Product p) -> p.value).reversed());

        List<ProductionSuggestion> suggestions = new ArrayList<>();

        // 2. Para cada produto, calcular quantas unidades podem ser produzidas
        for (Product product : products) {
            // Buscar matérias-primas necessárias para este produto
            List<ProductRawMaterial> requiredMaterials = ProductRawMaterial.list("product.id", product.id);

            // Se o produto não tem matérias-primas associadas, pula
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

        ProductionReport report = new ProductionReport(suggestions);
        return Response.ok(report).build();
    }

    /**
     * Calcula a quantidade maxima de produtos que podem ser produzidos
     * baseado no estoque disponível de matérias-primas
     */
    private Integer calculateMaxQuantity(List<ProductRawMaterial> requiredMaterials) {
        Integer minQuantity = Integer.MAX_VALUE;

        for (ProductRawMaterial material : requiredMaterials) {
            RawMaterial rawMaterial = material.rawMaterial;
            Integer requiredQuantity = material.requiredQuantity;

            // Calcula quantas unidades do produto podem ser feitas com esta matéria-prima
            Integer possibleQuantity = rawMaterial.stockQuantity / requiredQuantity;

            // O limitante é a matéria-prima que permite produzir MENOS unidades
            if (possibleQuantity < minQuantity) {
                minQuantity = possibleQuantity;
            }
        }

        return minQuantity;
    }
}