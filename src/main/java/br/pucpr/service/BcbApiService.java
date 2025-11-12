package br.pucpr.service;

import com.fasterxml.jackson.databind.JsonNode;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;
import org.springframework.web.reactive.function.client.WebClientResponseException;

import java.math.BigDecimal;
import java.time.Duration;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;

@Service
@RequiredArgsConstructor
@Slf4j
public class BcbApiService {

    private final WebClient.Builder webClientBuilder;

    @Value("${bcb.api.base-url}")
    private String baseUrl;

    @Value("${bcb.api.timeout}")
    private Integer timeout;

    public BigDecimal obterCotacao(String codigoMoeda, LocalDate data) {
        log.info("Consultando cotação BCB para {} na data {}", codigoMoeda, data);

        String dataFormatada = data.format(DateTimeFormatter.ofPattern("MM-dd-yyyy"));

        try {
            WebClient webClient = webClientBuilder.baseUrl(baseUrl).build();

            JsonNode response = webClient.get()
                    .uri(uriBuilder -> uriBuilder
                            .path("/CotacaoMoedaDia(moeda=@moeda,dataCotacao=@dataCotacao)")
                            .queryParam("@moeda", "'" + codigoMoeda + "'")
                            .queryParam("@dataCotacao", "'" + dataFormatada + "'")
                            .queryParam("$format", "json")
                            .build())
                    .retrieve()
                    .bodyToMono(JsonNode.class)
                    .timeout(Duration.ofMillis(timeout))
                    .block();

            if (response == null || !response.has("value") || response.get("value").isEmpty()) {
                log.warn("Nenhuma cotação encontrada para {} em {}", codigoMoeda, data);
                throw new RuntimeException("Cotação não disponível para a data selecionada");
            }

            JsonNode value = response.get("value").get(0);
            BigDecimal cotacao = value.get("cotacaoCompra").decimalValue();

            log.info("Cotação obtida: {} = {}", codigoMoeda, cotacao);
            return cotacao;

        } catch (WebClientResponseException e) {
            log.error("Erro ao consultar API BCB: Status {}", e.getStatusCode());
            throw new RuntimeException("Erro ao consultar cotação: " + e.getMessage());
        } catch (Exception e) {
            log.error("Erro inesperado ao consultar BCB", e);
            throw new RuntimeException("Erro ao consultar cotação do Banco Central");
        }
    }

    public BigDecimal obterCotacaoAtual(String codigoMoeda) {
        LocalDate hoje = LocalDate.now();

        for (int i = 0; i < 7; i++) {
            try {
                return obterCotacao(codigoMoeda, hoje.minusDays(i));
            } catch (RuntimeException e) {
                log.debug("Cotação não encontrada para {}, tentando dia anterior", hoje.minusDays(i));
            }
        }

        throw new RuntimeException("Não foi possível obter cotação recente");
    }
}