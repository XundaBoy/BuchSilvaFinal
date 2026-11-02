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
    // IDENTIFICADOR DO REGISTRO
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
    // VENCIMENTO INFORMADO PELO USUARIO
    // =====================================

    @Column(name = "data_hora", nullable = false)
    private LocalDateTime dataHora;

    // =====================================
    // PAGAR OU RECEBER
    // =====================================

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 10)
    private TipoAgenda tipo;

    // =====================================
    // PENDENTE OU CONFIRMADO
    // =====================================

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 12)
    private StatusAgenda status = StatusAgenda.PENDENTE;

    // =====================================
    // CONSTRUTOR VAZIO PARA O JPA
    // =====================================

    public AgendaFinanceira() {
    }

    // =====================================
    // CONSTRUTOR PARA NOVO CADASTRO
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

        // TODA NOVA CONTA COMECA PENDENTE
        this.status = StatusAgenda.PENDENTE;
    }

    // =====================================
    // ATUALIZAR DADOS
    // NAO ALTERAR TIPO NEM STATUS
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
    }

    // =====================================
    // CONFIRMAR CONTA
    // =====================================

    public void confirmar() {
        this.status = StatusAgenda.CONFIRMADO;
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
}