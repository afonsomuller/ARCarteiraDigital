package pr.pucpr.ARCarteiraDigital.service;

import br.pucpr.dto.MoedaRequest;
import br.pucpr.dto.MoedaResponse;
import br.pucpr.model.Moeda;
import br.pucpr.repository.MoedaRepository;
import br.pucpr.service.MoedaService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class MoedaServiceTest {

    @Mock
    private MoedaRepository moedaRepository;

    @InjectMocks
    private MoedaService moedaService;

    private Moeda moeda;
    private MoedaRequest moedaRequest;

    @BeforeEach
    void setUp() {
        moeda = Moeda.builder()
                .id(1L)
                .codigo("USD")
                .nome("Dólar Americano")
                .simbolo("$")
                .codigoBcb("USD")
                .ativa(true)
                .build();

        moedaRequest = new MoedaRequest();
        moedaRequest.setCodigo("USD");
        moedaRequest.setNome("Dólar Americano");
        moedaRequest.setSimbolo("$");
        moedaRequest.setCodigoBcb("USD");
    }

    @Test
    void criar_DeveCriarMoeda_QuandoCodigoNaoExiste() {
        when(moedaRepository.findByCodigo(anyString())).thenReturn(Optional.empty());
        when(moedaRepository.save(any(Moeda.class))).thenReturn(moeda);

        MoedaResponse response = moedaService.criar(moedaRequest);

        assertNotNull(response);
        assertEquals("USD", response.getCodigo());
        verify(moedaRepository, times(1)).save(any(Moeda.class));
    }

    @Test
    void criar_DeveLancarExcecao_QuandoCodigoJaExiste() {
        when(moedaRepository.findByCodigo(anyString())).thenReturn(Optional.of(moeda));

        assertThrows(RuntimeException.class, () -> moedaService.criar(moedaRequest));
        verify(moedaRepository, never()).save(any(Moeda.class));
    }

    @Test
    void listarTodas_DeveRetornarListaDeMoedas() {
        List<Moeda> moedas = Arrays.asList(moeda);
        when(moedaRepository.findAll()).thenReturn(moedas);

        List<MoedaResponse> result = moedaService.listarTodas();

        assertNotNull(result);
        assertEquals(1, result.size());
        assertEquals("USD", result.get(0).getCodigo());
    }

    @Test
    void buscarPorId_DeveRetornarMoeda_QuandoExiste() {
        when(moedaRepository.findById(1L)).thenReturn(Optional.of(moeda));

        MoedaResponse response = moedaService.buscarPorId(1L);

        assertNotNull(response);
        assertEquals("USD", response.getCodigo());
    }

    @Test
    void buscarPorId_DeveLancarExcecao_QuandoNaoExiste() {
        when(moedaRepository.findById(1L)).thenReturn(Optional.empty());

        assertThrows(RuntimeException.class, () -> moedaService.buscarPorId(1L));
    }

    @Test
    void deletar_DeveRemoverMoeda_QuandoExiste() {
        when(moedaRepository.existsById(1L)).thenReturn(true);
        doNothing().when(moedaRepository).deleteById(1L);

        assertDoesNotThrow(() -> moedaService.deletar(1L));
        verify(moedaRepository, times(1)).deleteById(1L);
    }
}