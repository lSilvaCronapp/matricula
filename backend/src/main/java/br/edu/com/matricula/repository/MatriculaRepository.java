package br.edu.com.matricula.repository;

import br.edu.com.matricula.domain.model.Matricula;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;

import java.util.List;
import java.util.UUID;

public interface MatriculaRepository extends JpaRepository<Matricula, UUID>, JpaSpecificationExecutor<Matricula> {

    boolean existsByAlunoId(UUID alunoId);

    boolean existsByTurmaId(UUID turmaId);

    boolean existsByAlunoIdAndTurmaId(UUID alunoId, UUID turmaId);

    @EntityGraph(attributePaths = {"aluno", "turma", "turma.disciplina"})
    List<Matricula> findByAlunoId(UUID alunoId);

    @EntityGraph(attributePaths = {"aluno", "turma", "turma.disciplina"})
    List<Matricula> findByTurmaId(UUID turmaId);

    @EntityGraph(attributePaths = {"aluno", "turma", "turma.disciplina"})
    Page<Matricula> findAll(Specification<Matricula> spec, Pageable pageable);
}
