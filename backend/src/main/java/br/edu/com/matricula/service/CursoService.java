package br.edu.com.matricula.service;

import br.edu.com.matricula.domain.model.Curso;
import br.edu.com.matricula.dto.request.CursoRequest;
import br.edu.com.matricula.dto.response.CursoResponse;
import br.edu.com.matricula.dto.response.PageResponse;
import br.edu.com.matricula.exception.BusinessRuleException;
import br.edu.com.matricula.exception.ResourceNotFoundException;
import br.edu.com.matricula.mapper.CursoMapper;
import br.edu.com.matricula.repository.CursoRepository;
import br.edu.com.matricula.repository.DisciplinaRepository;
import br.edu.com.matricula.repository.spec.EntitySpecs;
import br.edu.com.matricula.util.SearchUtils;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.UUID;

@Service
public class CursoService {

    private final CursoRepository cursoRepository;
    private final DisciplinaRepository disciplinaRepository;

    public CursoService(CursoRepository cursoRepository, DisciplinaRepository disciplinaRepository) {
        this.cursoRepository = cursoRepository;
        this.disciplinaRepository = disciplinaRepository;
    }

    @Transactional
    public CursoResponse criar(CursoRequest request) {
        validarCodigoUnico(request.codigo(), null);
        return CursoMapper.toResponse(cursoRepository.save(CursoMapper.toEntity(request)));
    }

    @Transactional(readOnly = true)
    public PageResponse<CursoResponse> listar(String q, Pageable pageable) {
        var page = cursoRepository
                .findAll(EntitySpecs.cursoComBusca(SearchUtils.normalize(q)), pageable)
                .map(CursoMapper::toResponse);
        return PageResponse.from(page);
    }

    @Transactional(readOnly = true)
    public CursoResponse buscarPorId(UUID id) {
        return CursoMapper.toResponse(buscarEntidade(id));
    }

    @Transactional
    public CursoResponse atualizar(UUID id, CursoRequest request) {
        var curso = buscarEntidade(id);
        validarCodigoUnico(request.codigo(), id);
        CursoMapper.apply(request, curso);
        return CursoMapper.toResponse(curso);
    }

    @Transactional
    public void excluir(UUID id) {
        var curso = buscarEntidade(id);
        if (disciplinaRepository.existsByCursoId(id)) {
            throw new BusinessRuleException("Curso possui disciplinas vinculadas e não pode ser excluído");
        }
        cursoRepository.delete(curso);
    }

    Curso buscarEntidade(UUID id) {
        return cursoRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Curso não encontrado"));
    }

    private void validarCodigoUnico(String codigo, UUID idAtual) {
        cursoRepository.findByCodigo(codigo.trim().toUpperCase())
                .filter(curso -> !curso.getId().equals(idAtual))
                .ifPresent(curso -> {
                    throw new BusinessRuleException("Código já cadastrado para outro curso");
                });
    }
}
