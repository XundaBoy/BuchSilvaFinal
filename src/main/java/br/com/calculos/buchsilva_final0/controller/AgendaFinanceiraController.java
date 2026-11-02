package br.com.calculos.buchsilva_final0.controller;

import java.net.URI;
import java.util.List;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import br.com.calculos.buchsilva_final0.dto.AgendaCadastroRequest;
import br.com.calculos.buchsilva_final0.dto.AgendaEdicaoRequest;

import br.com.calculos.buchsilva_final0.entity.AgendaFinanceira;
import br.com.calculos.buchsilva_final0.entity.TipoAgenda;

import br.com.calculos.buchsilva_final0.service.AgendaFinanceiraService;

import jakarta.validation.Valid;

@RestController
@RequestMapping("/api/agenda")
public class AgendaFinanceiraController {

    private final AgendaFinanceiraService service;

    public AgendaFinanceiraController(
            AgendaFinanceiraService service) {

        this.service = service;
    }

    // =====================================
    // CADASTRAR
    // POST /api/agenda
    // =====================================

    @PostMapping
    public ResponseEntity<AgendaFinanceira> cadastrar(
            @Valid @RequestBody AgendaCadastroRequest dados) {

        AgendaFinanceira novaConta =
            service.cadastrar(dados);

        return ResponseEntity
            .created(
                URI.create(
                    "/api/agenda/" + novaConta.getId()
                )
            )
            .body(novaConta);
    }

    // =====================================
    // LISTAR POR TIPO
    // GET /api/agenda?tipo=PAGAR
    // GET /api/agenda?tipo=RECEBER
    // =====================================

    @GetMapping
    public ResponseEntity<List<AgendaFinanceira>> listar(
            @RequestParam TipoAgenda tipo) {

        return ResponseEntity.ok(
            service.listar(tipo)
        );
    }

    // =====================================
    // BUSCAR PELO ID
    // GET /api/agenda/1
    // =====================================

    @GetMapping("/{id}")
    public ResponseEntity<AgendaFinanceira> buscarPorId(
            @PathVariable Long id) {

        return ResponseEntity.ok(
            service.buscarPorId(id)
        );
    }

    // =====================================
    // EDITAR
    // PUT /api/agenda/1
    // =====================================

    @PutMapping("/{id}")
    public ResponseEntity<AgendaFinanceira> editar(
            @PathVariable Long id,
            @Valid @RequestBody AgendaEdicaoRequest dados) {

        return ResponseEntity.ok(
            service.editar(id, dados)
        );
    }

    // =====================================
    // CONFIRMAR PELO BOTAO VERDE
    // PATCH /api/agenda/1/confirmar
    // =====================================

    @PatchMapping("/{id}/confirmar")
    public ResponseEntity<AgendaFinanceira> confirmar(
            @PathVariable Long id) {

        return ResponseEntity.ok(
            service.confirmar(id)
        );
    }

    // =====================================
    // EXCLUIR
    // DELETE /api/agenda/1
    // =====================================

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> excluir(
            @PathVariable Long id) {

        service.excluir(id);

        return ResponseEntity.noContent().build();
    }
}