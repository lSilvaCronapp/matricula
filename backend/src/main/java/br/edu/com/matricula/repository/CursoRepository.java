package br.edu.com.matricula.repository;

import br.edu.com.matricula.domain.model.Curso;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;
import java.util.UUID;

public interface CursoRepository extends JpaRepository<Curso, UUID> {

    Optional<Curso> findByCodigo(String codigo);
}
