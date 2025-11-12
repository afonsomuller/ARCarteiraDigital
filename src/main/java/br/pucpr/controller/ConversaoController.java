package br.pucpr.controller;

import br.pucpr.dto.ConversaoRequest;
import br.pucpr.dto.ConversaoResponse;
import br.pucpr.model.User;
import br.pucpr.service.ConversaoService;
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
@RequestMapping("/conversoes")
@RequiredArgsConstructor
@SecurityRequirement(name = "bearerAuth")
@Tag(name = "Conversões")
public class ConversaoController {

    private final ConversaoService conversaoService;

    @PostMapping
    @Operation(summary = "Realizar conversão de moeda com cotação BCB")
    public ResponseEntity<ConversaoResponse> converter(
            @Valid @RequestBody ConversaoRequest request,
            @AuthenticationPrincipal User user
    ) {
        ConversaoResponse response = conversaoService.converter(request, user);
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    @GetMapping
    @Operation(summary = "Listar conversões do usuário")
    public ResponseEntity<List<ConversaoResponse>> listar(@AuthenticationPrincipal User user) {
        List<ConversaoResponse> conversoes = conversaoService.listarPorUsuario(user);
        return ResponseEntity.ok(conversoes);
    }

    @GetMapping("/{id}")
    @Operation(summary = "Buscar conversão por ID")
    public ResponseEntity<ConversaoResponse> buscarPorId(
            @PathVariable Long id,
            @AuthenticationPrincipal User user
    ) {
        ConversaoResponse conversao = conversaoService.buscarPorId(id, user);
        return ResponseEntity.ok(conversao);
    }
}