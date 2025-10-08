package br.pucpr.service;

import br.pucpr.dto.CarteiraRequest;
import br.pucpr.dto.CarteiraResponse;
import br.pucpr.model.Carteira;
import br.pucpr.model.User;
import br.pucpr.repository.CarteiraRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class CarteiraService {

    private final CarteiraRepository carteiraRepository;

    @Transactional
    public CarteiraResponse criar(CarteiraRequest request, User user) {
        Carteira carteira = Carteira.builder()
                .user(user)
                .nome(request.getNome())
                .saldo(request.getSaldo())
                .ativa(true)
                .build();

        carteira = carteiraRepository.save(carteira);
        return mapToResponse(carteira);
    }

    public List<CarteiraResponse> listarPorUsuario(User user) {
        return carteiraRepository.findByUserId(user.getId())
                .stream()
                .map(this::mapToResponse)
                .collect(Collectors.toList());
    }

    public CarteiraResponse buscarPorId(Long id, User user) {
        Carteira carteira = carteiraRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Carteira não encontrada"));

        if (!carteira.getUser().getId().equals(user.getId())) {
            throw new RuntimeException("Acesso negado");
        }

        return mapToResponse(carteira);
    }

    @Transactional
    public CarteiraResponse atualizar(Long id, CarteiraRequest request, User user) {
        Carteira carteira = carteiraRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Carteira não encontrada"));

        if (!carteira.getUser().getId().equals(user.getId())) {
            throw new RuntimeException("Acesso negado");
        }

        carteira.setNome(request.getNome());
        carteira.setSaldo(request.getSaldo());

        carteira = carteiraRepository.save(carteira);
        return mapToResponse(carteira);
    }

    @Transactional
    public void deletar(Long id, User user) {
        Carteira carteira = carteiraRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Carteira não encontrada"));

        if (!carteira.getUser().getId().equals(user.getId())) {
            throw new RuntimeException("Acesso negado");
        }

        carteiraRepository.delete(carteira);
    }

    private CarteiraResponse mapToResponse(Carteira carteira) {
        return CarteiraResponse.builder()
                .id(carteira.getId())
                .nome(carteira.getNome())
                .saldo(carteira.getSaldo())
                .ativa(carteira.getAtiva())
                .criadaEm(carteira.getCriadaEm())
                .atualizadaEm(carteira.getAtualizadaEm())
                .build();
    }
}