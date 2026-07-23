package br.edu.com.matricula.repository.spec;

import br.edu.com.matricula.domain.enums.StatusTurma;
import br.edu.com.matricula.domain.model.Aluno;
import br.edu.com.matricula.domain.model.Curso;
import br.edu.com.matricula.domain.model.Disciplina;
import br.edu.com.matricula.domain.model.Matricula;
import br.edu.com.matricula.domain.model.Turma;
import org.springframework.data.jpa.domain.Specification;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

public final class EntitySpecs {

    private EntitySpecs() {
    }

    public static Specification<Aluno> alunoComBusca(String q) {
        return (root, query, cb) -> {
            if (q == null) {
                return cb.conjunction();
            }
            String pattern = like(q);
            return cb.or(
                    cb.like(cb.lower(root.get("nome")), pattern),
                    cb.like(cb.lower(root.get("email")), pattern),
                    cb.like(cb.lower(root.get("cpf")), pattern),
                    cb.like(cb.lower(root.get("matriculaAcademica")), pattern)
            );
        };
    }

    public static Specification<Curso> cursoComBusca(String q) {
        return (root, query, cb) -> {
            if (q == null) {
                return cb.conjunction();
            }
            String pattern = like(q);
            return cb.or(
                    cb.like(cb.lower(root.get("nome")), pattern),
                    cb.like(cb.lower(root.get("codigo")), pattern)
            );
        };
    }

    public static Specification<Disciplina> disciplinaComBusca(String q) {
        return (root, query, cb) -> {
            if (q == null) {
                return cb.conjunction();
            }
            String pattern = like(q);
            return cb.or(
                    cb.like(cb.lower(root.get("nome")), pattern),
                    cb.like(cb.lower(root.get("codigo")), pattern)
            );
        };
    }

    public static Specification<Turma> turmaComFiltros(StatusTurma status, String q) {
        return (root, query, cb) -> {
            List<jakarta.persistence.criteria.Predicate> predicates = new ArrayList<>();
            if (status != null) {
                predicates.add(cb.equal(root.get("status"), status));
            }
            if (q != null) {
                predicates.add(cb.like(cb.lower(root.get("codigo")), like(q)));
            }
            if (predicates.isEmpty()) {
                return cb.conjunction();
            }
            return cb.and(predicates.toArray(jakarta.persistence.criteria.Predicate[]::new));
        };
    }

    public static Specification<Matricula> matriculaPorAluno(UUID alunoId, String q) {
        return (root, query, cb) -> {
            var predicates = new ArrayList<jakarta.persistence.criteria.Predicate>();
            predicates.add(cb.equal(root.get("aluno").get("id"), alunoId));
            if (q != null) {
                var turma = root.join("turma");
                var disciplina = turma.join("disciplina");
                String pattern = like(q);
                predicates.add(cb.or(
                        cb.like(cb.lower(turma.get("codigo")), pattern),
                        cb.like(cb.lower(disciplina.get("nome")), pattern)
                ));
                query.distinct(true);
            }
            return cb.and(predicates.toArray(jakarta.persistence.criteria.Predicate[]::new));
        };
    }

    public static Specification<Matricula> matriculaPorTurma(UUID turmaId, String q) {
        return (root, query, cb) -> {
            var predicates = new ArrayList<jakarta.persistence.criteria.Predicate>();
            predicates.add(cb.equal(root.get("turma").get("id"), turmaId));
            if (q != null) {
                var aluno = root.join("aluno");
                predicates.add(cb.like(cb.lower(aluno.get("nome")), like(q)));
                query.distinct(true);
            }
            return cb.and(predicates.toArray(jakarta.persistence.criteria.Predicate[]::new));
        };
    }

    private static String like(String q) {
        return "%" + q.toLowerCase() + "%";
    }
}
