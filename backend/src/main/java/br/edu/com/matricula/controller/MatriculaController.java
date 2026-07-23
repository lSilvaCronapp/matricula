package br.edu.com.matricula.controller;

import br.edu.com.matricula.dto.request.MatriculaCreateRequest;
import br.edu.com.matricula.dto.response.MatriculaResponse;
import br.edu.com.matricula.dto.response.PageResponse;
import br.edu.com.matricula.service.MatriculaService;
import jakarta.validation.Valid;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

import java.util.UUID;

@RestController
@RequestMapping("/api/v1/matriculas")
public class MatriculaController {

    private final MatriculaService matriculaService;

    public MatriculaController(MatriculaService matriculaService) {
        this.matriculaService = matriculaService;
    }

    @PostMapping
    public ResponseEntity<MatriculaResponse> criar(@Valid @RequestBody MatriculaCreateRequest request) {
        var response = matriculaService.criar(request);
        var location = ServletUriComponentsBuilder.fromCurrentRequest()
                .path("/{id}")
                .buildAndExpand(response.id())
                .toUri();
        return ResponseEntity.created(location).body(response);
    }

    @GetMapping("/{id}")
    public MatriculaResponse buscarPorId(@PathVariable UUID id) {
        return matriculaService.buscarPorId(id);
    }

    @PatchMapping("/{id}/confirmar")
    public MatriculaResponse confirmar(@PathVariable UUID id) {
        return matriculaService.confirmar(id);
    }

    @PatchMapping("/{id}/cancelar")
    public MatriculaResponse cancelar(@PathVariable UUID id) {
        return matriculaService.cancelar(id);
    }

    @GetMapping("/aluno/{alunoId}")
    public PageResponse<MatriculaResponse> buscarPorAluno(
            @PathVariable UUID alunoId,
            @RequestParam(required = false) String q,
            @PageableDefault(size = 10, sort = "dataSolicitacao", direction = Sort.Direction.DESC) Pageable pageable
    ) {
        return matriculaService.buscarPorAluno(alunoId, q, pageable);
    }

    @GetMapping("/turma/{turmaId}")
    public PageResponse<MatriculaResponse> buscarPorTurma(
            @PathVariable UUID turmaId,
            @RequestParam(required = false) String q,
            @PageableDefault(size = 10, sort = "dataSolicitacao", direction = Sort.Direction.DESC) Pageable pageable
    ) {
        return matriculaService.buscarPorTurma(turmaId, q, pageable);
    }
}
