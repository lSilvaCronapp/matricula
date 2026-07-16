package br.edu.com.matricula.mapper;

import br.edu.com.matricula.domain.model.Aluno;
import br.edu.com.matricula.dto.request.AlunoRequest;
import br.edu.com.matricula.dto.response.AlunoResponse;

public final class AlunoMapper {

    private AlunoMapper() {
    }

    public static Aluno toEntity(AlunoRequest request) {
        var aluno = new Aluno();
        apply(request, aluno);
        return aluno;
    }

    public static void apply(AlunoRequest request, Aluno aluno) {
        aluno.setNome(request.nome().trim());
        aluno.setEmail(request.email().trim().toLowerCase());
        aluno.setCpf(request.cpf().trim());
        aluno.setMatriculaAcademica(request.matriculaAcademica().trim());
        aluno.setDataNascimento(request.dataNascimento());
        aluno.setAtivo(request.ativo());
    }

    public static AlunoResponse toResponse(Aluno aluno) {
        return new AlunoResponse(
                aluno.getId(),
                aluno.getNome(),
                aluno.getEmail(),
                aluno.getCpf(),
                aluno.getMatriculaAcademica(),
                aluno.getDataNascimento(),
                aluno.getAtivo(),
                aluno.getCreatedAt(),
                aluno.getUpdatedAt()
        );
    }
}
