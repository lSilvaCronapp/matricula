package br.edu.com.matricula.dto.response;

public record FieldErrorResponse(
        String field,
        String message
) {
}
