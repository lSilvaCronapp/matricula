package br.edu.com.matricula.dto.response;

import java.time.Instant;
import java.util.UUID;

public record CursoResponse(
        UUID id,
        String nome,
        String codigo,
        String descricao,
        Boolean ativo,
        Instant createdAt,
        Instant updatedAt
) {
}
