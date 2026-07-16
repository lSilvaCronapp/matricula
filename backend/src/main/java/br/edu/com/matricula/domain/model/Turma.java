package br.edu.com.matricula.domain.model;

import br.edu.com.matricula.domain.enums.StatusTurma;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.FetchType;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import jakarta.persistence.UniqueConstraint;
import jakarta.persistence.Version;

@Entity
@Table(
        name = "turmas",
        uniqueConstraints = @UniqueConstraint(name = "uk_turmas_codigo", columnNames = "codigo")
)
public class Turma extends BaseEntity {

    @Column(nullable = false, length = 40)
    private String codigo;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "disciplina_id", nullable = false)
    private Disciplina disciplina;

    @Column(nullable = false)
    private Integer ano;

    @Column(nullable = false, length = 2)
    private String periodo;

    @Column(nullable = false)
    private Integer limiteVagas;

    @Column(nullable = false)
    private Integer vagasOcupadas = 0;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 20)
    private StatusTurma status = StatusTurma.ABERTA;

    @Version
    private Long version;

    public boolean estaAberta() {
        return StatusTurma.ABERTA.equals(status);
    }

    public boolean possuiVagaDisponivel() {
        return vagasOcupadas < limiteVagas;
    }

    public void ocuparVaga() {
        vagasOcupadas++;
    }

    public void liberarVaga() {
        if (vagasOcupadas > 0) {
            vagasOcupadas--;
        }
    }

    public String getCodigo() {
        return codigo;
    }

    public void setCodigo(String codigo) {
        this.codigo = codigo;
    }

    public Disciplina getDisciplina() {
        return disciplina;
    }

    public void setDisciplina(Disciplina disciplina) {
        this.disciplina = disciplina;
    }

    public Integer getAno() {
        return ano;
    }

    public void setAno(Integer ano) {
        this.ano = ano;
    }

    public String getPeriodo() {
        return periodo;
    }

    public void setPeriodo(String periodo) {
        this.periodo = periodo;
    }

    public Integer getLimiteVagas() {
        return limiteVagas;
    }

    public void setLimiteVagas(Integer limiteVagas) {
        this.limiteVagas = limiteVagas;
    }

    public Integer getVagasOcupadas() {
        return vagasOcupadas;
    }

    public void setVagasOcupadas(Integer vagasOcupadas) {
        this.vagasOcupadas = vagasOcupadas;
    }

    public StatusTurma getStatus() {
        return status;
    }

    public void setStatus(StatusTurma status) {
        this.status = status;
    }

    public Long getVersion() {
        return version;
    }
}
