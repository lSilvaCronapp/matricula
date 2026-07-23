package br.edu.com.matricula.service;

import br.edu.com.matricula.domain.enums.StatusTurma;
import br.edu.com.matricula.domain.model.Turma;
import br.edu.com.matricula.dto.request.TurmaRequest;
import br.edu.com.matricula.dto.response.PageResponse;
import br.edu.com.matricula.dto.response.TurmaResponse;
import br.edu.com.matricula.exception.BusinessRuleException;
import br.edu.com.matricula.exception.ResourceNotFoundException;
import br.edu.com.matricula.mapper.TurmaMapper;
import br.edu.com.matricula.repository.DisciplinaRepository;
import br.edu.com.matricula.repository.MatriculaRepository;
import br.edu.com.matricula.repository.TurmaRepository;
import br.edu.com.matricula.repository.spec.EntitySpecs;
import br.edu.com.matricula.util.SearchUtils;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.UUID;

@Service
public class TurmaService {

    private final TurmaRepository turmaRepository;
    private final DisciplinaRepository disciplinaRepository;
    private final MatriculaRepository matriculaRepository;

    public TurmaService(
            TurmaRepository turmaRepository,
            DisciplinaRepository disciplinaRepository,
            MatriculaRepository matriculaRepository
    ) {
        this.turmaRepository = turmaRepository;
        this.disciplinaRepository = disciplinaRepository;
        this.matriculaRepository = matriculaRepository;
    }

    @Transactional
    public TurmaResponse criar(TurmaRequest request) {
        validarCodigoUnico(request.codigo(), null);
        var disciplina = disciplinaRepository.findById(request.disciplinaId())
                .orElseThrow(() -> new ResourceNotFoundException("Disciplina não encontrada"));
        return TurmaMapper.toResponse(turmaRepository.save(TurmaMapper.toEntity(request, disciplina)));
    }

    @Transactional(readOnly = true)
    public PageResponse<TurmaResponse> listar(StatusTurma status, String q, Pageable pageable) {
        var page = turmaRepository
                .findAll(EntitySpecs.turmaComFiltros(status, SearchUtils.normalize(q)), pageable)
                .map(TurmaMapper::toResponse);
        return PageResponse.from(page);
    }

    @Transactional(readOnly = true)
    public TurmaResponse buscarPorId(UUID id) {
        return TurmaMapper.toResponse(buscarEntidade(id));
    }

    @Transactional
    public TurmaResponse atualizar(UUID id, TurmaRequest request) {
        var turma = buscarEntidade(id);
        validarCodigoUnico(request.codigo(), id);
        if (request.limiteVagas() < turma.getVagasOcupadas()) {
            throw new BusinessRuleException("Limite de vagas não pode ser menor que as vagas ocupadas");
        }
        var disciplina = disciplinaRepository.findById(request.disciplinaId())
                .orElseThrow(() -> new ResourceNotFoundException("Disciplina não encontrada"));
        TurmaMapper.apply(request, turma, disciplina);
        return TurmaMapper.toResponse(turma);
    }

    @Transactional
    public void excluir(UUID id) {
        var turma = buscarEntidade(id);
        if (matriculaRepository.existsByTurmaId(id)) {
            throw new BusinessRuleException("Turma possui matrículas vinculadas e não pode ser excluída");
        }
        turmaRepository.delete(turma);
    }

    Turma buscarEntidade(UUID id) {
        return turmaRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Turma não encontrada"));
    }

    private void validarCodigoUnico(String codigo, UUID idAtual) {
        turmaRepository.findByCodigo(codigo.trim().toUpperCase())
                .filter(turma -> !turma.getId().equals(idAtual))
                .ifPresent(turma -> {
                    throw new BusinessRuleException("Código já cadastrado para outra turma");
                });
    }
}
