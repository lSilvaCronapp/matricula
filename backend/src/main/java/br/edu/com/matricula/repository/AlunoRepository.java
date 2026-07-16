package br.edu.com.matricula.repository;

import br.edu.com.matricula.domain.model.Aluno;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;
import java.util.UUID;

public interface AlunoRepository extends JpaRepository<Aluno, UUID> {

    Optional<Aluno> findByEmail(String email);

    Optional<Aluno> findByCpf(String cpf);

    Optional<Aluno> findByMatriculaAcademica(String matriculaAcademica);
}
