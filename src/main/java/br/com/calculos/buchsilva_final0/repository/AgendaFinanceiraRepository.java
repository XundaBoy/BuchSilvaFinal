package br.com.calculos.buchsilva_final0.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;

import br.com.calculos.buchsilva_final0.entity.AgendaFinanceira;
import br.com.calculos.buchsilva_final0.entity.TipoAgenda;

public interface AgendaFinanceiraRepository
        extends JpaRepository<AgendaFinanceira, Long> {

    // LISTAR SOMENTE PAGAR OU RECEBER
    // ORDENADO PELO VENCIMENTO MAIS PROXIMO

    List<AgendaFinanceira> findByTipoOrderByDataHoraAsc(
        TipoAgenda tipo
    );
}