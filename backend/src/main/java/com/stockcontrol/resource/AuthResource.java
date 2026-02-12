package com.stockcontrol.resource;

import com.stockcontrol.dto.AuthRequest;
import com.stockcontrol.dto.AuthResponse;
import com.stockcontrol.service.AuthService;
import jakarta.inject.Inject;
import jakarta.validation.Valid;
import jakarta.ws.rs.*;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;

@Path("/auth")
@Produces(MediaType.APPLICATION_JSON)
@Consumes(MediaType.APPLICATION_JSON)
public class AuthResource {

    @Inject
    AuthService authService;

    @POST
    @Path("/login")
    public Response login(@Valid AuthRequest request) {
        AuthResponse response = authService.login(request);
        return Response.ok(response).build();
    }

    @POST
    @Path("/register")
    public Response register(@Valid AuthRequest request) {
        authService.register(request);
        return Response.status(Response.Status.CREATED).build();
    }
}
