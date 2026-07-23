package br.edu.com.matricula.service;

import br.edu.com.matricula.domain.enums.StatusMatricula;
import br.edu.com.matricula.dto.request.MatriculaCreateRequest;
import br.edu.com.matricula.dto.response.MatriculaResponse;
import br.edu.com.matricula.dto.response.PageResponse;
import br.edu.com.matricula.exception.BusinessRuleException;
import br.edu.com.matricula.exception.ResourceNotFoundException;
import br.edu.com.matricula.mapper.MatriculaMapper;
import br.edu.com.matricula.repository.MatriculaRepository;
import br.edu.com.matricula.repository.spec.EntitySpecs;
import br.edu.com.matricula.util.SearchUtils;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Instant;
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
    public PageResponse<MatriculaResponse> buscarPorAluno(UUID alunoId, String q, Pageable pageable) {
        alunoService.buscarEntidade(alunoId);
        var page = matriculaRepository
                .findAll(EntitySpecs.matriculaPorAluno(alunoId, SearchUtils.normalize(q)), pageable)
                .map(MatriculaMapper::toResponse);
        return PageResponse.from(page);
    }

    @Transactional(readOnly = true)
    public PageResponse<MatriculaResponse> buscarPorTurma(UUID turmaId, String q, Pageable pageable) {
        turmaService.buscarEntidade(turmaId);
        var page = matriculaRepository
                .findAll(EntitySpecs.matriculaPorTurma(turmaId, SearchUtils.normalize(q)), pageable)
                .map(MatriculaMapper::toResponse);
        return PageResponse.from(page);
    }

    private br.edu.com.matricula.domain.model.Matricula buscarEntidade(UUID id) {
        return matriculaRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Matrícula não encontrada"));
    }
}
