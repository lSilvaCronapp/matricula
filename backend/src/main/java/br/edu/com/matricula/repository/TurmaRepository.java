package br.edu.com.matricula.repository;

import br.edu.com.matricula.domain.enums.StatusTurma;
import br.edu.com.matricula.domain.model.Turma;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface TurmaRepository extends JpaRepository<Turma, UUID> {

    Optional<Turma> findByCodigo(String codigo);

    boolean existsByDisciplinaId(UUID disciplinaId);

    List<Turma> findByStatus(StatusTurma status);
}
