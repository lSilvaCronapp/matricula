package br.edu.com.matricula.dto.response;

import br.edu.com.matricula.domain.enums.StatusTurma;

import java.time.Instant;
import java.util.UUID;

public record TurmaResponse(
        UUID id,
        String codigo,
        UUID disciplinaId,
        String disciplinaNome,
        Integer ano,
        String periodo,
        Integer limiteVagas,
        Integer vagasOcupadas,
        StatusTurma status,
        Long version,
        Instant createdAt,
        Instant updatedAt
) {
}
