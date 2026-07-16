package br.edu.com.matricula.repository;

import br.edu.com.matricula.domain.model.Disciplina;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;
import java.util.UUID;

public interface DisciplinaRepository extends JpaRepository<Disciplina, UUID> {

    Optional<Disciplina> findByCodigo(String codigo);

    boolean existsByCursoId(UUID cursoId);
}
