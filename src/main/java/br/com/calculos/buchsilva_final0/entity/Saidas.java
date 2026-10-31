
package br.com.calculos.buchsilva_final0.entity;

import java.math.BigDecimal;
import java.time.LocalDateTime;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;

import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.Digits;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

@Entity
public class Saidas {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @NotBlank(message = "O nome da saída é obrigatório")
    private String nomeSaida;

    private LocalDateTime dataHora = LocalDateTime.now();

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
    private BigDecimal valor;

    // CONSTRUTOR VAZIO
    public Saidas() {
    }

    // CONSTRUTOR COMPLETO
    public Saidas(
            Long id,
            String nomeSaida,
            LocalDateTime dataHora,
            BigDecimal valor) {

        this.id = id;
        this.nomeSaida = nomeSaida;
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

    public String getNomeSaida() {
        return nomeSaida;
    }

    public void setNomeSaida(String nomeSaida) {
        this.nomeSaida = nomeSaida;
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
}
