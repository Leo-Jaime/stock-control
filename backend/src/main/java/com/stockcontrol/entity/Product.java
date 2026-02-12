package com.stockcontrol.entity;

import java.math.BigDecimal;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.CascadeType;
import jakarta.persistence.OneToMany;
import jakarta.persistence.Table;
import io.quarkus.hibernate.orm.panache.PanacheEntity;

import java.util.List;

import com.fasterxml.jackson.annotation.JsonIgnore;


@Entity
@Table(name = "products")
public class Product extends PanacheEntity {

    @Column(nullable = false, unique = true)
    public String code;

    @Column(nullable = false)
    public String name;

    @Column(nullable = false, name = "price")
    public BigDecimal value;

    @OneToMany(mappedBy = "product", cascade = CascadeType.ALL, orphanRemoval = true)
    @JsonIgnore
    public List<ProductRawMaterial> rawMaterials;
}
