package br.edu.com.matricula.repository;

import br.edu.com.matricula.domain.model.Disciplina;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;

import java.util.Optional;
import java.util.UUID;

public interface DisciplinaRepository extends JpaRepository<Disciplina, UUID>, JpaSpecificationExecutor<Disciplina> {

    Optional<Disciplina> findByCodigo(String codigo);

    boolean existsByCursoId(UUID cursoId);

    @EntityGraph(attributePaths = {"curso"})
    Page<Disciplina> findAll(Specification<Disciplina> spec, Pageable pageable);
}
