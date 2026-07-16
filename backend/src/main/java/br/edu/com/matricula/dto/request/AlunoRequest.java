package br.edu.com.matricula.dto.request;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.PastOrPresent;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;

import java.time.LocalDate;

public record AlunoRequest(
        @NotBlank(message = "Nome é obrigatório")
        @Size(min = 2, max = 120, message = "Nome deve ter entre 2 e 120 caracteres")
        String nome,

        @NotBlank(message = "E-mail é obrigatório")
        @Email(message = "E-mail deve ser válido")
        @Size(max = 150, message = "E-mail deve ter no máximo 150 caracteres")
        String email,

        @NotBlank(message = "CPF é obrigatório")
        @Pattern(regexp = "\\d{11}", message = "CPF deve conter 11 dígitos")
        String cpf,

        @NotBlank(message = "Matrícula acadêmica é obrigatória")
        @Size(max = 30, message = "Matrícula acadêmica deve ter no máximo 30 caracteres")
        String matriculaAcademica,

        @PastOrPresent(message = "Data de nascimento não pode ser futura")
        LocalDate dataNascimento,

        @NotNull(message = "Ativo é obrigatório")
        Boolean ativo
) {
}
