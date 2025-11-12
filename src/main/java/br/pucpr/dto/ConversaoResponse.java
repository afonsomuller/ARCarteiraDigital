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
public class ConversaoResponse {
    private Long id;
    private Long carteiraOrigemId;
    private String carteiraOrigemNome;
    private Long carteiraDestinoId;
    private String carteiraDestinoNome;
    private String moedaOrigem;
    private String moedaDestino;
    private BigDecimal valorOrigem;
    private BigDecimal saldoAnteriorOrigem;
    private BigDecimal saldoNovoOrigem;
    private BigDecimal taxaCambio;
    private BigDecimal taxaImposto;
    private BigDecimal valorImposto;
    private BigDecimal valorFinal;
    private LocalDateTime dataConversao;
    private LocalDateTime criadaEm;
}