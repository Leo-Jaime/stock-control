package com.stockcontrol.resource;

import java.lang.annotation.Repeatable;

import com.stockcontrol.entity.RawMaterial;
import jakarta.transaction.Transactional;
import jakarta.ws.rs.*;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;

import java.util.List;

@Path("/raw-materials")
@Produces(MediaType.APPLICATION_JSON)
@Consumes(MediaType.APPLICATION_JSON)
public class RawMaterialResource {
    
    @GET
    public Response findAll(){
        try {
            List<RawMaterial> rawMaterials = RawMaterial.listAll();
            return Response.ok(rawMaterials).build();
        } catch (Exception e) {
            return Response.status(Response.Status.INTERNAL_SERVER_ERROR).entity("Erro ao recuperar matérias primas: " + e.getMessage()).build();
        }
    }

    @GET
    @Path("/{id}")
    public Response findById(@PathParam("id") Long id){
        try {
            RawMAterial rawMaterial = RawMaterial.findById(id);

            if (rawMaterial == null) {
                return Reponse.status(Response.Status.NOT_FOUND).entity("Matéria prima nao econtrada com o id:" + id).build();
            }

            return Response.ok(rawMaterial).build();
        } catch (Exception e) {
            return Response.status(Response.Status.INTERNAL_SERVER_ERROR).entity("Erro ao recuperar a matéria prima: " + e.getMessage()).build();
        }
    }

    @POST
    @Transactional
    public Response create(RawMaterial rawMaterial){
        try {
            if (rawMaterial == null){
                return Response.status(Response.Status.BAD_REQUEST).entity("Os dados da matéria prima e requirido").build();
            }

            if(rawMaterial.code == null || rawMaterial.code.trim().isEmpty()){
                return Response.status(Response.Status.BAD_REQUEST).entity("Codigo do matérial e requirido").build();
            }

            if (rawMaterial.name == null || rawMaterial.name.trim().isEmpty()){
                return Response.status(Response.Status.BAD_REQUEST).entity("Nome do matérial e requirido").build();
            }

            if (rawMaterial.stockQuantity == null || rawMaterial.stockQuantity < 0){
                return Response.status(Response.Status.BAD_REQUEST).entity("A quantidade em estoque deve ser zero ou maior.").build();
            }

            RawMaterial existingRawMaterial = RawMaterial.find("code", rawMaterial.code).firstResult();
            if ( existingRawMaterial != null) { 
                return Response.status(Response.Status.CONFLICT).entity("Matéria prima com codigo: '" + rawMaterial.code + "' já existe!").build();
            }

            rawMaterial.persist();

            return Response.status(Response.Status.CREATED).entity(rawMaterial).build();

        } catch (Exception e) {
            return Response.status(Response.Status.INTERNAL_SERVER_ERROR).entity("Erro ao criar matéria prima: " + e.getMessage()).build();
        }
    }

    @PUT
    @Path("/{id}")
    @Transactional
    public Response update(@PathParam("id") Long id, RawMaterial data) {
        try {
            
            if (data == null) {
                return Response.status(Response.Status.BAD_REQUEST)
                    .entity("Os dados da matéria prima são obrigatórios.")
                    .build();
            }

            
            RawMaterial rawMaterial = RawMaterial.findById(id);

            if (rawMaterial == null) {
                return Response.status(Response.Status.NOT_FOUND)
                    .entity("Matéria prima não encontrada para o ID informado: " + id)
                    .build();
            }

            
            if (data.code == null || data.code.trim().isEmpty()) {
                return Response.status(Response.Status.BAD_REQUEST)
                    .entity("O código da matéria-prima é obrigatório.")
                    .build();
            }

            if (data.name == null || data.name.trim().isEmpty()) {
                return Response.status(Response.Status.BAD_REQUEST)
                    .entity("O nome da matéria-prima é obrigatório.")
                    .build();
            }

            if (data.stockQuantity == null || data.stockQuantity < 0) {
                return Response.status(Response.Status.BAD_REQUEST)
                    .entity("A quantidade em estoque deve ser zero ou maior.")
                    .build();
            }

            // Validação codigo duplicado 
            if (!rawMaterial.code.equals(data.code)) {
                RawMaterial existingRawMaterial = RawMaterial.find("code", data.code).firstResult();
                if (existingRawMaterial != null) {
                    return Response.status(Response.Status.CONFLICT)
                        .entity("Já existe uma matéria prima cadastrada com o código '" + data.code + "'")
                        .build();
                }
            }

            // Atualiza os dados
            rawMaterial.code = data.code;
            rawMaterial.name = data.name;
            rawMaterial.stockQuantity = data.stockQuantity;
            rawMaterial.persist();

            return Response.ok(rawMaterial).build();
            
        } catch (Exception e) {
            return Response.status(Response.Status.INTERNAL_SERVER_ERROR)
                .entity("Error updating raw material: " + e.getMessage())
                .build();
        }
    }

    @DELETE
    @Path("/{id}")
    @Transactional
    public Response delete(@PathParam("id") Long id) {
        try {
            boolean deleted = RawMaterial.deleteById(id);
            
            if (!deleted) {
                return Response.status(Response.Status.NOT_FOUND)
                    .entity("Matéria prima não encontrada com o id: " + id)
                    .build();
            }
            
            return Response.noContent().build();
            
        } catch (Exception e) {
            return Response.status(Response.Status.INTERNAL_SERVER_ERROR)
                .entity("Erro ao deletar Matéria prima: " + e.getMessage())
                .build();
        }
    }
}