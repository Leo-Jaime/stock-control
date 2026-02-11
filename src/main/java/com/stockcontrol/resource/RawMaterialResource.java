package com.stockcontrol.resource;

import com.stockcontrol.dto.RawMaterialRequest;
import com.stockcontrol.dto.RawMaterialResponse;
import com.stockcontrol.entity.RawMaterial;
import com.stockcontrol.exception.ConflictException;
import com.stockcontrol.exception.NotFoundException;
import com.stockcontrol.exception.ValidationException;
import jakarta.transaction.Transactional;
import jakarta.ws.rs.*;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;

import java.util.List;
import java.util.stream.Collectors;

@Path("/raw-materials")
@Produces(MediaType.APPLICATION_JSON)
@Consumes(MediaType.APPLICATION_JSON)
public class RawMaterialResource {

    @GET
    public Response findAll() {
        List<RawMaterial> rawMaterials = RawMaterial.listAll();
        List<RawMaterialResponse> response = rawMaterials.stream()
            .map(RawMaterialResponse::fromEntity)
            .collect(Collectors.toList());
        return Response.ok(response).build();
    }

    @GET
    @Path("/{id}")
    public Response findById(@PathParam("id") Long id) {
        RawMaterial rawMaterial = RawMaterial.findById(id);

        if (rawMaterial == null) {
            throw new NotFoundException("Matéria-prima não encontrada com o id: " + id);
        }

        return Response.ok(RawMaterialResponse.fromEntity(rawMaterial)).build();
    }

    @POST
    @Transactional
    public Response create(RawMaterialRequest request) {
        if (request == null) {
            throw new ValidationException("Os dados da matéria-prima são requeridos");
        }

        if (request.code == null || request.code.trim().isEmpty()) {
            throw new ValidationException("Código do material é requerido");
        }

        if (request.name == null || request.name.trim().isEmpty()) {
            throw new ValidationException("Nome do material é requerido");
        }

        if (request.stockQuantity == null || request.stockQuantity < 0) {
            throw new ValidationException("A quantidade em estoque deve ser zero ou maior.");
        }

        RawMaterial existingRawMaterial = RawMaterial.find("code", request.code).firstResult();
        if (existingRawMaterial != null) {
            throw new ConflictException("Matéria-prima com código '" + request.code + "' já existe!");
        }

        RawMaterial rawMaterial = request.toEntity();
        rawMaterial.persist();

        return Response.status(Response.Status.CREATED)
            .entity(RawMaterialResponse.fromEntity(rawMaterial))
            .build();
    }

    @PUT
    @Path("/{id}")
    @Transactional
    public Response update(@PathParam("id") Long id, RawMaterialRequest request) {
        if (request == null) {
            throw new ValidationException("Os dados da matéria-prima são obrigatórios.");
        }

        RawMaterial rawMaterial = RawMaterial.findById(id);
        if (rawMaterial == null) {
            throw new NotFoundException("Matéria-prima não encontrada para o id informado: " + id);
        }

        if (request.code == null || request.code.trim().isEmpty()) {
            throw new ValidationException("O código da matéria-prima é obrigatório.");
        }

        if (request.name == null || request.name.trim().isEmpty()) {
            throw new ValidationException("O nome da matéria-prima é obrigatório.");
        }

        if (request.stockQuantity == null || request.stockQuantity < 0) {
            throw new ValidationException("A quantidade em estoque deve ser zero ou maior.");
        }

        // Validação codigo duplicado
        if (!rawMaterial.code.equals(request.code)) {
            RawMaterial existingRawMaterial = RawMaterial.find("code", request.code).firstResult();
            if (existingRawMaterial != null) {
                throw new ConflictException("Já existe uma matéria-prima cadastrada com o código '" + request.code + "'");
            }
        }

        // Atualiza os dados
        rawMaterial.code = request.code;
        rawMaterial.name = request.name;
        rawMaterial.stockQuantity = request.stockQuantity;
        rawMaterial.persist();

        return Response.ok(RawMaterialResponse.fromEntity(rawMaterial)).build();
    }

    @DELETE
    @Path("/{id}")
    @Transactional
    public Response delete(@PathParam("id") Long id) {
        boolean deleted = RawMaterial.deleteById(id);

        if (!deleted) {
            throw new NotFoundException("Matéria-prima não encontrada com o id: " + id);
        }

        return Response.noContent().build();
    }
}