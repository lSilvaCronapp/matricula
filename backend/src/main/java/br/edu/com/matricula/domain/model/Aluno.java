package br.edu.com.matricula.domain.model;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Table;
import jakarta.persistence.UniqueConstraint;

import java.time.LocalDate;

@Entity
@Table(
        name = "alunos",
        uniqueConstraints = {
                @UniqueConstraint(name = "uk_alunos_email", columnNames = "email"),
                @UniqueConstraint(name = "uk_alunos_cpf", columnNames = "cpf"),
                @UniqueConstraint(name = "uk_alunos_matricula_academica", columnNames = "matricula_academica")
        }
)
public class Aluno extends BaseEntity {

    @Column(nullable = false, length = 120)
    private String nome;

    @Column(nullable = false, length = 150)
    private String email;

    @Column(nullable = false, length = 11)
    private String cpf;

    @Column(name = "matricula_academica", nullable = false, length = 30)
    private String matriculaAcademica;

    private LocalDate dataNascimento;

    @Column(nullable = false)
    private Boolean ativo = true;

    public String getNome() {
        return nome;
    }

    public void setNome(String nome) {
        this.nome = nome;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getCpf() {
        return cpf;
    }

    public void setCpf(String cpf) {
        this.cpf = cpf;
    }

    public String getMatriculaAcademica() {
        return matriculaAcademica;
    }

    public void setMatriculaAcademica(String matriculaAcademica) {
        this.matriculaAcademica = matriculaAcademica;
    }

    public LocalDate getDataNascimento() {
        return dataNascimento;
    }

    public void setDataNascimento(LocalDate dataNascimento) {
        this.dataNascimento = dataNascimento;
    }

    public Boolean getAtivo() {
        return ativo;
    }

    public void setAtivo(Boolean ativo) {
        this.ativo = ativo;
    }
}
