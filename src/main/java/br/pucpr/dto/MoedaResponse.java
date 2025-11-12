package br.pucpr.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class MoedaResponse {
    private Long id;
    private String codigo;
    private String nome;
    private String simbolo;
    private String codigoBcb;
    private Boolean ativa;
    private LocalDateTime criadaEm;
    private LocalDateTime atualizadaEm;
}