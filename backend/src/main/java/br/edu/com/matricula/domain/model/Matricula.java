package br.edu.com.matricula.domain.model;

import br.edu.com.matricula.domain.enums.StatusMatricula;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.FetchType;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import jakarta.persistence.UniqueConstraint;

import java.time.Instant;

@Entity
@Table(
        name = "matriculas",
        uniqueConstraints = @UniqueConstraint(
                name = "uk_matriculas_aluno_turma",
                columnNames = {"aluno_id", "turma_id"}
        )
)
public class Matricula extends BaseEntity {

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "aluno_id", nullable = false)
    private Aluno aluno;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "turma_id", nullable = false)
    private Turma turma;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 20)
    private StatusMatricula status = StatusMatricula.PENDENTE;

    @Column(nullable = false)
    private Instant dataSolicitacao;

    private Instant dataConfirmacao;

    private Instant dataCancelamento;

    public Aluno getAluno() {
        return aluno;
    }

    public void setAluno(Aluno aluno) {
        this.aluno = aluno;
    }

    public Turma getTurma() {
        return turma;
    }

    public void setTurma(Turma turma) {
        this.turma = turma;
    }

    public StatusMatricula getStatus() {
        return status;
    }

    public void setStatus(StatusMatricula status) {
        this.status = status;
    }

    public Instant getDataSolicitacao() {
        return dataSolicitacao;
    }

    public void setDataSolicitacao(Instant dataSolicitacao) {
        this.dataSolicitacao = dataSolicitacao;
    }

    public Instant getDataConfirmacao() {
        return dataConfirmacao;
    }

    public void setDataConfirmacao(Instant dataConfirmacao) {
        this.dataConfirmacao = dataConfirmacao;
    }

    public Instant getDataCancelamento() {
        return dataCancelamento;
    }

    public void setDataCancelamento(Instant dataCancelamento) {
        this.dataCancelamento = dataCancelamento;
    }
}
