
package br.com.calculos.buchsilva_final0.service;

import java.math.BigDecimal;

import org.springframework.stereotype.Service;

import br.com.calculos.buchsilva_final0.repository.EntradasRepository;
import br.com.calculos.buchsilva_final0.repository.SaidasRepository;

@Service
public class SaldoService {

    private final EntradasRepository entradasRepository;
    private final SaidasRepository saidasRepository;

    public SaldoService(
            EntradasRepository entradasRepository,
            SaidasRepository saidasRepository) {

        this.entradasRepository = entradasRepository;
        this.saidasRepository = saidasRepository;
    }

    // TOTAL DE ENTRADAS EM USD
    public BigDecimal calcularTotalEntradas() {

        BigDecimal total = entradasRepository.somarEntradas();

        return total != null ? total : BigDecimal.ZERO;
    }

    // TOTAL DE SAIDAS EM USD
    public BigDecimal calcularTotalSaidas() {

        BigDecimal total = saidasRepository.somarSaidas();

        return total != null ? total : BigDecimal.ZERO;
    }

    // SALDO ACUMULADO EM USD
    public BigDecimal calcularSaldo() {

        BigDecimal entradas = calcularTotalEntradas();
        BigDecimal saidas = calcularTotalSaidas();

        return entradas.subtract(saidas);
    }
}
