package br.pucpr.config;

import br.pucpr.model.Moeda;
import br.pucpr.repository.MoedaRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;

import java.util.Arrays;
import java.util.List;

@Component
@RequiredArgsConstructor
@Slf4j
public class DataInitializer implements CommandLineRunner {

    private final MoedaRepository moedaRepository;

    @Override
    public void run(String... args) {
        inicializarMoedas();
    }

    private void inicializarMoedas() {
        if (moedaRepository.count() == 0) {
            log.info("Criando moedas iniciais...");

            List<Moeda> moedas = Arrays.asList(
                    Moeda.builder()
                            .codigo("BRL")
                            .nome("Real Brasileiro")
                            .simbolo("R$")
                            .codigoBcb("BRL")
                            .ativa(true)
                            .build(),

                    Moeda.builder()
                            .codigo("USD")
                            .nome("Dólar Americano")
                            .simbolo("$")
                            .codigoBcb("USD")
                            .ativa(true)
                            .build(),

                    Moeda.builder()
                            .codigo("EUR")
                            .nome("Euro")
                            .simbolo("€")
                            .codigoBcb("EUR")
                            .ativa(true)
                            .build(),

                    Moeda.builder()
                            .codigo("GBP")
                            .nome("Libra Esterlina")
                            .simbolo("£")
                            .codigoBcb("GBP")
                            .ativa(true)
                            .build()
            );

            moedaRepository.saveAll(moedas);
            log.info("{} moedas criadas com sucesso!", moedas.size());
        }
    }
}