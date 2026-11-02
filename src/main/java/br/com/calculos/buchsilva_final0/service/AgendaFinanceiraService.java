package br.com.calculos.buchsilva_final0.service;

import java.util.List;

import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;

import br.com.calculos.buchsilva_final0.dto.AgendaCadastroRequest;
import br.com.calculos.buchsilva_final0.dto.AgendaEdicaoRequest;

import br.com.calculos.buchsilva_final0.entity.AgendaFinanceira;
import br.com.calculos.buchsilva_final0.entity.StatusAgenda;
import br.com.calculos.buchsilva_final0.entity.TipoAgenda;

import br.com.calculos.buchsilva_final0.repository.AgendaFinanceiraRepository;

@Service
public class AgendaFinanceiraService {

    private final AgendaFinanceiraRepository repository;

    public AgendaFinanceiraService(
            AgendaFinanceiraRepository repository) {

        this.repository = repository;
    }

    // =====================================
    // CADASTRAR CONTA
    // SEMPRE COM STATUS PENDENTE
    // =====================================

    @Transactional
    public AgendaFinanceira cadastrar(
            AgendaCadastroRequest dados) {

        AgendaFinanceira novaConta =
            new AgendaFinanceira(
                dados.nome().trim(),
                dados.valor(),
                dados.moeda(),
                dados.dataHora(),
                dados.tipo()
            );

        return repository.save(novaConta);
    }

    // =====================================
    // LISTAR CONTAS PELO TIPO
    // PAGAR OU RECEBER
    // =====================================

    @Transactional(readOnly = true)
    public List<AgendaFinanceira> listar(
            TipoAgenda tipo) {

        return repository.findByTipoOrderByDataHoraAsc(tipo);
    }

    // =====================================
    // BUSCAR UMA CONTA PELO ID
    // =====================================

    @Transactional(readOnly = true)
    public AgendaFinanceira buscarPorId(Long id) {

        return repository.findById(id)
            .orElseThrow(() ->
                new ResponseStatusException(
                    HttpStatus.NOT_FOUND,
                    "Conta da agenda nao encontrada"
                )
            );
    }

    // =====================================
    // EDITAR CONTA
    // PRESERVAR TIPO E STATUS
    // =====================================

    @Transactional
    public AgendaFinanceira editar(
            Long id,
            AgendaEdicaoRequest dados) {

        AgendaFinanceira existente =
            repository.findById(id)
                .orElseThrow(() ->
                    new ResponseStatusException(
                        HttpStatus.NOT_FOUND,
                        "Conta da agenda nao encontrada"
                    )
                );

        existente.atualizarDados(
            dados.nome().trim(),
            dados.valor(),
            dados.moeda(),
            dados.dataHora()
        );

        return repository.save(existente);
    }

    // =====================================
    // CONFIRMAR CONTA
    // PENDENTE -> CONFIRMADO
    // =====================================

    @Transactional
    public AgendaFinanceira confirmar(Long id) {

        AgendaFinanceira existente =
            repository.findById(id)
                .orElseThrow(() ->
                    new ResponseStatusException(
                        HttpStatus.NOT_FOUND,
                        "Conta da agenda nao encontrada"
                    )
                );

        if (existente.getStatus() == StatusAgenda.CONFIRMADO) {

            throw new ResponseStatusException(
                HttpStatus.CONFLICT,
                "Esta conta ja foi confirmada"
            );
        }

        existente.confirmar();

        return repository.save(existente);
    }

    // =====================================
    // EXCLUIR CONTA
    // =====================================

    @Transactional
    public void excluir(Long id) {

        AgendaFinanceira existente =
            repository.findById(id)
                .orElseThrow(() ->
                    new ResponseStatusException(
                        HttpStatus.NOT_FOUND,
                        "Conta da agenda nao encontrada"
                    )
                );

        repository.delete(existente);
    }
}