package br.edu.com.matricula.repository;

import br.edu.com.matricula.domain.model.Aluno;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;

import java.util.Optional;
import java.util.UUID;

public interface AlunoRepository extends JpaRepository<Aluno, UUID>, JpaSpecificationExecutor<Aluno> {

    Optional<Aluno> findByEmail(String email);

    Optional<Aluno> findByCpf(String cpf);

    Optional<Aluno> findByMatriculaAcademica(String matriculaAcademica);
}
