package br.pucpr.model;

import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;

@Entity
@Table(name = "log_operacoes")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class LogOperacao {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id")
    private User user;

    @Column(nullable = false, length = 10)
    private String metodoHttp;

    @Column(nullable = false, length = 500)
    private String endpoint;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private StatusOperacao status;

    @Column(nullable = false)
    private Integer statusCode;

    @Column(length = 2000)
    private String requestBody;

    @Column(length = 5000)
    private String responseBody;

    @Column(length = 1000)
    private String mensagemErro;

    @Column(length = 100)
    private String ipAddress;

    @Column(length = 500)
    private String userAgent;

    @Column(nullable = false)
    private Long tempoExecucaoMs;

    @Column(nullable = false)
    private LocalDateTime timestamp;

    @PrePersist
    protected void onCreate() {
        timestamp = LocalDateTime.now();
    }

    public enum StatusOperacao {
        SUCESSO, ERRO, AVISO
    }
}