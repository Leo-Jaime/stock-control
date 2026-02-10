package com.stockcontrol.resource;

import com.stockcontrol.entity.Product;
import com.stockcontrol.entity.ProductRawMaterial;
import com.stockcontrol.entity.RawMaterial;
import jakarta.transaction.Transactional;
import jakarta.ws.rs.*;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;

import java.util.List;

@Path("/products/{productId}/raw-materials")
@Produces(MediaType.APPLICATION_JSON)
@Consumes(MediaType.APPLICATION_JSON)
public class ProductRawMaterialResource {

    @GET
    public Response listRawMaterials(@PathParam("productId") Long productId) {
        try {
            Product product = Product.findById(productId);
            
            if (product == null) {
                return Response.status(Response.Status.NOT_FOUND)
                    .entity("Produto não encontrado com id: " + productId)
                    .build();
            }

            List<ProductRawMaterial> rawMaterials = ProductRawMaterial.list("product.id", productId);
            return Response.ok(rawMaterials).build();
            
        } catch (Exception e) {
            return Response.status(Response.Status.INTERNAL_SERVER_ERROR)
                .entity("Erro ao buscar matérias primas: " + e.getMessage())
                .build();
        }
    }

    @POST
    @Transactional
    public Response addRawMaterial(
            @PathParam("productId") Long productId,
            ProductRawMaterialRequest request) {
        try {
            // Validação: request não pode ser nulo
            if (request == null) {
                return Response.status(Response.Status.BAD_REQUEST)
                    .entity("Dados da requisição são obrigatórios")
                    .build();
            }

            // Validação: rawMaterialId obrigatório
            if (request.rawMaterialId == null) {
                return Response.status(Response.Status.BAD_REQUEST)
                    .entity("ID da matéria prima é obrigatório")
                    .build();
            }

            // Validação: requiredQuantity obrigatório
            if (request.requiredQuantity == null || request.requiredQuantity <= 0) {
                return Response.status(Response.Status.BAD_REQUEST)
                    .entity("Quantidade necessária deve ser maior que zero")
                    .build();
            }

       
            Product product = Product.findById(productId);
            if (product == null) {
                return Response.status(Response.Status.NOT_FOUND)
                    .entity("Produto não encontrado com id: " + productId)
                    .build();
            }

            RawMaterial rawMaterial = RawMaterial.findById(request.rawMaterialId);
            if (rawMaterial == null) {
                return Response.status(Response.Status.NOT_FOUND)
                    .entity("Matéria prima não encontrada com id: " + request.rawMaterialId)
                    .build();
            }

            // Verifica se já existe essa associação
            ProductRawMaterial existing = ProductRawMaterial.find(
                "product.id = ?1 and rawMaterial.id = ?2", 
                productId, 
                request.rawMaterialId
            ).firstResult();

            if (existing != null) {
                return Response.status(Response.Status.CONFLICT)
                    .entity("Esta matéria prima já está associada a este produto")
                    .build();
            }

            // Cria a associação
            ProductRawMaterial productRawMaterial = new ProductRawMaterial();
            productRawMaterial.product = product;
            productRawMaterial.rawMaterial = rawMaterial;
            productRawMaterial.requiredQuantity = request.requiredQuantity;
            productRawMaterial.persist();

            return Response.status(Response.Status.CREATED)
                .entity(productRawMaterial)
                .build();
                
        } catch (Exception e) {
            return Response.status(Response.Status.INTERNAL_SERVER_ERROR)
                .entity("Erro ao adicionar matéria prima: " + e.getMessage())
                .build();
        }
    }

    @DELETE
    @Path("/{associationId}")
    @Transactional
    public Response removeRawMaterial(
            @PathParam("productId") Long productId,
            @PathParam("associationId") Long associationId) {
        try {
            ProductRawMaterial association = ProductRawMaterial.findById(associationId);
            
            if (association == null) {
                return Response.status(Response.Status.NOT_FOUND)
                    .entity("Associação não encontrada com id: " + associationId)
                    .build();
            }

            if (!association.product.id.equals(productId)) {
                return Response.status(Response.Status.BAD_REQUEST)
                    .entity("Associação não pertence a este produto")
                    .build();
            }

            association.delete();
            return Response.noContent().build();
            
        } catch (Exception e) {
            return Response.status(Response.Status.INTERNAL_SERVER_ERROR)
                .entity("Erro ao remover matéria prima: " + e.getMessage())
                .build();
        }
    }

    // Classe auxiliar para receber dados do POST
    public static class ProductRawMaterialRequest {
        public Long rawMaterialId;
        public Integer requiredQuantity;
    }
}