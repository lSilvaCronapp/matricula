package br.edu.com.matricula.support;

import br.edu.com.matricula.domain.enums.StatusMatricula;
import br.edu.com.matricula.domain.enums.StatusTurma;
import br.edu.com.matricula.domain.model.Aluno;
import br.edu.com.matricula.domain.model.Disciplina;
import br.edu.com.matricula.domain.model.Matricula;
import br.edu.com.matricula.domain.model.Turma;
import org.springframework.test.util.ReflectionTestUtils;

import java.time.Instant;
import java.util.UUID;

public final class TestEntityFactory {

    private TestEntityFactory() {
    }

    public static Aluno alunoAtivo() {
        var aluno = new Aluno();
        aluno.setNome("Maria Silva");
        aluno.setEmail("maria@email.com");
        aluno.setCpf("12345678901");
        aluno.setMatriculaAcademica("RA2026001");
        aluno.setAtivo(true);
        identify(aluno);
        return aluno;
    }

    public static Aluno alunoInativo() {
        var aluno = alunoAtivo();
        aluno.setAtivo(false);
        return aluno;
    }

    public static Disciplina disciplina() {
        var disciplina = new Disciplina();
        disciplina.setNome("Banco de Dados");
        disciplina.setCodigo("BD-01");
        disciplina.setCargaHoraria(60);
        disciplina.setAtivo(true);
        identify(disciplina);
        return disciplina;
    }

    public static Turma turmaAberta(int limiteVagas, int vagasOcupadas) {
        var turma = new Turma();
        turma.setCodigo("BD-2026-1A");
        turma.setDisciplina(disciplina());
        turma.setAno(2026);
        turma.setPeriodo("1");
        turma.setLimiteVagas(limiteVagas);
        turma.setVagasOcupadas(vagasOcupadas);
        turma.setStatus(StatusTurma.ABERTA);
        identify(turma);
        return turma;
    }

    public static Turma turmaFechada() {
        var turma = turmaAberta(40, 0);
        turma.setStatus(StatusTurma.FECHADA);
        return turma;
    }

    public static Matricula matricula(Aluno aluno, Turma turma, StatusMatricula status) {
        var matricula = new Matricula();
        matricula.setAluno(aluno);
        matricula.setTurma(turma);
        matricula.setStatus(status);
        matricula.setDataSolicitacao(Instant.parse("2026-01-10T12:00:00Z"));
        identify(matricula);
        return matricula;
    }

    public static void identify(Object entity) {
        ReflectionTestUtils.setField(entity, "id", UUID.randomUUID());
        var now = Instant.parse("2026-01-10T12:00:00Z");
        ReflectionTestUtils.setField(entity, "createdAt", now);
        ReflectionTestUtils.setField(entity, "updatedAt", now);
    }
}
