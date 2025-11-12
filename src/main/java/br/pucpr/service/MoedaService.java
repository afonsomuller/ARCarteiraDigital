package br.pucpr.service;

import br.pucpr.dto.MoedaRequest;
import br.pucpr.dto.MoedaResponse;
import br.pucpr.model.Moeda;
import br.pucpr.repository.MoedaRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Slf4j
public class MoedaService {

    private final MoedaRepository moedaRepository;

    @Transactional
    public MoedaResponse criar(MoedaRequest request) {
        log.info("Criando nova moeda: {}", request.getCodigo());

        if (moedaRepository.findByCodigo(request.getCodigo()).isPresent()) {
            throw new RuntimeException("Moeda com código " + request.getCodigo() + " já existe");
        }

        Moeda moeda = Moeda.builder()
                .codigo(request.getCodigo().toUpperCase())
                .nome(request.getNome())
                .simbolo(request.getSimbolo())
                .codigoBcb(request.getCodigoBcb())
                .ativa(request.getAtiva() != null ? request.getAtiva() : true)
                .build();

        moeda = moedaRepository.save(moeda);
        return mapToResponse(moeda);
    }

    public List<MoedaResponse> listarTodas() {
        return moedaRepository.findAll()
                .stream()
                .map(this::mapToResponse)
                .collect(Collectors.toList());
    }

    public List<MoedaResponse> listarAtivas() {
        return moedaRepository.findByAtivaTrue()
                .stream()
                .map(this::mapToResponse)
                .collect(Collectors.toList());
    }

    public MoedaResponse buscarPorId(Long id) {
        Moeda moeda = moedaRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Moeda não encontrada"));
        return mapToResponse(moeda);
    }

    public MoedaResponse buscarPorCodigo(String codigo) {
        Moeda moeda = moedaRepository.findByCodigo(codigo.toUpperCase())
                .orElseThrow(() -> new RuntimeException("Moeda não encontrada"));
        return mapToResponse(moeda);
    }

    @Transactional
    public MoedaResponse atualizar(Long id, MoedaRequest request) {
        log.info("Atualizando moeda ID: {}", id);

        Moeda moeda = moedaRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Moeda não encontrada"));

        moeda.setCodigo(request.getCodigo().toUpperCase());
        moeda.setNome(request.getNome());
        moeda.setSimbolo(request.getSimbolo());
        moeda.setCodigoBcb(request.getCodigoBcb());
        moeda.setAtiva(request.getAtiva());

        moeda = moedaRepository.save(moeda);
        return mapToResponse(moeda);
    }

    @Transactional
    public void deletar(Long id) {
        log.info("Deletando moeda ID: {}", id);

        if (!moedaRepository.existsById(id)) {
            throw new RuntimeException("Moeda não encontrada");
        }

        moedaRepository.deleteById(id);
    }

    private MoedaResponse mapToResponse(Moeda moeda) {
        return MoedaResponse.builder()
                .id(moeda.getId())
                .codigo(moeda.getCodigo())
                .nome(moeda.getNome())
                .simbolo(moeda.getSimbolo())
                .codigoBcb(moeda.getCodigoBcb())
                .ativa(moeda.getAtiva())
                .criadaEm(moeda.getCriadaEm())
                .atualizadaEm(moeda.getAtualizadaEm())
                .build();
    }
}