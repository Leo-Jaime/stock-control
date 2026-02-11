package com.stockcontrol.exception;

import com.stockcontrol.dto.ErrorResponse;
import jakarta.validation.ConstraintViolation;
import jakarta.validation.ConstraintViolationException;
import jakarta.ws.rs.core.Response;
import jakarta.ws.rs.ext.ExceptionMapper;
import jakarta.ws.rs.ext.Provider;

import java.util.stream.Collectors;

@Provider
public class GlobalExceptionMapper implements ExceptionMapper<Exception> {

    @Override
    public Response toResponse(Exception exception) {

        if (exception instanceof ConstraintViolationException) {
            String messages = ((ConstraintViolationException) exception).getConstraintViolations()
                .stream()
                .map(ConstraintViolation::getMessage)
                .collect(Collectors.joining("; "));
            return buildResponse(Response.Status.BAD_REQUEST, messages);
        }

        if (exception instanceof NotFoundException) {
            return buildResponse(Response.Status.NOT_FOUND, exception.getMessage());
        }

        if (exception instanceof ConflictException) {
            return buildResponse(Response.Status.CONFLICT, exception.getMessage());
        }

        if (exception instanceof ValidationException) {
            return buildResponse(Response.Status.BAD_REQUEST, exception.getMessage());
        }

        return buildResponse(
            Response.Status.INTERNAL_SERVER_ERROR,
            "Erro interno do servidor: " + exception.getMessage()
        );
    }

    private Response buildResponse(Response.Status status, String message) {
        ErrorResponse error = new ErrorResponse(message, status.getStatusCode());
        return Response.status(status).entity(error).build();
    }
}
