package com.example.domain;

import java.math.BigDecimal;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.OneToMany;
import jakarta.persistence.Table;
import io.quarkus.hibernate.orm.panache.PanacheEntity;

import java.util.List;


@Entity
@Table(name = "products")
public class Product extends PanacheEntity {

    @Column(nullable = false, unique = true)
    public String code;

    @Column(nullable = false)
    public String name;

    @Column(nullable = false, name = "price")
    public BigDecimal value;

    @OneToMany(mappedBy = "product")
    public List<ProductRawMaterial> rawMaterials;
}
