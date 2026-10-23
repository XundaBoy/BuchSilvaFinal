package br.com.calculos.buchsilva_final0.service;

import java.math.BigDecimal;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import br.com.calculos.buchsilva_final0.repository.EntradasRepository;
import br.com.calculos.buchsilva_final0.repository.SaidasRepository;

@Service
public class SaldoService {

	 @Autowired
	    private EntradasRepository entradasRepository;

	    @Autowired
	    private SaidasRepository saidasRepository;

	    public BigDecimal calcularSaldo() {

	        BigDecimal entradas = entradasRepository.somarEntradas();
	        BigDecimal saidas = saidasRepository.somarSaidas();

	        if (entradas == null) {
	            entradas = BigDecimal.ZERO;
	        }

	        if (saidas == null) {
	            saidas = BigDecimal.ZERO;
	        }

	        return entradas.subtract(saidas);
	    }
	
}
