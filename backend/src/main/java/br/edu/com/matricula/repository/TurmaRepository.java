package br.edu.com.matricula.repository;

import br.edu.com.matricula.domain.enums.StatusTurma;
import br.edu.com.matricula.domain.model.Turma;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface TurmaRepository extends JpaRepository<Turma, UUID>, JpaSpecificationExecutor<Turma> {

    Optional<Turma> findByCodigo(String codigo);

    boolean existsByDisciplinaId(UUID disciplinaId);

    List<Turma> findByStatus(StatusTurma status);

    @EntityGraph(attributePaths = {"disciplina"})
    Page<Turma> findAll(Specification<Turma> spec, Pageable pageable);
}
