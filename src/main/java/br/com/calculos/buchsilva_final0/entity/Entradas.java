
package br.com.calculos.buchsilva_final0.entity;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;

import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.Digits;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

@Entity
public class Entradas {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @NotBlank(message = "O nome da entrada é obrigatório")
    private String nomeEntrada;

    private LocalDateTime dataHora = LocalDateTime.now();

    // VALOR ORIGINAL NA MOEDA ESCOLHIDA
    @NotNull(message = "O valor é obrigatório")
    @DecimalMin(
        value = "0.01",
        message = "O valor deve ser maior que zero"
    )
    @Digits(
        integer = 30,
        fraction = 2,
        message = "O valor deve ter no máximo 2 casas decimais"
    )
    @Column(precision = 32, scale = 2)
    private BigDecimal valor;

    // BRL, PYG OU USD
    @Column(name = "moeda_original", length = 3)
    private String moedaOriginal;

    // QUANTAS UNIDADES DA MOEDA EQUIVALEM A 1 USD
    @Column(name = "cotacao_por_usd", precision = 20, scale = 8)
    private BigDecimal cotacaoPorUsd;

    // VALOR CONVERTIDO PARA DOLAR
    @Column(name = "valor_usd", precision = 32, scale = 2)
    private BigDecimal valorUsd;

    // AUTOMATICA, MANUAL OU MIGRACAO
    @Column(name = "origem_cotacao", length = 20)
    private String origemCotacao;

    // DATA DE REFERENCIA DA COTACAO
    @Column(name = "data_cotacao")
    private LocalDate dataCotacao;

    // CONSTRUTOR VAZIO
    public Entradas() {
    }

    // CONSTRUTOR ORIGINAL
    public Entradas(
            Long id,
            String nomeEntrada,
            LocalDateTime dataHora,
            BigDecimal valor) {

        this.id = id;
        this.nomeEntrada = nomeEntrada;
        this.dataHora = dataHora;
        this.valor = valor;
    }

    // GETTERS E SETTERS

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getNomeEntrada() {
        return nomeEntrada;
    }

    public void setNomeEntrada(String nomeEntrada) {
        this.nomeEntrada = nomeEntrada;
    }

    public LocalDateTime getDataHora() {
        return dataHora;
    }

    public void setDataHora(LocalDateTime dataHora) {
        this.dataHora = dataHora;
    }

    public BigDecimal getValor() {
        return valor;
    }

    public void setValor(BigDecimal valor) {
        this.valor = valor;
    }

    public String getMoedaOriginal() {
        return moedaOriginal;
    }

    public void setMoedaOriginal(String moedaOriginal) {
        this.moedaOriginal = moedaOriginal;
    }

    public BigDecimal getCotacaoPorUsd() {
        return cotacaoPorUsd;
    }

    public void setCotacaoPorUsd(BigDecimal cotacaoPorUsd) {
        this.cotacaoPorUsd = cotacaoPorUsd;
    }

    public BigDecimal getValorUsd() {
        return valorUsd;
    }

    public void setValorUsd(BigDecimal valorUsd) {
        this.valorUsd = valorUsd;
    }

    public String getOrigemCotacao() {
        return origemCotacao;
    }

    public void setOrigemCotacao(String origemCotacao) {
        this.origemCotacao = origemCotacao;
    }

    public LocalDate getDataCotacao() {
        return dataCotacao;
    }

    public void setDataCotacao(LocalDate dataCotacao) {
        this.dataCotacao = dataCotacao;
    }
}
