package br.pucpr.controller;

import br.pucpr.dto.MoedaRequest;
import br.pucpr.dto.MoedaResponse;
import br.pucpr.service.MoedaService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/moedas")
@RequiredArgsConstructor
@SecurityRequirement(name = "bearerAuth")
@Tag(name = "Moedas")
public class MoedaController {

    private final MoedaService moedaService;

    @PostMapping
    @Operation(summary = "Criar moeda")
    public ResponseEntity<MoedaResponse> criar(@Valid @RequestBody MoedaRequest request) {
        MoedaResponse response = moedaService.criar(request);
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    @GetMapping
    @Operation(summary = "Listar todas as moedas")
    public ResponseEntity<List<MoedaResponse>> listarTodas() {
        List<MoedaResponse> moedas = moedaService.listarTodas();
        return ResponseEntity.ok(moedas);
    }

    @GetMapping("/ativas")
    @Operation(summary = "Listar moedas ativas")
    public ResponseEntity<List<MoedaResponse>> listarAtivas() {
        List<MoedaResponse> moedas = moedaService.listarAtivas();
        return ResponseEntity.ok(moedas);
    }

    @GetMapping("/{id}")
    @Operation(summary = "Buscar moeda por ID")
    public ResponseEntity<MoedaResponse> buscarPorId(@PathVariable Long id) {
        MoedaResponse moeda = moedaService.buscarPorId(id);
        return ResponseEntity.ok(moeda);
    }

    @GetMapping("/codigo/{codigo}")
    @Operation(summary = "Buscar moeda por código")
    public ResponseEntity<MoedaResponse> buscarPorCodigo(@PathVariable String codigo) {
        MoedaResponse moeda = moedaService.buscarPorCodigo(codigo);
        return ResponseEntity.ok(moeda);
    }

    @PutMapping("/{id}")
    @Operation(summary = "Atualizar moeda")
    public ResponseEntity<MoedaResponse> atualizar(
            @PathVariable Long id,
            @Valid @RequestBody MoedaRequest request
    ) {
        MoedaResponse response = moedaService.atualizar(id, request);
        return ResponseEntity.ok(response);
    }

    @DeleteMapping("/{id}")
    @Operation(summary = "Deletar moeda")
    public ResponseEntity<Void> deletar(@PathVariable Long id) {
        moedaService.deletar(id);
        return ResponseEntity.noContent().build();
    }
}