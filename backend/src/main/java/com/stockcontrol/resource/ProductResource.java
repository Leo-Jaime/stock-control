package com.stockcontrol.resource;

import com.stockcontrol.dto.ProductRequest;
import com.stockcontrol.dto.ProductResponse;
import com.stockcontrol.service.ProductService;
import jakarta.inject.Inject;
import jakarta.validation.Valid;
import jakarta.ws.rs.*;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;

import java.util.List;

@Path("/products")
@Produces(MediaType.APPLICATION_JSON)
@Consumes(MediaType.APPLICATION_JSON)
public class ProductResource {

    @Inject
    ProductService productService;

    @GET
    public Response findAll() {
        List<ProductResponse> response = productService.findAll();
        return Response.ok(response).build();
    }

    @GET
    @Path("/{id}")
    public Response findById(@PathParam("id") Long id) {
        ProductResponse response = productService.findById(id);
        return Response.ok(response).build();
    }

    @POST
    public Response create(@Valid ProductRequest request) {
        ProductResponse response = productService.create(request);
        return Response.status(Response.Status.CREATED).entity(response).build();
    }

    @PUT
    @Path("/{id}")
    public Response update(@PathParam("id") Long id, @Valid ProductRequest request) {
        ProductResponse response = productService.update(id, request);
        return Response.ok(response).build();
    }

    @DELETE
    @Path("/{id}")
    public Response delete(@PathParam("id") Long id) {
        productService.delete(id);
        return Response.noContent().build();
    }
}
