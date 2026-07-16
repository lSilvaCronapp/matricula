package br.edu.com.matricula.mapper;

import br.edu.com.matricula.domain.model.Curso;
import br.edu.com.matricula.domain.model.Disciplina;
import br.edu.com.matricula.dto.request.DisciplinaRequest;
import br.edu.com.matricula.dto.response.DisciplinaResponse;

public final class DisciplinaMapper {

    private DisciplinaMapper() {
    }

    public static Disciplina toEntity(DisciplinaRequest request, Curso curso) {
        var disciplina = new Disciplina();
        apply(request, disciplina, curso);
        return disciplina;
    }

    public static void apply(DisciplinaRequest request, Disciplina disciplina, Curso curso) {
        disciplina.setNome(request.nome().trim());
        disciplina.setCodigo(request.codigo().trim().toUpperCase());
        disciplina.setCargaHoraria(request.cargaHoraria());
        disciplina.setCurso(curso);
        disciplina.setAtivo(request.ativo());
    }

    public static DisciplinaResponse toResponse(Disciplina disciplina) {
        return new DisciplinaResponse(
                disciplina.getId(),
                disciplina.getNome(),
                disciplina.getCodigo(),
                disciplina.getCargaHoraria(),
                disciplina.getCurso().getId(),
                disciplina.getCurso().getNome(),
                disciplina.getAtivo(),
                disciplina.getCreatedAt(),
                disciplina.getUpdatedAt()
        );
    }
}
