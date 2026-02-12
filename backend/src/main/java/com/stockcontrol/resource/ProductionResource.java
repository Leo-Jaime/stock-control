package com.stockcontrol.resource;

import com.stockcontrol.dto.ProductionReport;
import com.stockcontrol.service.ProductionService;
import jakarta.annotation.security.RolesAllowed;
import jakarta.inject.Inject;
import jakarta.ws.rs.GET;
import jakarta.ws.rs.Path;
import jakarta.ws.rs.Produces;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;

@Path("/production")
@Produces(MediaType.APPLICATION_JSON)
@RolesAllowed("user")
public class ProductionResource {

    @Inject
    ProductionService productionService;

    @GET
    @Path("/calculate")
    public Response calculateProduction() {
        ProductionReport report = productionService.calculateProduction();
        return Response.ok(report).build();
    }
}