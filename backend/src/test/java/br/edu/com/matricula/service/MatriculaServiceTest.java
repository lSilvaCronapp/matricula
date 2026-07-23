package br.edu.com.matricula.service;

import br.edu.com.matricula.domain.enums.StatusMatricula;
import br.edu.com.matricula.dto.request.MatriculaCreateRequest;
import br.edu.com.matricula.exception.BusinessRuleException;
import br.edu.com.matricula.exception.ResourceNotFoundException;
import br.edu.com.matricula.repository.MatriculaRepository;
import br.edu.com.matricula.support.TestEntityFactory;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Optional;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class MatriculaServiceTest {

    @Mock
    private MatriculaRepository matriculaRepository;

    @Mock
    private AlunoService alunoService;

    @Mock
    private TurmaService turmaService;

    @InjectMocks
    private MatriculaService matriculaService;

    @Test
    void criar_comAlunoAtivoETurmaAberta_criaComoPendenteSemConsumirVaga() {
        var aluno = TestEntityFactory.alunoAtivo();
        var turma = TestEntityFactory.turmaAberta(40, 5);
        when(alunoService.buscarEntidade(aluno.getId())).thenReturn(aluno);
        when(turmaService.buscarEntidade(turma.getId())).thenReturn(turma);
        when(matriculaRepository.existsByAlunoIdAndTurmaId(aluno.getId(), turma.getId())).thenReturn(false);
        when(matriculaRepository.save(any())).thenAnswer(invocation -> {
            var matricula = invocation.getArgument(0, br.edu.com.matricula.domain.model.Matricula.class);
            TestEntityFactory.identify(matricula);
            return matricula;
        });

        var response = matriculaService.criar(new MatriculaCreateRequest(aluno.getId(), turma.getId()));

        assertThat(response.status()).isEqualTo(StatusMatricula.PENDENTE);
        assertThat(response.dataSolicitacao()).isNotNull();
        assertThat(turma.getVagasOcupadas()).isEqualTo(5);

        var captor = ArgumentCaptor.forClass(br.edu.com.matricula.domain.model.Matricula.class);
        verify(matriculaRepository).save(captor.capture());
        assertThat(captor.getValue().getStatus()).isEqualTo(StatusMatricula.PENDENTE);
    }

    @Test
    void criar_comAlunoInativo_lancaConflito() {
        var aluno = TestEntityFactory.alunoInativo();
        var turma = TestEntityFactory.turmaAberta(40, 0);
        when(alunoService.buscarEntidade(aluno.getId())).thenReturn(aluno);
        when(turmaService.buscarEntidade(turma.getId())).thenReturn(turma);

        assertThatThrownBy(() -> matriculaService.criar(new MatriculaCreateRequest(aluno.getId(), turma.getId())))
                .isInstanceOf(BusinessRuleException.class)
                .hasMessage("Aluno inativo não pode ser matriculado");

        verify(matriculaRepository, never()).save(any());
    }

    @Test
    void criar_comTurmaFechada_lancaConflito() {
        var aluno = TestEntityFactory.alunoAtivo();
        var turma = TestEntityFactory.turmaFechada();
        when(alunoService.buscarEntidade(aluno.getId())).thenReturn(aluno);
        when(turmaService.buscarEntidade(turma.getId())).thenReturn(turma);

        assertThatThrownBy(() -> matriculaService.criar(new MatriculaCreateRequest(aluno.getId(), turma.getId())))
                .isInstanceOf(BusinessRuleException.class)
                .hasMessage("Aluno só pode ser matriculado em turma aberta");
    }

    @Test
    void criar_quandoJaExisteMatriculaNaTurma_lancaConflito() {
        var aluno = TestEntityFactory.alunoAtivo();
        var turma = TestEntityFactory.turmaAberta(40, 0);
        when(alunoService.buscarEntidade(aluno.getId())).thenReturn(aluno);
        when(turmaService.buscarEntidade(turma.getId())).thenReturn(turma);
        when(matriculaRepository.existsByAlunoIdAndTurmaId(aluno.getId(), turma.getId())).thenReturn(true);

        assertThatThrownBy(() -> matriculaService.criar(new MatriculaCreateRequest(aluno.getId(), turma.getId())))
                .isInstanceOf(BusinessRuleException.class)
                .hasMessage("Aluno já possui matrícula nesta turma");
    }

    @Test
    void criar_quandoAlunoNaoExiste_propagaNotFound() {
        var alunoId = UUID.randomUUID();
        var turmaId = UUID.randomUUID();
        when(alunoService.buscarEntidade(alunoId)).thenThrow(new ResourceNotFoundException("Aluno não encontrado"));

        assertThatThrownBy(() -> matriculaService.criar(new MatriculaCreateRequest(alunoId, turmaId)))
                .isInstanceOf(ResourceNotFoundException.class)
                .hasMessage("Aluno não encontrado");
    }

    @Test
    void confirmar_pendenteComVaga_confirmaEOcupaVaga() {
        var aluno = TestEntityFactory.alunoAtivo();
        var turma = TestEntityFactory.turmaAberta(40, 3);
        var matricula = TestEntityFactory.matricula(aluno, turma, StatusMatricula.PENDENTE);
        when(matriculaRepository.findById(matricula.getId())).thenReturn(Optional.of(matricula));

        var response = matriculaService.confirmar(matricula.getId());

        assertThat(response.status()).isEqualTo(StatusMatricula.CONFIRMADA);
        assertThat(response.dataConfirmacao()).isNotNull();
        assertThat(turma.getVagasOcupadas()).isEqualTo(4);
        assertThat(matricula.getStatus()).isEqualTo(StatusMatricula.CONFIRMADA);
    }

    @Test
    void confirmar_quandoNaoEstaPendente_lancaConflito() {
        var aluno = TestEntityFactory.alunoAtivo();
        var turma = TestEntityFactory.turmaAberta(40, 0);
        var matricula = TestEntityFactory.matricula(aluno, turma, StatusMatricula.CONFIRMADA);
        when(matriculaRepository.findById(matricula.getId())).thenReturn(Optional.of(matricula));

        assertThatThrownBy(() -> matriculaService.confirmar(matricula.getId()))
                .isInstanceOf(BusinessRuleException.class)
                .hasMessage("Apenas matrículas pendentes podem ser confirmadas");
    }

    @Test
    void confirmar_comTurmaFechada_lancaConflito() {
        var aluno = TestEntityFactory.alunoAtivo();
        var turma = TestEntityFactory.turmaFechada();
        var matricula = TestEntityFactory.matricula(aluno, turma, StatusMatricula.PENDENTE);
        when(matriculaRepository.findById(matricula.getId())).thenReturn(Optional.of(matricula));

        assertThatThrownBy(() -> matriculaService.confirmar(matricula.getId()))
                .isInstanceOf(BusinessRuleException.class)
                .hasMessage("Não é possível confirmar matrícula em turma fechada");
    }

    @Test
    void confirmar_semVaga_lancaConflito() {
        var aluno = TestEntityFactory.alunoAtivo();
        var turma = TestEntityFactory.turmaAberta(2, 2);
        var matricula = TestEntityFactory.matricula(aluno, turma, StatusMatricula.PENDENTE);
        when(matriculaRepository.findById(matricula.getId())).thenReturn(Optional.of(matricula));

        assertThatThrownBy(() -> matriculaService.confirmar(matricula.getId()))
                .isInstanceOf(BusinessRuleException.class)
                .hasMessage("Turma sem vagas disponíveis");
        assertThat(turma.getVagasOcupadas()).isEqualTo(2);
    }

    @Test
    void confirmar_quandoMatriculaNaoExiste_lancaNotFound() {
        var id = UUID.randomUUID();
        when(matriculaRepository.findById(id)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> matriculaService.confirmar(id))
                .isInstanceOf(ResourceNotFoundException.class)
                .hasMessage("Matrícula não encontrada");
    }

    @Test
    void cancelar_pendente_cancelaSemAlterarVagas() {
        var aluno = TestEntityFactory.alunoAtivo();
        var turma = TestEntityFactory.turmaAberta(40, 5);
        var matricula = TestEntityFactory.matricula(aluno, turma, StatusMatricula.PENDENTE);
        when(matriculaRepository.findById(matricula.getId())).thenReturn(Optional.of(matricula));

        var response = matriculaService.cancelar(matricula.getId());

        assertThat(response.status()).isEqualTo(StatusMatricula.CANCELADA);
        assertThat(response.dataCancelamento()).isNotNull();
        assertThat(turma.getVagasOcupadas()).isEqualTo(5);
    }

    @Test
    void cancelar_confirmada_cancelaELiberaVaga() {
        var aluno = TestEntityFactory.alunoAtivo();
        var turma = TestEntityFactory.turmaAberta(40, 5);
        var matricula = TestEntityFactory.matricula(aluno, turma, StatusMatricula.CONFIRMADA);
        when(matriculaRepository.findById(matricula.getId())).thenReturn(Optional.of(matricula));

        var response = matriculaService.cancelar(matricula.getId());

        assertThat(response.status()).isEqualTo(StatusMatricula.CANCELADA);
        assertThat(turma.getVagasOcupadas()).isEqualTo(4);
    }

    @Test
    void cancelar_jaCancelada_lancaConflito() {
        var aluno = TestEntityFactory.alunoAtivo();
        var turma = TestEntityFactory.turmaAberta(40, 0);
        var matricula = TestEntityFactory.matricula(aluno, turma, StatusMatricula.CANCELADA);
        when(matriculaRepository.findById(matricula.getId())).thenReturn(Optional.of(matricula));

        assertThatThrownBy(() -> matriculaService.cancelar(matricula.getId()))
                .isInstanceOf(BusinessRuleException.class)
                .hasMessage("Matrícula já está cancelada");
    }
}
