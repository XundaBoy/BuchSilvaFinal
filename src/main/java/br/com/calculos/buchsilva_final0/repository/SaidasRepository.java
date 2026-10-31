
package br.com.calculos.buchsilva_final0.repository;

import java.math.BigDecimal;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import br.com.calculos.buchsilva_final0.entity.Saidas;

public interface SaidasRepository
        extends JpaRepository<Saidas, Long> {

    // SOMAR AS SAIDAS CONVERTIDAS PARA DOLAR
    @Query("SELECT SUM(s.valorUsd) FROM Saidas s")
    BigDecimal somarSaidas();

}
