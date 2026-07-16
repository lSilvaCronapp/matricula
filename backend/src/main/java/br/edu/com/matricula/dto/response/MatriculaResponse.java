package br.edu.com.matricula.dto.response;

import br.edu.com.matricula.domain.enums.StatusMatricula;

import java.time.Instant;
import java.util.UUID;

public record MatriculaResponse(
        UUID id,
        UUID alunoId,
        String alunoNome,
        UUID turmaId,
        String turmaCodigo,
        UUID disciplinaId,
        String disciplinaNome,
        StatusMatricula status,
        Instant dataSolicitacao,
        Instant dataConfirmacao,
        Instant dataCancelamento,
        Instant createdAt,
        Instant updatedAt
) {
}
