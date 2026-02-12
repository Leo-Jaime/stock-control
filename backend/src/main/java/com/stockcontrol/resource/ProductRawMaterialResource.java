package com.stockcontrol.resource;

import com.stockcontrol.dto.ProductRawMaterialRequest;
import com.stockcontrol.dto.ProductRawMaterialResponse;
import com.stockcontrol.service.ProductRawMaterialService;
import jakarta.annotation.security.RolesAllowed;
import jakarta.inject.Inject;
import jakarta.validation.Valid;
import jakarta.ws.rs.*;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;

import java.util.List;

@Path("/products/{productId}/raw-materials")
@Produces(MediaType.APPLICATION_JSON)
@Consumes(MediaType.APPLICATION_JSON)
@RolesAllowed("user")
public class ProductRawMaterialResource {

    @Inject
    ProductRawMaterialService productRawMaterialService;

    @GET
    public Response listRawMaterials(@PathParam("productId") Long productId) {
        List<ProductRawMaterialResponse> response = productRawMaterialService.listByProduct(productId);
        return Response.ok(response).build();
    }

    @POST
    public Response addRawMaterial(
            @PathParam("productId") Long productId,
            @Valid ProductRawMaterialRequest request) {
        ProductRawMaterialResponse response = productRawMaterialService.addRawMaterial(productId, request);
        return Response.status(Response.Status.CREATED).entity(response).build();
    }

    @PUT
    @Path("/{associationId}")
    public Response updateRawMaterial(
            @PathParam("productId") Long productId,
            @PathParam("associationId") Long associationId,
            @Valid ProductRawMaterialRequest request) {
        ProductRawMaterialResponse response = productRawMaterialService.updateRawMaterial(productId, associationId, request);
        return Response.ok(response).build();
    }

    @DELETE
    @Path("/{associationId}")
    public Response removeRawMaterial(
            @PathParam("productId") Long productId,
            @PathParam("associationId") Long associationId) {
        productRawMaterialService.removeRawMaterial(productId, associationId);
        return Response.noContent().build();
    }
}