package com.stockcontrol.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.OneToMany;
import jakarta.persistence.Table;
import io.quarkus.hibernate.orm.panache.PanacheEntity;

import java.util.List;

import com.fasterxml.jackson.annotation.JsonIgnore;


@Entity
@Table(name = "raw_materials")
public class RawMaterial extends PanacheEntity {

    @Column(nullable = false, unique = true)
    public String code;

    @Column(nullable = false)
    public String name;

    @Column(nullable = false)
    public Integer stockQuantity;

    @OneToMany(mappedBy = "rawMaterial")
    @JsonIgnore
    public List<ProductRawMaterial> products;
}
