package br.edu.com.matricula.mapper;

import br.edu.com.matricula.domain.model.Matricula;
import br.edu.com.matricula.dto.response.MatriculaResponse;

public final class MatriculaMapper {

    private MatriculaMapper() {
    }

    public static MatriculaResponse toResponse(Matricula matricula) {
        var turma = matricula.getTurma();
        var disciplina = turma.getDisciplina();

        return new MatriculaResponse(
                matricula.getId(),
                matricula.getAluno().getId(),
                matricula.getAluno().getNome(),
                turma.getId(),
                turma.getCodigo(),
                disciplina.getId(),
                disciplina.getNome(),
                matricula.getStatus(),
                matricula.getDataSolicitacao(),
                matricula.getDataConfirmacao(),
                matricula.getDataCancelamento(),
                matricula.getCreatedAt(),
                matricula.getUpdatedAt()
        );
    }
}
