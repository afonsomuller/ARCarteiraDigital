package br.pucpr.service;

import br.pucpr.dto.ConversaoRequest;
import br.pucpr.dto.ConversaoResponse;
import br.pucpr.model.Carteira;
import br.pucpr.model.Conversao;
import br.pucpr.model.Moeda;
import br.pucpr.model.User;
import br.pucpr.repository.CarteiraRepository;
import br.pucpr.repository.ConversaoRepository;
import br.pucpr.repository.MoedaRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Slf4j
public class ConversaoService {

    private final ConversaoRepository conversaoRepository;
    private final MoedaRepository moedaRepository;
    private final CarteiraRepository carteiraRepository;
    private final BcbApiService bcbApiService;

    @Value("${conversao.taxa-imposto}")
    private BigDecimal taxaImposto;

    @Transactional
    public ConversaoResponse converter(ConversaoRequest request, User user) {
        log.info("Iniciando conversão da carteira {} para moeda {}",
                request.getCarteiraOrigemId(), request.getCodigoMoedaDestino());

        // Busca carteira de origem
        Carteira carteiraOrigem = carteiraRepository.findById(request.getCarteiraOrigemId())
                .orElseThrow(() -> new RuntimeException("Carteira de origem não encontrada"));

        // Verifica se a carteira pertence ao usuário
        if (!carteiraOrigem.getUser().getId().equals(user.getId())) {
            throw new RuntimeException("Você não tem permissão para usar esta carteira");
        }

        // Verifica se a carteira está ativa
        if (!carteiraOrigem.getAtiva()) {
            throw new RuntimeException("Carteira de origem não está ativa");
        }

        // Verifica se tem saldo suficiente
        if (carteiraOrigem.getSaldo().compareTo(request.getValor()) < 0) {
            throw new RuntimeException("Saldo insuficiente na carteira de origem");
        }

        // Busca moeda de destino
        Moeda moedaDestino = moedaRepository.findByCodigo(request.getCodigoMoedaDestino())
                .orElseThrow(() -> new RuntimeException("Moeda de destino não encontrada"));

        if (!moedaDestino.getAtiva()) {
            throw new RuntimeException("Moeda de destino não está ativa");
        }

        // Moeda de origem é sempre BRL (da carteira)
        Moeda moedaOrigem = moedaRepository.findByCodigo("BRL")
                .orElseThrow(() -> new RuntimeException("Moeda BRL não encontrada"));

        // Consulta cotação na API do BCB
        BigDecimal taxaCambio = bcbApiService.obterCotacaoAtual(moedaDestino.getCodigoBcb());

        // Calcula conversão
        BigDecimal valorOrigem = request.getValor();
        BigDecimal valorConvertido = valorOrigem.divide(taxaCambio, 4, RoundingMode.HALF_UP);
        BigDecimal valorImposto = valorConvertido.multiply(taxaImposto).setScale(4, RoundingMode.HALF_UP);
        BigDecimal valorFinal = valorConvertido.add(valorImposto).setScale(4, RoundingMode.HALF_UP);

        // Guarda saldo anterior
        BigDecimal saldoAnteriorOrigem = carteiraOrigem.getSaldo();

        // Deduz saldo da carteira de origem
        BigDecimal novoSaldoOrigem = carteiraOrigem.getSaldo().subtract(valorOrigem);
        carteiraOrigem.setSaldo(novoSaldoOrigem);
        carteiraRepository.save(carteiraOrigem);

        log.info("Saldo deduzido da carteira origem. Saldo anterior: {}, Novo saldo: {}",
                saldoAnteriorOrigem, novoSaldoOrigem);

        // Cria nova carteira com a moeda convertida
        Carteira carteiraDestino = Carteira.builder()
                .user(user)
                .nome(carteiraOrigem.getNome() + " - " + moedaDestino.getCodigo())
                .saldo(valorFinal)
                .ativa(true)
                .build();

        carteiraDestino = carteiraRepository.save(carteiraDestino);

        log.info("Nova carteira criada: {} com saldo {}", carteiraDestino.getNome(), valorFinal);

        // Salva registro da conversão
        Conversao conversao = Conversao.builder()
                .user(user)
                .carteiraOrigem(carteiraOrigem)
                .carteiraDestino(carteiraDestino)
                .moedaOrigem(moedaOrigem)
                .moedaDestino(moedaDestino)
                .valorOrigem(valorOrigem)
                .saldoAnteriorOrigem(saldoAnteriorOrigem)
                .saldoNovoOrigem(novoSaldoOrigem)
                .taxaCambio(taxaCambio)
                .taxaImposto(taxaImposto)
                .valorImposto(valorImposto)
                .valorFinal(valorFinal)
                .dataConversao(LocalDateTime.now())
                .build();

        conversao = conversaoRepository.save(conversao);

        log.info("Conversão realizada com sucesso: ID {}", conversao.getId());

        return mapToResponse(conversao);
    }

    public List<ConversaoResponse> listarPorUsuario(User user) {
        return conversaoRepository.findByUserIdOrderByCriadaEmDesc(user.getId())
                .stream()
                .map(this::mapToResponse)
                .collect(Collectors.toList());
    }

    public ConversaoResponse buscarPorId(Long id, User user) {
        Conversao conversao = conversaoRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Conversão não encontrada"));

        if (!conversao.getUser().getId().equals(user.getId())) {
            throw new RuntimeException("Acesso negado");
        }

        return mapToResponse(conversao);
    }

    private ConversaoResponse mapToResponse(Conversao conversao) {
        return ConversaoResponse.builder()
                .id(conversao.getId())
                .carteiraOrigemId(conversao.getCarteiraOrigem().getId())
                .carteiraOrigemNome(conversao.getCarteiraOrigem().getNome())
                .carteiraDestinoId(conversao.getCarteiraDestino().getId())
                .carteiraDestinoNome(conversao.getCarteiraDestino().getNome())
                .moedaOrigem(conversao.getMoedaOrigem().getCodigo())
                .moedaDestino(conversao.getMoedaDestino().getCodigo())
                .valorOrigem(conversao.getValorOrigem())
                .saldoAnteriorOrigem(conversao.getSaldoAnteriorOrigem())
                .saldoNovoOrigem(conversao.getSaldoNovoOrigem())
                .taxaCambio(conversao.getTaxaCambio())
                .taxaImposto(conversao.getTaxaImposto())
                .valorImposto(conversao.getValorImposto())
                .valorFinal(conversao.getValorFinal())
                .dataConversao(conversao.getDataConversao())
                .criadaEm(conversao.getCriadaEm())
                .build();
    }
}