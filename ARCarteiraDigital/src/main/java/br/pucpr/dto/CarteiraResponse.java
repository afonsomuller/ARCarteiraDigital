package br.pucpr.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class CarteiraResponse {
    private Long id;
    private String nome;
    private BigDecimal saldo;
    private Boolean ativa;
    private LocalDateTime criadaEm;
    private LocalDateTime atualizadaEm;
}