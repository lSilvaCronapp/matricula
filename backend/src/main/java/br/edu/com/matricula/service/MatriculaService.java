package br.edu.com.matricula.service;

import br.edu.com.matricula.domain.enums.StatusMatricula;
import br.edu.com.matricula.dto.request.MatriculaCreateRequest;
import br.edu.com.matricula.dto.response.MatriculaResponse;
import br.edu.com.matricula.exception.BusinessRuleException;
import br.edu.com.matricula.exception.ResourceNotFoundException;
import br.edu.com.matricula.mapper.MatriculaMapper;
import br.edu.com.matricula.repository.MatriculaRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Instant;
import java.util.List;
import java.util.UUID;

@Service
public class MatriculaService {

    private final MatriculaRepository matriculaRepository;
    private final AlunoService alunoService;
    private final TurmaService turmaService;

    public MatriculaService(
            MatriculaRepository matriculaRepository,
            AlunoService alunoService,
            TurmaService turmaService
    ) {
        this.matriculaRepository = matriculaRepository;
        this.alunoService = alunoService;
        this.turmaService = turmaService;
    }

    @Transactional
    public MatriculaResponse criar(MatriculaCreateRequest request) {
        var aluno = alunoService.buscarEntidade(request.alunoId());
        var turma = turmaService.buscarEntidade(request.turmaId());

        if (!Boolean.TRUE.equals(aluno.getAtivo())) {
            throw new BusinessRuleException("Aluno inativo não pode ser matriculado");
        }
        if (!turma.estaAberta()) {
            throw new BusinessRuleException("Aluno só pode ser matriculado em turma aberta");
        }
        if (matriculaRepository.existsByAlunoIdAndTurmaId(aluno.getId(), turma.getId())) {
            throw new BusinessRuleException("Aluno já possui matrícula nesta turma");
        }

        var matricula = new br.edu.com.matricula.domain.model.Matricula();
        matricula.setAluno(aluno);
        matricula.setTurma(turma);
        matricula.setStatus(StatusMatricula.PENDENTE);
        matricula.setDataSolicitacao(Instant.now());

        return MatriculaMapper.toResponse(matriculaRepository.save(matricula));
    }

    @Transactional(readOnly = true)
    public MatriculaResponse buscarPorId(UUID id) {
        return MatriculaMapper.toResponse(buscarEntidade(id));
    }

    @Transactional
    public MatriculaResponse confirmar(UUID id) {
        var matricula = buscarEntidade(id);
        if (!StatusMatricula.PENDENTE.equals(matricula.getStatus())) {
            throw new BusinessRuleException("Apenas matrículas pendentes podem ser confirmadas");
        }

        var turma = matricula.getTurma();
        if (!turma.estaAberta()) {
            throw new BusinessRuleException("Não é possível confirmar matrícula em turma fechada");
        }
        if (!turma.possuiVagaDisponivel()) {
            throw new BusinessRuleException("Turma sem vagas disponíveis");
        }

        turma.ocuparVaga();
        matricula.setStatus(StatusMatricula.CONFIRMADA);
        matricula.setDataConfirmacao(Instant.now());
        return MatriculaMapper.toResponse(matricula);
    }

    @Transactional
    public MatriculaResponse cancelar(UUID id) {
        var matricula = buscarEntidade(id);
        if (StatusMatricula.CANCELADA.equals(matricula.getStatus())) {
            throw new BusinessRuleException("Matrícula já está cancelada");
        }

        if (StatusMatricula.CONFIRMADA.equals(matricula.getStatus())) {
            matricula.getTurma().liberarVaga();
        }

        matricula.setStatus(StatusMatricula.CANCELADA);
        matricula.setDataCancelamento(Instant.now());
        return MatriculaMapper.toResponse(matricula);
    }

    @Transactional(readOnly = true)
    public List<MatriculaResponse> buscarPorAluno(UUID alunoId) {
        alunoService.buscarEntidade(alunoId);
        return matriculaRepository.findByAlunoId(alunoId).stream()
                .map(MatriculaMapper::toResponse)
                .toList();
    }

    @Transactional(readOnly = true)
    public List<MatriculaResponse> buscarPorTurma(UUID turmaId) {
        turmaService.buscarEntidade(turmaId);
        return matriculaRepository.findByTurmaId(turmaId).stream()
                .map(MatriculaMapper::toResponse)
                .toList();
    }

    private br.edu.com.matricula.domain.model.Matricula buscarEntidade(UUID id) {
        return matriculaRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Matrícula não encontrada"));
    }
}
