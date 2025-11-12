package br.pucpr.model;

import jakarta.persistence.*;
import lombok.*;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Entity
@Table(name = "conversoes")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Conversao {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "carteira_origem_id", nullable = false)
    private Carteira carteiraOrigem;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "carteira_destino_id", nullable = false)
    private Carteira carteiraDestino;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "moeda_origem_id", nullable = false)
    private Moeda moedaOrigem;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "moeda_destino_id", nullable = false)
    private Moeda moedaDestino;

    @Column(nullable = false, precision = 19, scale = 4)
    private BigDecimal valorOrigem;

    @Column(nullable = false, precision = 19, scale = 4)
    private BigDecimal saldoAnteriorOrigem;

    @Column(nullable = false, precision = 19, scale = 4)
    private BigDecimal saldoNovoOrigem;

    @Column(nullable = false, precision = 19, scale = 6)
    private BigDecimal taxaCambio;

    @Column(nullable = false, precision = 5, scale = 4)
    private BigDecimal taxaImposto;

    @Column(nullable = false, precision = 19, scale = 4)
    private BigDecimal valorImposto;

    @Column(nullable = false, precision = 19, scale = 4)
    private BigDecimal valorFinal;

    @Column(nullable = false)
    private LocalDateTime dataConversao;

    @Column(nullable = false, updatable = false)
    private LocalDateTime criadaEm;

    @PrePersist
    protected void onCreate() {
        criadaEm = LocalDateTime.now();
    }
}