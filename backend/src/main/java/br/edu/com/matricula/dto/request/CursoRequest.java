package br.edu.com.matricula.dto.request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

public record CursoRequest(
        @NotBlank(message = "Nome é obrigatório")
        @Size(min = 2, max = 120, message = "Nome deve ter entre 2 e 120 caracteres")
        String nome,

        @NotBlank(message = "Código é obrigatório")
        @Size(max = 30, message = "Código deve ter no máximo 30 caracteres")
        String codigo,

        @Size(max = 500, message = "Descrição deve ter no máximo 500 caracteres")
        String descricao,

        @NotNull(message = "Ativo é obrigatório")
        Boolean ativo
) {
}
