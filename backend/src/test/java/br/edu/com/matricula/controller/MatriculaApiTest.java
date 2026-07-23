package br.edu.com.matricula.controller;

import br.edu.com.matricula.domain.enums.StatusTurma;
import br.edu.com.matricula.domain.model.Aluno;
import br.edu.com.matricula.domain.model.Curso;
import br.edu.com.matricula.domain.model.Disciplina;
import br.edu.com.matricula.domain.model.Turma;
import br.edu.com.matricula.repository.AlunoRepository;
import br.edu.com.matricula.repository.CursoRepository;
import br.edu.com.matricula.repository.DisciplinaRepository;
import br.edu.com.matricula.repository.MatriculaRepository;
import br.edu.com.matricula.repository.TurmaRepository;
import br.edu.com.matricula.support.AbstractPostgresIntegrationTest;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;

import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.patch;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
class MatriculaApiTest extends AbstractPostgresIntegrationTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @Autowired
    private AlunoRepository alunoRepository;

    @Autowired
    private CursoRepository cursoRepository;

    @Autowired
    private DisciplinaRepository disciplinaRepository;

    @Autowired
    private TurmaRepository turmaRepository;

    @Autowired
    private MatriculaRepository matriculaRepository;

    private Aluno aluno;
    private Turma turma;

    @BeforeEach
    void setUp() {
        matriculaRepository.deleteAll();
        turmaRepository.deleteAll();
        disciplinaRepository.deleteAll();
        cursoRepository.deleteAll();
        alunoRepository.deleteAll();

        aluno = alunoRepository.save(novoAluno("Maria Silva", "maria@email.com", "12345678901", "RA2026001", true));
        turma = turmaRepository.save(novaTurma("BD-2026-1A", 2, 0, StatusTurma.ABERTA));
    }

    @Test
    void fluxoFeliz_criarConfirmarCancelar_ajustaVagas() throws Exception {
        var criar = postJson(aluno.getId(), turma.getId())
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.status").value("PENDENTE"))
                .andReturn();

        assertThat(recarregarTurma().getVagasOcupadas()).isZero();

        var matriculaId = idFrom(criar);

        mockMvc.perform(patch("/api/v1/matriculas/{id}/confirmar", matriculaId))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.status").value("CONFIRMADA"));

        assertThat(recarregarTurma().getVagasOcupadas()).isEqualTo(1);

        mockMvc.perform(patch("/api/v1/matriculas/{id}/cancelar", matriculaId))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.status").value("CANCELADA"));

        assertThat(recarregarTurma().getVagasOcupadas()).isZero();
    }

    @Test
    void criar_duplicataMesmoAlunoTurma_retorna409() throws Exception {
        postJson(aluno.getId(), turma.getId()).andExpect(status().isCreated());

        postJson(aluno.getId(), turma.getId())
                .andExpect(status().isConflict())
                .andExpect(jsonPath("$.status").value(409))
                .andExpect(jsonPath("$.message").value("Aluno já possui matrícula nesta turma"));
    }

    @Test
    void criar_alunoInativo_retorna409() throws Exception {
        var inativo = alunoRepository.save(
                novoAluno("João Inativo", "joao@email.com", "98765432100", "RA2026002", false)
        );

        postJson(inativo.getId(), turma.getId())
                .andExpect(status().isConflict())
                .andExpect(jsonPath("$.message").value("Aluno inativo não pode ser matriculado"));
    }

    @Test
    void confirmar_semVaga_retorna409() throws Exception {
        turma.setLimiteVagas(1);
        turma.setVagasOcupadas(1);
        turmaRepository.save(turma);

        var criar = postJson(aluno.getId(), turma.getId())
                .andExpect(status().isCreated())
                .andReturn();

        mockMvc.perform(patch("/api/v1/matriculas/{id}/confirmar", idFrom(criar)))
                .andExpect(status().isConflict())
                .andExpect(jsonPath("$.message").value("Turma sem vagas disponíveis"));
    }

    @Test
    void criar_semAlunoId_retorna400() throws Exception {
        mockMvc.perform(
                        post("/api/v1/matriculas")
                                .contentType(MediaType.APPLICATION_JSON)
                                .content("""
                                        {"turmaId":"%s"}
                                        """.formatted(turma.getId()))
                )
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.status").value(400));
    }

    private org.springframework.test.web.servlet.ResultActions postJson(UUID alunoId, UUID turmaId) throws Exception {
        return mockMvc.perform(
                post("/api/v1/matriculas")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                {"alunoId":"%s","turmaId":"%s"}
                                """.formatted(alunoId, turmaId))
        );
    }

    private UUID idFrom(MvcResult result) throws Exception {
        JsonNode node = objectMapper.readTree(result.getResponse().getContentAsString());
        return UUID.fromString(node.get("id").asText());
    }

    private Turma recarregarTurma() {
        return turmaRepository.findById(turma.getId()).orElseThrow();
    }

    private Aluno novoAluno(String nome, String email, String cpf, String ra, boolean ativo) {
        var entity = new Aluno();
        entity.setNome(nome);
        entity.setEmail(email);
        entity.setCpf(cpf);
        entity.setMatriculaAcademica(ra);
        entity.setAtivo(ativo);
        return entity;
    }

    private Turma novaTurma(String codigo, int limite, int ocupadas, StatusTurma status) {
        var curso = new Curso();
        curso.setNome("Engenharia de Software");
        curso.setCodigo("ENG-SOFT-" + UUID.randomUUID().toString().substring(0, 8));
        curso.setAtivo(true);
        curso = cursoRepository.save(curso);

        var disciplina = new Disciplina();
        disciplina.setNome("Banco de Dados");
        disciplina.setCodigo("BD-" + UUID.randomUUID().toString().substring(0, 8));
        disciplina.setCargaHoraria(60);
        disciplina.setCurso(curso);
        disciplina.setAtivo(true);
        disciplina = disciplinaRepository.save(disciplina);

        var entity = new Turma();
        entity.setCodigo(codigo);
        entity.setDisciplina(disciplina);
        entity.setAno(2026);
        entity.setPeriodo("1");
        entity.setLimiteVagas(limite);
        entity.setVagasOcupadas(ocupadas);
        entity.setStatus(status);
        return entity;
    }
}
