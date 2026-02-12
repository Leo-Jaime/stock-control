package com.stockcontrol.service;

import com.stockcontrol.dto.RawMaterialRequest;
import com.stockcontrol.dto.RawMaterialResponse;
import com.stockcontrol.entity.ProductRawMaterial;
import com.stockcontrol.entity.RawMaterial;
import com.stockcontrol.exception.ConflictException;
import com.stockcontrol.exception.NotFoundException;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.transaction.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@ApplicationScoped
public class RawMaterialService {

    public List<RawMaterialResponse> findAll() {
        List<RawMaterial> rawMaterials = RawMaterial.listAll();
        return rawMaterials.stream()
            .map(RawMaterialResponse::fromEntity)
            .collect(Collectors.toList());
    }

    public RawMaterialResponse findById(Long id) {
        RawMaterial rawMaterial = RawMaterial.findById(id);
        if (rawMaterial == null) {
            throw new NotFoundException("Matéria-prima não encontrada com o id: " + id);
        }
        return RawMaterialResponse.fromEntity(rawMaterial);
    }

    @Transactional
    public RawMaterialResponse create(RawMaterialRequest request) {
        RawMaterial existingRawMaterial = RawMaterial.find("code", request.code).firstResult();
        if (existingRawMaterial != null) {
            throw new ConflictException("Matéria-prima com código '" + request.code + "' já existe!");
        }

        RawMaterial rawMaterial = request.toEntity();
        rawMaterial.persist();

        return RawMaterialResponse.fromEntity(rawMaterial);
    }

    @Transactional
    public RawMaterialResponse update(Long id, RawMaterialRequest request) {
        RawMaterial rawMaterial = RawMaterial.findById(id);
        if (rawMaterial == null) {
            throw new NotFoundException("Matéria-prima não encontrada para o id informado: " + id);
        }

        if (!rawMaterial.code.equals(request.code)) {
            RawMaterial existingRawMaterial = RawMaterial.find("code", request.code).firstResult();
            if (existingRawMaterial != null) {
                throw new ConflictException("Já existe uma matéria-prima cadastrada com o código '" + request.code + "'");
            }
        }

        rawMaterial.code = request.code;
        rawMaterial.name = request.name;
        rawMaterial.stockQuantity = request.stockQuantity;
        rawMaterial.persist();

        return RawMaterialResponse.fromEntity(rawMaterial);
    }

    @Transactional
    public void delete(Long id) {
        RawMaterial rawMaterial = RawMaterial.findById(id);
        if (rawMaterial == null) {
            throw new NotFoundException("Matéria-prima não encontrada com o id: " + id);
        }

        // verificar se esta sendo usada
        long count = ProductRawMaterial.count("rawMaterial.id", id);
        if (count > 0){
            throw new ConflictException(
                "Não é possivel deletar. Esta matéria-prima está associada a " + count + "produtos(s). Remova as associações primeiro."
            );
        }
        rawMaterial.delete();
    }
}

