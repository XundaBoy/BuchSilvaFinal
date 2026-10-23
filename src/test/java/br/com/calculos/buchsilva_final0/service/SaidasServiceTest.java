package br.com.calculos.buchsilva_final0.service;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertSame;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import br.com.calculos.buchsilva_final0.entity.Saidas;
import br.com.calculos.buchsilva_final0.repository.SaidasRepository;

@ExtendWith(MockitoExtension.class)
class SaidasServiceTest {

    @Mock
    private SaidasRepository saidasRepository;

    @InjectMocks
    private SaidasService saidasService;

    @Test
    void save_deveSalvarSaidaERetornarMensagem() {
        Saidas saida = criarSaida("Conta de luz", "250.50");

        String resultado = saidasService.save(saida);

        assertEquals("Saida salva com sucesso", resultado);
        verify(saidasRepository).save(saida);
    }

    @Test
    void save_devePropagarErroDoRepository() {
        Saidas saida = criarSaida("Conta de luz", "250.50");
        when(saidasRepository.save(saida))
                .thenThrow(new RuntimeException("Falha ao salvar"));

        RuntimeException erro = assertThrows(RuntimeException.class,
                () -> saidasService.save(saida));

        assertEquals("Falha ao salvar", erro.getMessage());
    }

    @Test
    void updateById_deveAtualizarNomeEValorSemAlterarDataOriginal() {
        Saidas existente = criarSaida("Conta de luz", "250.50");
        existente.setId(7L);
        LocalDateTime dataOriginal = LocalDateTime.of(2026, 10, 20, 10, 30);
        existente.setDataHora(dataOriginal);

        Saidas alteracoes = criarSaida("Conta de água", "180.00");
        when(saidasRepository.findById(7L)).thenReturn(Optional.of(existente));

        String resultado = saidasService.updateById(alteracoes, 7L);

        assertEquals("Saída atualizada com sucesso", resultado);
        assertEquals(7L, existente.getId());
        assertEquals("Conta de água", existente.getNomeSaida());
        assertEquals(new BigDecimal("180.00"), existente.getValor());
        assertEquals(dataOriginal, existente.getDataHora());
        verify(saidasRepository).findById(7L);
        verify(saidasRepository).save(existente);
    }

    @Test
    void updateById_deveLancarErroQuandoIdNaoExiste() {
        Saidas alteracoes = criarSaida("Conta de água", "180.00");
        when(saidasRepository.findById(999L)).thenReturn(Optional.empty());

        RuntimeException erro = assertThrows(RuntimeException.class,
                () -> saidasService.updateById(alteracoes, 999L));

        assertEquals("Saída não encontrada", erro.getMessage());
        verify(saidasRepository, never()).save(any(Saidas.class));
    }

    @Test
    void updateById_devePropagarErroAoSalvar() {
        Saidas existente = criarSaida("Conta de luz", "250.50");
        Saidas alteracoes = criarSaida("Conta de água", "180.00");
        when(saidasRepository.findById(7L)).thenReturn(Optional.of(existente));
        when(saidasRepository.save(existente))
                .thenThrow(new RuntimeException("Falha ao atualizar"));

        RuntimeException erro = assertThrows(RuntimeException.class,
                () -> saidasService.updateById(alteracoes, 7L));

        assertEquals("Falha ao atualizar", erro.getMessage());
    }

    @Test
    void deleteById_deveExcluirSaidaPeloId() {
        String resultado = saidasService.deleteById(4L);

        assertEquals("Saida deletada com sucesso", resultado);
        verify(saidasRepository).deleteById(4L);
    }

    @Test
    void deleteById_devePropagarErroDoRepository() {
        doThrow(new RuntimeException("Registro não encontrado"))
                .when(saidasRepository).deleteById(999L);

        RuntimeException erro = assertThrows(RuntimeException.class,
                () -> saidasService.deleteById(999L));

        assertEquals("Registro não encontrado", erro.getMessage());
    }

    @Test
    void findAll_deveRetornarTodasAsSaidas() {
        Saidas primeira = criarSaida("Luz", "250.50");
        Saidas segunda = criarSaida("Água", "100.00");
        List<Saidas> esperadas = List.of(primeira, segunda);
        when(saidasRepository.findAll()).thenReturn(esperadas);

        List<Saidas> resultado = saidasService.findAll();

        assertSame(esperadas, resultado);
        assertEquals(2, resultado.size());
        verify(saidasRepository).findAll();
    }

    @Test
    void findAll_deveRetornarListaVaziaQuandoNaoHaSaidas() {
        when(saidasRepository.findAll()).thenReturn(List.of());

        List<Saidas> resultado = saidasService.findAll();

        assertTrue(resultado.isEmpty());
    }

    private Saidas criarSaida(String nome, String valor) {
        Saidas saida = new Saidas();
        saida.setNomeSaida(nome);
        saida.setValor(new BigDecimal(valor));
        return saida;
    }
}