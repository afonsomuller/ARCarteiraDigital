package br.pucpr.model;

import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;
import java.util.List;

@Entity
@Table(name = "moedas")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Moeda {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, unique = true, length = 3)
    private String codigo;

    @Column(nullable = false)
    private String nome;

    @Column(length = 10)
    private String simbolo;

    @Column(length = 10)
    private String codigoBcb;

    @Column(nullable = false)
    private Boolean ativa = true;

    @Column(nullable = false, updatable = false)
    private LocalDateTime criadaEm;

    @Column(nullable = false)
    private LocalDateTime atualizadaEm;

    @OneToMany(mappedBy = "moedaOrigem", cascade = CascadeType.ALL)
    private List<Conversao> conversoesOrigem;

    @OneToMany(mappedBy = "moedaDestino", cascade = CascadeType.ALL)
    private List<Conversao> conversoesDestino;

    @PrePersist
    protected void onCreate() {
        criadaEm = LocalDateTime.now();
        atualizadaEm = LocalDateTime.now();
    }

    @PreUpdate
    protected void onUpdate() {
        atualizadaEm = LocalDateTime.now();
    }
}