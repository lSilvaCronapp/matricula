package br.edu.com.matricula.mapper;

import br.edu.com.matricula.domain.enums.StatusTurma;
import br.edu.com.matricula.domain.model.Disciplina;
import br.edu.com.matricula.domain.model.Turma;
import br.edu.com.matricula.dto.request.TurmaRequest;
import br.edu.com.matricula.dto.response.TurmaResponse;

public final class TurmaMapper {

    private TurmaMapper() {
    }

    public static Turma toEntity(TurmaRequest request, Disciplina disciplina) {
        var turma = new Turma();
        apply(request, turma, disciplina);
        turma.setVagasOcupadas(0);
        return turma;
    }

    public static void apply(TurmaRequest request, Turma turma, Disciplina disciplina) {
        turma.setCodigo(request.codigo().trim().toUpperCase());
        turma.setDisciplina(disciplina);
        turma.setAno(request.ano());
        turma.setPeriodo(request.periodo());
        turma.setLimiteVagas(request.limiteVagas());
        turma.setStatus(request.status() == null ? StatusTurma.ABERTA : request.status());
    }

    public static TurmaResponse toResponse(Turma turma) {
        return new TurmaResponse(
                turma.getId(),
                turma.getCodigo(),
                turma.getDisciplina().getId(),
                turma.getDisciplina().getNome(),
                turma.getAno(),
                turma.getPeriodo(),
                turma.getLimiteVagas(),
                turma.getVagasOcupadas(),
                turma.getStatus(),
                turma.getVersion(),
                turma.getCreatedAt(),
                turma.getUpdatedAt()
        );
    }
}
