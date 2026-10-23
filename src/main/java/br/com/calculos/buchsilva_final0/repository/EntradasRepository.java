package br.com.calculos.buchsilva_final0.repository;

import java.math.BigDecimal;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import br.com.calculos.buchsilva_final0.entity.Entradas;

public interface EntradasRepository extends JpaRepository<Entradas, Long>{
	
	 @Query("SELECT SUM(e.valor) FROM Entradas e")
	    BigDecimal somarEntradas();
}
