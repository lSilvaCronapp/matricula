package br.edu.com.matricula.controller;

import br.edu.com.matricula.domain.enums.StatusTurma;
import br.edu.com.matricula.dto.request.TurmaRequest;
import br.edu.com.matricula.dto.response.PageResponse;
import br.edu.com.matricula.dto.response.TurmaResponse;
import br.edu.com.matricula.service.TurmaService;
import jakarta.validation.Valid;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

import java.util.UUID;

@RestController
@RequestMapping("/api/v1/turmas")
public class TurmaController {

    private final TurmaService turmaService;

    public TurmaController(TurmaService turmaService) {
        this.turmaService = turmaService;
    }

    @PostMapping
    public ResponseEntity<TurmaResponse> criar(@Valid @RequestBody TurmaRequest request) {
        var response = turmaService.criar(request);
        var location = ServletUriComponentsBuilder.fromCurrentRequest()
                .path("/{id}")
                .buildAndExpand(response.id())
                .toUri();
        return ResponseEntity.created(location).body(response);
    }

    @GetMapping
    public PageResponse<TurmaResponse> listar(
            @RequestParam(required = false) StatusTurma status,
            @RequestParam(required = false) String q,
            @PageableDefault(size = 10, sort = "codigo", direction = Sort.Direction.ASC) Pageable pageable
    ) {
        return turmaService.listar(status, q, pageable);
    }

    @GetMapping("/{id}")
    public TurmaResponse buscarPorId(@PathVariable UUID id) {
        return turmaService.buscarPorId(id);
    }

    @PutMapping("/{id}")
    public TurmaResponse atualizar(@PathVariable UUID id, @Valid @RequestBody TurmaRequest request) {
        return turmaService.atualizar(id, request);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> excluir(@PathVariable UUID id) {
        turmaService.excluir(id);
        return ResponseEntity.noContent().build();
    }
}
