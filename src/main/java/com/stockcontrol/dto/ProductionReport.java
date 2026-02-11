package com.stockcontrol.dto;

import java.math.BigDecimal;
import java.util.List;

public class ProductionReport {
    public List<ProductionSuggestion> suggestions;
    public BigDecimal totalProductionValue;
    public Integer totalProducts;

    public ProductionReport() {
    }

    public ProductionReport(List<ProductionSuggestion> suggestions) {
        this.suggestions = suggestions;
        this.totalProducts = suggestions.size();
        this.totalProductionValue = suggestions.stream()
            .map(s -> s.totalValue)
            .reduce(BigDecimal.ZERO, BigDecimal::add);
    }
}