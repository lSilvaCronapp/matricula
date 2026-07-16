package br.edu.com.matricula.controller;

import br.edu.com.matricula.dto.request.DisciplinaRequest;
import br.edu.com.matricula.dto.response.DisciplinaResponse;
import br.edu.com.matricula.service.DisciplinaService;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/v1/disciplinas")
public class DisciplinaController {

    private final DisciplinaService disciplinaService;

    public DisciplinaController(DisciplinaService disciplinaService) {
        this.disciplinaService = disciplinaService;
    }

    @PostMapping
    public ResponseEntity<DisciplinaResponse> criar(@Valid @RequestBody DisciplinaRequest request) {
        var response = disciplinaService.criar(request);
        var location = ServletUriComponentsBuilder.fromCurrentRequest()
                .path("/{id}")
                .buildAndExpand(response.id())
                .toUri();
        return ResponseEntity.created(location).body(response);
    }

    @GetMapping
    public List<DisciplinaResponse> listar() {
        return disciplinaService.listar();
    }

    @GetMapping("/{id}")
    public DisciplinaResponse buscarPorId(@PathVariable UUID id) {
        return disciplinaService.buscarPorId(id);
    }

    @PutMapping("/{id}")
    public DisciplinaResponse atualizar(@PathVariable UUID id, @Valid @RequestBody DisciplinaRequest request) {
        return disciplinaService.atualizar(id, request);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> excluir(@PathVariable UUID id) {
        disciplinaService.excluir(id);
        return ResponseEntity.noContent().build();
    }
}
