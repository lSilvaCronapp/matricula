package br.edu.com.matricula.dto.request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import jakarta.validation.constraints.Size;

import java.util.UUID;

public record DisciplinaRequest(
        @NotBlank(message = "Nome é obrigatório")
        @Size(min = 2, max = 120, message = "Nome deve ter entre 2 e 120 caracteres")
        String nome,

        @NotBlank(message = "Código é obrigatório")
        @Size(max = 30, message = "Código deve ter no máximo 30 caracteres")
        String codigo,

        @NotNull(message = "Carga horária é obrigatória")
        @Positive(message = "Carga horária deve ser maior que zero")
        Integer cargaHoraria,

        @NotNull(message = "Curso é obrigatório")
        UUID cursoId,

        @NotNull(message = "Ativo é obrigatório")
        Boolean ativo
) {
}
