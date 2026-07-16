package br.edu.com.matricula.mapper;

import br.edu.com.matricula.domain.model.Curso;
import br.edu.com.matricula.dto.request.CursoRequest;
import br.edu.com.matricula.dto.response.CursoResponse;

public final class CursoMapper {

    private CursoMapper() {
    }

    public static Curso toEntity(CursoRequest request) {
        var curso = new Curso();
        apply(request, curso);
        return curso;
    }

    public static void apply(CursoRequest request, Curso curso) {
        curso.setNome(request.nome().trim());
        curso.setCodigo(request.codigo().trim().toUpperCase());
        curso.setDescricao(request.descricao());
        curso.setAtivo(request.ativo());
    }

    public static CursoResponse toResponse(Curso curso) {
        return new CursoResponse(
                curso.getId(),
                curso.getNome(),
                curso.getCodigo(),
                curso.getDescricao(),
                curso.getAtivo(),
                curso.getCreatedAt(),
                curso.getUpdatedAt()
        );
    }
}
