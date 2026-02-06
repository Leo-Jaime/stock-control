package com.example.resource;

import com.example.domain.Product;
import jakarta.transaction.Transactional;
import jakarta.ws.rs.*;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;

import java.util.List;

@Path("/products")
@Produces(MediaType.APPLICATION_JSON)
@Consumes(MediaType.APPLICATION_JSON)
public class ProductResource {

    @GET
    public Response findAll() {
        try {
            List<Product> products = Product.listAll();
            return Response.ok(products).build();
        } catch (Exception e ){
            return Response.status(Response.Status.INTERNAL_SERVER_ERROR).entity("Erro ao recuperar produtos" + e.getMessage()).build();
        }


        }

    @GET
    @Path("/{id}")
    public Response findById(@PathParam("id") Long id) {
        try{
            Product product = Product.findById(id);

            if (product == null){
                return Response.status(Response.Status.NOT_FOUND).entity("Produto nao econtrado com id:" + id).build();
            }

            return Response.ok(product).build();
        } catch (Exception e) {
            return Response.status(Response.Status.INTERNAL_SERVER_ERROR).entity("Erro ao recuperar produtos" + e.getMessage()).build();
        }
    }


    @POST
    @Transactional
    public Response create(Product product) {
        try {
            if (product == null){
                return Response.status(Response.Status.BAD_REQUEST).entity("Dados do Produto requiridos").build();
            }
            
            if (product.code == null || product.code.trim().isEmpty()){
                return Response.status(Response.Status.BAD_REQUEST).entity("Codigo do produto requirido").build();
            }

            if (product.name == null || product.name.trim().isEmpty()){
                return Response.status(Response.Status.BAD_REQUEST).entity("Nome do produto requirido").build();
            }

            if (product.value == null || product.value.doubleValue() <= 0){
                return Response.status(Response.Status.BAD_REQUEST).entity("Valor do Produto tem que ser maior que zero").build();
            }
            
            Product existingProduct = Product.find("code", product.code).firstResult();
            if (existingProduct != null){
                return Response.status(Response.Status.CONFLICT).entity("Produto com o codigo '"+ product.code + "' já existe").build();
            }

            product.persist();

            return Response.status(Response.Status.CREATED).entity(product).build();
        } catch (Exception e) {
            return Response.status(Response.Status.INTERNAL_SERVER_ERROR).entity("Erro ao criar produto: " + e.getMessage()).build();
        }
    }

    @PUT
    @Path("/{id}")
    @Transactional
    public Product update(@PathParam("id") Long id, Product data) {
        Product product = Product.findById(id);

        if (product == null) {
            throw new NotFoundException();
        }

        product.code = data.code;
        product.name = data.name;
        product.value = data.value;

        return product;
    }

    @DELETE
    @Path("/{id}")
    @Transactional
    public void delete(@PathParam("id") Long id) {
        Product.deleteById(id);
    }
}
