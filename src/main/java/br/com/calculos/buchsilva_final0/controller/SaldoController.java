package br.com.calculos.buchsilva_final0.controller;

import java.math.BigDecimal;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import br.com.calculos.buchsilva_final0.service.SaldoService;

@RestController
@RequestMapping("/api/saldo")
public class SaldoController {

	 @Autowired
	    private SaldoService saldoService;

	    @GetMapping
	    public ResponseEntity<BigDecimal> consultarSaldo() {

	        BigDecimal saldo = this.saldoService.calcularSaldo();

	        return new ResponseEntity<>(saldo, HttpStatus.OK);
	    }
	
}
