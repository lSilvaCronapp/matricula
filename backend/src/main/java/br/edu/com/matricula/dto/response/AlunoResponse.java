package br.edu.com.matricula.dto.response;

import java.time.Instant;
import java.time.LocalDate;
import java.util.UUID;

public record AlunoResponse(
        UUID id,
        String nome,
        String email,
        String cpf,
        String matriculaAcademica,
        LocalDate dataNascimento,
        Boolean ativo,
        Instant createdAt,
        Instant updatedAt
) {
}
