package com.stockcontrol.resource;

import com.stockcontrol.dto.ProductRawMaterialRequest;
import com.stockcontrol.dto.ProductRawMaterialResponse;
import com.stockcontrol.entity.Product;
import com.stockcontrol.entity.ProductRawMaterial;
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

@Path("/products/{productId}/raw-materials")
@Produces(MediaType.APPLICATION_JSON)
@Consumes(MediaType.APPLICATION_JSON)
public class ProductRawMaterialResource {

    @GET
    public Response listRawMaterials(@PathParam("productId") Long productId) {
        Product product = Product.findById(productId);

        if (product == null) {
            throw new NotFoundException("Produto não encontrado com id: " + productId);
        }

        List<ProductRawMaterial> rawMaterials = ProductRawMaterial.list("product.id", productId);
        List<ProductRawMaterialResponse> response = rawMaterials.stream()
            .map(ProductRawMaterialResponse::fromEntity)
            .collect(Collectors.toList());

        return Response.ok(response).build();
    }

    @POST
    @Transactional
    public Response addRawMaterial(
            @PathParam("productId") Long productId,
            ProductRawMaterialRequest request) {

        // Validação: request não pode ser nulo
        if (request == null) {
            throw new ValidationException("Dados da requisição são obrigatórios");
        }

        // Validação: rawMaterialId obrigatório
        if (request.rawMaterialId == null) {
            throw new ValidationException("ID da matéria prima é obrigatório");
        }

        // Validação: requiredQuantity obrigatório
        if (request.requiredQuantity == null || request.requiredQuantity <= 0) {
            throw new ValidationException("Quantidade necessária deve ser maior que zero");
        }

        Product product = Product.findById(productId);
        if (product == null) {
            throw new NotFoundException("Produto não encontrado com id: " + productId);
        }

        RawMaterial rawMaterial = RawMaterial.findById(request.rawMaterialId);
        if (rawMaterial == null) {
            throw new NotFoundException("Matéria prima não encontrada com id: " + request.rawMaterialId);
        }

        // Verifica se já existe essa associação
        ProductRawMaterial existing = ProductRawMaterial.find(
            "product.id = ?1 and rawMaterial.id = ?2",
            productId,
            request.rawMaterialId
        ).firstResult();

        if (existing != null) {
            throw new ConflictException("Esta matéria prima já está associada a este produto");
        }

        // Cria a associação
        ProductRawMaterial productRawMaterial = new ProductRawMaterial();
        productRawMaterial.product = product;
        productRawMaterial.rawMaterial = rawMaterial;
        productRawMaterial.requiredQuantity = request.requiredQuantity;
        productRawMaterial.persist();

        return Response.status(Response.Status.CREATED)
            .entity(ProductRawMaterialResponse.fromEntity(productRawMaterial))
            .build();
    }

    @PUT
    @Path("/{associationId}")
    @Transactional
    public Response updateRawMaterial(
            @PathParam("productId") Long productId,
            @PathParam("associationId") Long associationId,
            ProductRawMaterialRequest request) {

        if (request == null) {
            throw new ValidationException("Dados da requisição são obrigatórios");
        }

        if (request.requiredQuantity == null || request.requiredQuantity <= 0) {
            throw new ValidationException("Quantidade necessária deve ser maior que zero");
        }

        ProductRawMaterial association = ProductRawMaterial.findById(associationId);

        if (association == null) {
            throw new NotFoundException("Associação não encontrada com id: " + associationId);
        }

        if (!association.product.id.equals(productId)) {
            throw new ValidationException("Associação não pertence a este produto");
        }

        association.requiredQuantity = request.requiredQuantity;
        association.persist();

        return Response.ok(ProductRawMaterialResponse.fromEntity(association)).build();
    }

    @DELETE
    @Path("/{associationId}")
    @Transactional
    public Response removeRawMaterial(
            @PathParam("productId") Long productId,
            @PathParam("associationId") Long associationId) {

        ProductRawMaterial association = ProductRawMaterial.findById(associationId);

        if (association == null) {
            throw new NotFoundException("Associação não encontrada com id: " + associationId);
        }

        if (!association.product.id.equals(productId)) {
            throw new ValidationException("Associação não pertence a este produto");
        }

        association.delete();
        return Response.noContent().build();
    }
}