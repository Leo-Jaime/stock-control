package com.stockcontrol.resource;

import jakarta.transaction.Transactional;
import jakarta.ws.rs.*;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;

import java.util.List;
import java.util.stream.Collectors;

import com.stockcontrol.dto.ProductRequest;
import com.stockcontrol.dto.ProductResponse;
import com.stockcontrol.entity.Product;
import com.stockcontrol.exception.ConflictException;
import com.stockcontrol.exception.NotFoundException;
import com.stockcontrol.exception.ValidationException;

@Path("/products")
@Produces(MediaType.APPLICATION_JSON)
@Consumes(MediaType.APPLICATION_JSON)
public class ProductResource {

    @GET
    public Response findAll() {
        List<Product> products = Product.listAll();
        List<ProductResponse> response = products.stream()
            .map(ProductResponse::fromEntity)
            .collect(Collectors.toList());
        return Response.ok(response).build();
    }

    @GET
    @Path("/{id}")
    public Response findById(@PathParam("id") Long id) {
        Product product = Product.findById(id);

        if (product == null) {
            throw new NotFoundException("Produto não encontrado com id: " + id);
        }

        return Response.ok(ProductResponse.fromEntity(product)).build();
    }

    @POST
    @Transactional
    public Response create(ProductRequest request) {
        if (request == null) {
            throw new ValidationException("Dados do produto requeridos");
        }

        if (request.code == null || request.code.trim().isEmpty()) {
            throw new ValidationException("Código do produto requerido");
        }

        if (request.name == null || request.name.trim().isEmpty()) {
            throw new ValidationException("Nome do produto requerido");
        }

        if (request.value == null || request.value.doubleValue() <= 0) {
            throw new ValidationException("O valor do produto deve ser maior que zero");
        }

        Product existingProduct = Product.find("code", request.code).firstResult();
        if (existingProduct != null) {
            throw new ConflictException("Produto com o código '" + request.code + "' já existe");
        }

        Product product = request.toEntity();
        product.persist();

        return Response.status(Response.Status.CREATED)
            .entity(ProductResponse.fromEntity(product))
            .build();
    }

    @PUT
    @Path("/{id}")
    @Transactional
    public Response update(@PathParam("id") Long id, ProductRequest request) {
        if (request == null) {
            throw new ValidationException("Dados do produto requeridos");
        }

        Product product = Product.findById(id);
        if (product == null) {
            throw new NotFoundException("Produto não encontrado com id: " + id);
        }

        if (request.code == null || request.code.trim().isEmpty()) {
            throw new ValidationException("Código do produto requerido");
        }

        if (request.name == null || request.name.trim().isEmpty()) {
            throw new ValidationException("Nome do produto requerido");
        }

        if (request.value == null || request.value.doubleValue() <= 0) {
            throw new ValidationException("O valor do produto deve ser maior que zero");
        }

        if (!product.code.equals(request.code)) {
            Product existingProduct = Product.find("code", request.code).firstResult();
            if (existingProduct != null) {
                throw new ConflictException("Produto com o código: '" + request.code + "' já existe");
            }
        }

        product.code = request.code;
        product.name = request.name;
        product.value = request.value;
        product.persist();

        return Response.ok(ProductResponse.fromEntity(product)).build();
    }

    @DELETE
    @Path("/{id}")
    @Transactional
    public Response delete(@PathParam("id") Long id) {
        boolean deleted = Product.deleteById(id);

        if (!deleted) {
            throw new NotFoundException("Produto não encontrado com id: " + id);
        }

        return Response.noContent().build();
    }
}
