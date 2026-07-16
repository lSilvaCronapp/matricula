package br.edu.com.matricula.dto.request;

import jakarta.validation.constraints.NotNull;

import java.util.UUID;

public record MatriculaCreateRequest(
        @NotNull(message = "Aluno é obrigatório")
        UUID alunoId,

        @NotNull(message = "Turma é obrigatória")
        UUID turmaId
) {
}
