package br.com.calculos.buchsilva_final0.service;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.math.BigDecimal;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import br.com.calculos.buchsilva_final0.repository.EntradasRepository;
import br.com.calculos.buchsilva_final0.repository.SaidasRepository;

@ExtendWith(MockitoExtension.class)
class SaldoServiceTest {

    @Mock
    private EntradasRepository entradasRepository;

    @Mock
    private SaidasRepository saidasRepository;

    @InjectMocks
    private SaldoService saldoService;

    @Test
    void calcularSaldo_deveRetornarSaldoPositivo() {
        when(entradasRepository.somarEntradas()).thenReturn(new BigDecimal("5000.00"));
        when(saidasRepository.somarSaidas()).thenReturn(new BigDecimal("1750.00"));

        BigDecimal saldo = saldoService.calcularSaldo();

        assertEquals(new BigDecimal("3250.00"), saldo);
        verify(entradasRepository).somarEntradas();
        verify(saidasRepository).somarSaidas();
    }

    @Test
    void calcularSaldo_deveRetornarSaldoNegativo() {
        when(entradasRepository.somarEntradas()).thenReturn(new BigDecimal("100.00"));
        when(saidasRepository.somarSaidas()).thenReturn(new BigDecimal("150.00"));

        assertEquals(new BigDecimal("-50.00"), saldoService.calcularSaldo());
    }

    @Test
    void calcularSaldo_deveRetornarZeroQuandoValoresSaoIguais() {
        when(entradasRepository.somarEntradas()).thenReturn(new BigDecimal("200.00"));
        when(saidasRepository.somarSaidas()).thenReturn(new BigDecimal("200.00"));

        assertEquals(0, saldoService.calcularSaldo().compareTo(BigDecimal.ZERO));
    }

    @Test
    void calcularSaldo_deveRetornarZeroQuandoNaoExistemLancamentos() {
        when(entradasRepository.somarEntradas()).thenReturn(null);
        when(saidasRepository.somarSaidas()).thenReturn(null);

        assertEquals(0, saldoService.calcularSaldo().compareTo(BigDecimal.ZERO));
    }

    @Test
    void calcularSaldo_deveConsiderarZeroQuandoNaoExistemEntradas() {
        when(entradasRepository.somarEntradas()).thenReturn(null);
        when(saidasRepository.somarSaidas()).thenReturn(new BigDecimal("70.00"));

        assertEquals(new BigDecimal("-70.00"), saldoService.calcularSaldo());
    }

    @Test
    void calcularSaldo_deveConsiderarZeroQuandoNaoExistemSaidas() {
        when(entradasRepository.somarEntradas()).thenReturn(new BigDecimal("250.00"));
        when(saidasRepository.somarSaidas()).thenReturn(null);

        assertEquals(new BigDecimal("250.00"), saldoService.calcularSaldo());
    }

    @Test
    void calcularSaldo_deveManterPrecisaoDecimal() {
        when(entradasRepository.somarEntradas()).thenReturn(new BigDecimal("100.05"));
        when(saidasRepository.somarSaidas()).thenReturn(new BigDecimal("0.04"));

        assertEquals(new BigDecimal("100.01"), saldoService.calcularSaldo());
    }
}
