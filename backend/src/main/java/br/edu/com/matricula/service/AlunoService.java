package br.edu.com.matricula.service;

import br.edu.com.matricula.domain.model.Aluno;
import br.edu.com.matricula.dto.request.AlunoRequest;
import br.edu.com.matricula.dto.response.AlunoResponse;
import br.edu.com.matricula.dto.response.PageResponse;
import br.edu.com.matricula.exception.BusinessRuleException;
import br.edu.com.matricula.exception.ResourceNotFoundException;
import br.edu.com.matricula.mapper.AlunoMapper;
import br.edu.com.matricula.repository.AlunoRepository;
import br.edu.com.matricula.repository.MatriculaRepository;
import br.edu.com.matricula.repository.spec.EntitySpecs;
import br.edu.com.matricula.util.SearchUtils;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.UUID;

@Service
public class AlunoService {

    private final AlunoRepository alunoRepository;
    private final MatriculaRepository matriculaRepository;

    public AlunoService(AlunoRepository alunoRepository, MatriculaRepository matriculaRepository) {
        this.alunoRepository = alunoRepository;
        this.matriculaRepository = matriculaRepository;
    }

    @Transactional
    public AlunoResponse criar(AlunoRequest request) {
        validarUnicidade(request, null);
        return AlunoMapper.toResponse(alunoRepository.save(AlunoMapper.toEntity(request)));
    }

    @Transactional(readOnly = true)
    public PageResponse<AlunoResponse> listar(String q, Pageable pageable) {
        var page = alunoRepository
                .findAll(EntitySpecs.alunoComBusca(SearchUtils.normalize(q)), pageable)
                .map(AlunoMapper::toResponse);
        return PageResponse.from(page);
    }

    @Transactional(readOnly = true)
    public AlunoResponse buscarPorId(UUID id) {
        return AlunoMapper.toResponse(buscarEntidade(id));
    }

    @Transactional
    public AlunoResponse atualizar(UUID id, AlunoRequest request) {
        var aluno = buscarEntidade(id);
        validarUnicidade(request, id);
        AlunoMapper.apply(request, aluno);
        return AlunoMapper.toResponse(aluno);
    }

    @Transactional
    public void excluir(UUID id) {
        var aluno = buscarEntidade(id);
        if (matriculaRepository.existsByAlunoId(id)) {
            throw new BusinessRuleException("Aluno possui matrículas vinculadas e não pode ser excluído");
        }
        alunoRepository.delete(aluno);
    }

    Aluno buscarEntidade(UUID id) {
        return alunoRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Aluno não encontrado"));
    }

    private void validarUnicidade(AlunoRequest request, UUID idAtual) {
        alunoRepository.findByEmail(request.email().trim().toLowerCase())
                .filter(aluno -> !aluno.getId().equals(idAtual))
                .ifPresent(aluno -> {
                    throw new BusinessRuleException("E-mail já cadastrado para outro aluno");
                });

        alunoRepository.findByCpf(request.cpf().trim())
                .filter(aluno -> !aluno.getId().equals(idAtual))
                .ifPresent(aluno -> {
                    throw new BusinessRuleException("CPF já cadastrado para outro aluno");
                });

        alunoRepository.findByMatriculaAcademica(request.matriculaAcademica().trim())
                .filter(aluno -> !aluno.getId().equals(idAtual))
                .ifPresent(aluno -> {
                    throw new BusinessRuleException("Matrícula acadêmica já cadastrada para outro aluno");
                });
    }
}
