package br.pucpr.controller;

import br.pucpr.dto.CarteiraRequest;
import br.pucpr.dto.CarteiraResponse;
import br.pucpr.model.User;
import br.pucpr.service.CarteiraService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/carteiras")
@RequiredArgsConstructor
@SecurityRequirement(name = "bearerAuth")
@Tag(name = "Carteiras")
public class CarteiraController {

    private final CarteiraService carteiraService;

    @PostMapping
    @Operation(summary = "Criar carteira")
    public ResponseEntity<CarteiraResponse> criar(
            @Valid @RequestBody CarteiraRequest request,
            @AuthenticationPrincipal User user
    ) {
        CarteiraResponse response = carteiraService.criar(request, user);
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    @GetMapping
    @Operation(summary = "Listar minhas carteiras")
    public ResponseEntity<List<CarteiraResponse>> listar(@AuthenticationPrincipal User user) {
        List<CarteiraResponse> carteiras = carteiraService.listarPorUsuario(user);
        return ResponseEntity.ok(carteiras);
    }

    @GetMapping("/{id}")
    @Operation(summary = "Buscar carteira por ID")
    public ResponseEntity<CarteiraResponse> buscarPorId(
            @PathVariable Long id,
            @AuthenticationPrincipal User user
    ) {
        CarteiraResponse carteira = carteiraService.buscarPorId(id, user);
        return ResponseEntity.ok(carteira);
    }

    @PutMapping("/{id}")
    @Operation(summary = "Atualizar carteira")
    public ResponseEntity<CarteiraResponse> atualizar(
            @PathVariable Long id,
            @Valid @RequestBody CarteiraRequest request,
            @AuthenticationPrincipal User user
    ) {
        CarteiraResponse response = carteiraService.atualizar(id, request, user);
        return ResponseEntity.ok(response);
    }

    @DeleteMapping("/{id}")
    @Operation(summary = "Deletar carteira")
    public ResponseEntity<Void> deletar(
            @PathVariable Long id,
            @AuthenticationPrincipal User user
    ) {
        carteiraService.deletar(id, user);
        return ResponseEntity.noContent().build();
    }
}