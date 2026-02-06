package com.example.domain;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;


import io.quarkus.hibernate.orm.panache.PanacheEntity;

@Entity
@Table(name = "product_raw_materials")
public class ProductRawMaterial extends PanacheEntity {

    @ManyToOne(optional = false)
    @JoinColumn(name = "product_id")
    public Product product;

    @ManyToOne(optional = false)
    @JoinColumn(name = "raw_material_id")
    public RawMaterial rawMaterial;

    @Column(nullable = false)
    public Integer requiredQuantity;
}
