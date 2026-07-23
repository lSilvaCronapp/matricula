package br.edu.com.matricula.service;

import br.edu.com.matricula.domain.model.Disciplina;
import br.edu.com.matricula.dto.request.DisciplinaRequest;
import br.edu.com.matricula.dto.response.DisciplinaResponse;
import br.edu.com.matricula.dto.response.PageResponse;
import br.edu.com.matricula.exception.BusinessRuleException;
import br.edu.com.matricula.exception.ResourceNotFoundException;
import br.edu.com.matricula.mapper.DisciplinaMapper;
import br.edu.com.matricula.repository.CursoRepository;
import br.edu.com.matricula.repository.DisciplinaRepository;
import br.edu.com.matricula.repository.TurmaRepository;
import br.edu.com.matricula.repository.spec.EntitySpecs;
import br.edu.com.matricula.util.SearchUtils;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.UUID;

@Service
public class DisciplinaService {

    private final DisciplinaRepository disciplinaRepository;
    private final CursoRepository cursoRepository;
    private final TurmaRepository turmaRepository;

    public DisciplinaService(
            DisciplinaRepository disciplinaRepository,
            CursoRepository cursoRepository,
            TurmaRepository turmaRepository
    ) {
        this.disciplinaRepository = disciplinaRepository;
        this.cursoRepository = cursoRepository;
        this.turmaRepository = turmaRepository;
    }

    @Transactional
    public DisciplinaResponse criar(DisciplinaRequest request) {
        validarCodigoUnico(request.codigo(), null);
        var curso = cursoRepository.findById(request.cursoId())
                .orElseThrow(() -> new ResourceNotFoundException("Curso não encontrado"));
        return DisciplinaMapper.toResponse(disciplinaRepository.save(DisciplinaMapper.toEntity(request, curso)));
    }

    @Transactional(readOnly = true)
    public PageResponse<DisciplinaResponse> listar(String q, Pageable pageable) {
        var page = disciplinaRepository
                .findAll(EntitySpecs.disciplinaComBusca(SearchUtils.normalize(q)), pageable)
                .map(DisciplinaMapper::toResponse);
        return PageResponse.from(page);
    }

    @Transactional(readOnly = true)
    public DisciplinaResponse buscarPorId(UUID id) {
        return DisciplinaMapper.toResponse(buscarEntidade(id));
    }

    @Transactional
    public DisciplinaResponse atualizar(UUID id, DisciplinaRequest request) {
        var disciplina = buscarEntidade(id);
        validarCodigoUnico(request.codigo(), id);
        var curso = cursoRepository.findById(request.cursoId())
                .orElseThrow(() -> new ResourceNotFoundException("Curso não encontrado"));
        DisciplinaMapper.apply(request, disciplina, curso);
        return DisciplinaMapper.toResponse(disciplina);
    }

    @Transactional
    public void excluir(UUID id) {
        var disciplina = buscarEntidade(id);
        if (turmaRepository.existsByDisciplinaId(id)) {
            throw new BusinessRuleException("Disciplina possui turmas vinculadas e não pode ser excluída");
        }
        disciplinaRepository.delete(disciplina);
    }

    Disciplina buscarEntidade(UUID id) {
        return disciplinaRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Disciplina não encontrada"));
    }

    private void validarCodigoUnico(String codigo, UUID idAtual) {
        disciplinaRepository.findByCodigo(codigo.trim().toUpperCase())
                .filter(disciplina -> !disciplina.getId().equals(idAtual))
                .ifPresent(disciplina -> {
                    throw new BusinessRuleException("Código já cadastrado para outra disciplina");
                });
    }
}
