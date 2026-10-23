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

import br.com.calculos.buchsilva_final0.entity.Entradas;
import br.com.calculos.buchsilva_final0.repository.EntradasRepository;

@ExtendWith(MockitoExtension.class)
class EntradasServiceTest {

    @Mock
    private EntradasRepository entradasRepository;

    @InjectMocks
    private EntradasService entradasService;

    @Test
    void save_deveSalvarEntradaERetornarMensagem() {
        Entradas entrada = criarEntrada("Salário", "2500.00");

        String resultado = entradasService.save(entrada);

        assertEquals("Entrada salva com sucesso", resultado);
        verify(entradasRepository).save(entrada);
    }

    @Test
    void save_devePropagarErroDoRepository() {
        Entradas entrada = criarEntrada("Salário", "2500.00");
        when(entradasRepository.save(entrada))
                .thenThrow(new RuntimeException("Falha ao salvar"));

        RuntimeException erro = assertThrows(RuntimeException.class,
                () -> entradasService.save(entrada));

        assertEquals("Falha ao salvar", erro.getMessage());
    }

    @Test
    void updateById_deveAtualizarNomeEValorSemAlterarDataOriginal() {
        Entradas existente = criarEntrada("Salário", "2500.00");
        existente.setId(5L);
        LocalDateTime dataOriginal = LocalDateTime.of(2026, 10, 20, 10, 30);
        existente.setDataHora(dataOriginal);

        Entradas alteracoes = criarEntrada("Salário atualizado", "3000.00");
        when(entradasRepository.findById(5L)).thenReturn(Optional.of(existente));

        String resultado = entradasService.updateById(alteracoes, 5L);

        assertEquals("Entrada atualizada com sucesso", resultado);
        assertEquals(5L, existente.getId());
        assertEquals("Salário atualizado", existente.getNomeEntrada());
        assertEquals(new BigDecimal("3000.00"), existente.getValor());
        assertEquals(dataOriginal, existente.getDataHora());
        verify(entradasRepository).findById(5L);
        verify(entradasRepository).save(existente);
    }

    @Test
    void updateById_deveLancarErroQuandoIdNaoExiste() {
        Entradas alteracoes = criarEntrada("Salário atualizado", "3000.00");
        when(entradasRepository.findById(999L)).thenReturn(Optional.empty());

        RuntimeException erro = assertThrows(RuntimeException.class,
                () -> entradasService.updateById(alteracoes, 999L));

        assertEquals("Entrada não encontrada", erro.getMessage());
        verify(entradasRepository, never()).save(any(Entradas.class));
    }

    @Test
    void updateById_devePropagarErroAoSalvar() {
        Entradas existente = criarEntrada("Salário", "2500.00");
        Entradas alteracoes = criarEntrada("Salário atualizado", "3000.00");
        when(entradasRepository.findById(5L)).thenReturn(Optional.of(existente));
        when(entradasRepository.save(existente))
                .thenThrow(new RuntimeException("Falha ao atualizar"));

        RuntimeException erro = assertThrows(RuntimeException.class,
                () -> entradasService.updateById(alteracoes, 5L));

        assertEquals("Falha ao atualizar", erro.getMessage());
    }

    @Test
    void deleteById_deveExcluirEntradaPeloId() {
        String resultado = entradasService.deleteById(3L);

        assertEquals("Entrada deletada com sucesso", resultado);
        verify(entradasRepository).deleteById(3L);
    }

    @Test
    void deleteById_devePropagarErroDoRepository() {
        doThrow(new RuntimeException("Registro não encontrado"))
                .when(entradasRepository).deleteById(999L);

        RuntimeException erro = assertThrows(RuntimeException.class,
                () -> entradasService.deleteById(999L));

        assertEquals("Registro não encontrado", erro.getMessage());
    }

    @Test
    void findAll_deveRetornarTodasAsEntradas() {
        Entradas primeira = criarEntrada("Salário", "2500.00");
        Entradas segunda = criarEntrada("Freelance", "400.00");
        List<Entradas> esperadas = List.of(primeira, segunda);
        when(entradasRepository.findAll()).thenReturn(esperadas);

        List<Entradas> resultado = entradasService.findAll();

        assertSame(esperadas, resultado);
        assertEquals(2, resultado.size());
        verify(entradasRepository).findAll();
    }

    @Test
    void findAll_deveRetornarListaVaziaQuandoNaoHaEntradas() {
        when(entradasRepository.findAll()).thenReturn(List.of());

        List<Entradas> resultado = entradasService.findAll();

        assertTrue(resultado.isEmpty());
    }

    private Entradas criarEntrada(String nome, String valor) {
        Entradas entrada = new Entradas();
        entrada.setNomeEntrada(nome);
        entrada.setValor(new BigDecimal(valor));
        return entrada;
    }
}