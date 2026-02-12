package com.stockcontrol.resource;

import com.stockcontrol.dto.RawMaterialRequest;
import com.stockcontrol.dto.RawMaterialResponse;
import com.stockcontrol.service.RawMaterialService;
import jakarta.inject.Inject;
import jakarta.validation.Valid;
import jakarta.ws.rs.*;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;

import java.util.List;

@Path("/raw-materials")
@Produces(MediaType.APPLICATION_JSON)
@Consumes(MediaType.APPLICATION_JSON)
public class RawMaterialResource {

    @Inject
    RawMaterialService rawMaterialService;

    @GET
    public Response findAll() {
        List<RawMaterialResponse> response = rawMaterialService.findAll();
        return Response.ok(response).build();
    }

    @GET
    @Path("/{id}")
    public Response findById(@PathParam("id") Long id) {
        RawMaterialResponse response = rawMaterialService.findById(id);
        return Response.ok(response).build();
    }

    @POST
    public Response create(@Valid RawMaterialRequest request) {
        RawMaterialResponse response = rawMaterialService.create(request);
        return Response.status(Response.Status.CREATED).entity(response).build();
    }

    @PUT
    @Path("/{id}")
    public Response update(@PathParam("id") Long id, @Valid RawMaterialRequest request) {
        RawMaterialResponse response = rawMaterialService.update(id, request);
        return Response.ok(response).build();
    }

    @DELETE
    @Path("/{id}")
    public Response delete(@PathParam("id") Long id) {
        rawMaterialService.delete(id);
        return Response.noContent().build();
    }
}