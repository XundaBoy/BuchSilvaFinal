package br.com.calculos.buchsilva_final0.repository;

import java.math.BigDecimal;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import br.com.calculos.buchsilva_final0.entity.Saidas;

public interface SaidasRepository extends JpaRepository<Saidas, Long> {

    @Query("SELECT SUM(s.valor) FROM Saidas s")
    BigDecimal somarSaidas();
	
}
 