package br.edu.com.matricula.dto.response;

import java.time.Instant;
import java.util.UUID;

public record DisciplinaResponse(
        UUID id,
        String nome,
        String codigo,
        Integer cargaHoraria,
        UUID cursoId,
        String cursoNome,
        Boolean ativo,
        Instant createdAt,
        Instant updatedAt
) {
}
