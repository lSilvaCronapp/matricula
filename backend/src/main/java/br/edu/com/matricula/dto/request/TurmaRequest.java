package br.edu.com.matricula.dto.request;

import br.edu.com.matricula.domain.enums.StatusTurma;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;

import java.util.UUID;

public record TurmaRequest(
        @NotBlank(message = "Código é obrigatório")
        @Size(max = 40, message = "Código deve ter no máximo 40 caracteres")
        String codigo,

        @NotNull(message = "Disciplina é obrigatória")
        UUID disciplinaId,

        @NotNull(message = "Ano é obrigatório")
        @Min(value = 2000, message = "Ano deve ser maior ou igual a 2000")
        Integer ano,

        @NotBlank(message = "Período é obrigatório")
        @Pattern(regexp = "[12]", message = "Período deve ser 1 ou 2")
        String periodo,

        @NotNull(message = "Limite de vagas é obrigatório")
        @Min(value = 1, message = "Limite de vagas deve ser maior ou igual a 1")
        Integer limiteVagas,

        StatusTurma status
) {
}
