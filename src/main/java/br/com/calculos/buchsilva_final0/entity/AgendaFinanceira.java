
package br.com.calculos.buchsilva_final0.entity;

import java.math.BigDecimal;
import java.time.LocalDateTime;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;

@Entity
@Table(name = "agenda_financeira")
public class AgendaFinanceira {

    // =====================================
    // IDENTIFICADOR
    // =====================================

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    // =====================================
    // NOME DA CONTA
    // =====================================

    @Column(nullable = false, length = 150)
    private String nome;

    // =====================================
    // VALOR ORIGINAL, SEM CONVERSAO
    // =====================================

    @Column(
        nullable = false,
        precision = 32,
        scale = 2
    )
    private BigDecimal valor;

    // =====================================
    // MOEDA: BRL, PYG OU USD
    // =====================================

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 3)
    private MoedaAgenda moeda;

    // =====================================
    // DATA DE VENCIMENTO
    // =====================================

    @Column(name = "data_hora", nullable = false)
    private LocalDateTime dataHora;

    // =====================================
    // TIPO: PAGAR OU RECEBER
    // =====================================

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 10)
    private TipoAgenda tipo;

    // =====================================
    // STATUS: PENDENTE OU CONFIRMADO
    // =====================================

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 12)
    private StatusAgenda status = StatusAgenda.PENDENTE;

    // =====================================
    // NOVO CAMPO: DATA DE CONFIRMACAO
    // =====================================

    // Preenchido somente quando o usuario
    // clicar no V verde para confirmar.
    // Pode ser null para registros pendentes.

    @Column(name = "data_confirmacao")
    private LocalDateTime dataConfirmacao;

    // =====================================
    // CONSTRUTOR VAZIO PARA O JPA
    // =====================================

    public AgendaFinanceira() {
    }

    // =====================================
    // CONSTRUTOR PARA CADASTRO
    // =====================================

    public AgendaFinanceira(
            String nome,
            BigDecimal valor,
            MoedaAgenda moeda,
            LocalDateTime dataHora,
            TipoAgenda tipo) {

        this.nome = nome;
        this.valor = valor;
        this.moeda = moeda;
        this.dataHora = dataHora;
        this.tipo = tipo;

        // TODO NOVO REGISTRO COMECA PENDENTE
        this.status = StatusAgenda.PENDENTE;

        // AINDA NAO FOI CONFIRMADO
        this.dataConfirmacao = null;
    }

    // =====================================
    // EDITAR CONTA
    // =====================================

    public void atualizarDados(
            String nome,
            BigDecimal valor,
            MoedaAgenda moeda,
            LocalDateTime dataHora) {

        this.nome = nome;
        this.valor = valor;
        this.moeda = moeda;
        this.dataHora = dataHora;

        // PRESERVAR TIPO, STATUS E
        // DATA DE CONFIRMACAO
    }

    // =====================================
    // CONFIRMAR CONTA PELO V VERDE
    // =====================================

    public void confirmar() {

        this.status = StatusAgenda.CONFIRMADO;

        // REGISTRAR QUANDO FOI CONFIRMADA
        this.dataConfirmacao = LocalDateTime.now();
    }

    // =====================================
    // GETTERS
    // =====================================

    public Long getId() {
        return id;
    }

    public String getNome() {
        return nome;
    }

    public BigDecimal getValor() {
        return valor;
    }

    public MoedaAgenda getMoeda() {
        return moeda;
    }

    public LocalDateTime getDataHora() {
        return dataHora;
    }

    public TipoAgenda getTipo() {
        return tipo;
    }

    public StatusAgenda getStatus() {
        return status;
    }

    public LocalDateTime getDataConfirmacao() {
        return dataConfirmacao;
    }
}
