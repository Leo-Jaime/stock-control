package com.stockcontrol.resource;

import jakarta.transaction.Transactional;
import jakarta.ws.rs.*;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;

import java.util.List;

import com.stockcontrol.entity.Product;

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
            return Response.status(Response.Status.INTERNAL_SERVER_ERROR).entity("Erro ao recuperar produtos: " + e.getMessage()).build();
        }


        }

    @GET
    @Path("/{id}")
    public Response findById(@PathParam("id") Long id) {
        try{
            Product product = Product.findById(id);

            if (product == null){
                return Response.status(Response.Status.NOT_FOUND).entity("Produto não encontrado com id: " + id).build();
            }

            return Response.ok(product).build();
        } catch (Exception e) {
            return Response.status(Response.Status.INTERNAL_SERVER_ERROR).entity("Erro ao recuperar produto: " + e.getMessage()).build();
        }
    }


    @POST
    @Transactional
    public Response create(Product product) {
        try {
            if (product == null){
                return Response.status(Response.Status.BAD_REQUEST).entity("Dados do produto requeridos").build();
            }
            
            if (product.code == null || product.code.trim().isEmpty()){
                return Response.status(Response.Status.BAD_REQUEST).entity("Código do produto requerido").build();
            }

            if (product.name == null || product.name.trim().isEmpty()){
                return Response.status(Response.Status.BAD_REQUEST).entity("Nome do produto requerido").build();
            }

            if (product.value == null || product.value.doubleValue() <= 0){
                return Response.status(Response.Status.BAD_REQUEST).entity("O valor do produto deve ser maior que zero").build();
            }
            
            Product existingProduct = Product.find("code", product.code).firstResult();
            if (existingProduct != null){
                return Response.status(Response.Status.CONFLICT).entity("Produto com o código '"+ product.code + "' já existe").build();
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
    public Response update(@PathParam("id") Long id, Product data) {
        try {
            if (data == null) { 
                return Response.status(Response.Status.BAD_REQUEST).entity("Dados do produto requeridos").build();
            }
            
            Product product = Product.findById(id);
            if (product == null){
                return Response.status(Response.Status.NOT_FOUND).entity("Produto não econtrado id: " + id).build();
            }

            if (data.code == null || data.code.trim().isEmpty()){
                return Response.status(Response.Status.BAD_REQUEST).entity("Código do produto requerido").build();
            }

            if (data.name == null || data.name.trim().isEmpty()){
                return Response.status(Response.Status.BAD_REQUEST).entity("Nome do produto requerido").build();
            } 
            
            if (data.value == null || data.value.doubleValue() <= 0){
                return Response.status(Response.Status.BAD_REQUEST).entity("O valor do produto deve ser maior que zero").build();
            }

            if (!product.code.equals(data.code)){
                Product existingProduct = Product.find("code", data.code).firstResult();
                if (existingProduct != null){
                    return Response.status(Response.Status.CONFLICT).entity("Produto com o código : '" + data.code + "' já existe").build();
                }
            }

            product.code = data.code;
            product.name = data.name;
            product.value = data.value;
            product.persist();

            return Response.ok(product).build();

        } catch (Exception e){
            return Response.status(Response.Status.INTERNAL_SERVER_ERROR).entity("Erro ao atualizar o produto: " + e.getMessage()).build();
        }

    }

    @DELETE
    @Path("/{id}")
    @Transactional
    public Response delete(@PathParam("id") Long id) {
        try {

            boolean deleted = Product.deleteById(id);

            if (!deleted) {
                return Response.status(Response.Status.NOT_FOUND).entity("Produto não encontrado com id: " + id).build();
            }

            return Response.noContent().build();

        } catch (Exception e){
            return Response.status(Response.Status.INTERNAL_SERVER_ERROR).entity("Erro ao deletar produto:  " + e.getMessage()).build();
        }
    }
}
