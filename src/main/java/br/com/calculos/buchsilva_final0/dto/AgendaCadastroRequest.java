package br.com.calculos.buchsilva_final0.dto;

import java.math.BigDecimal;
import java.time.LocalDateTime;

import br.com.calculos.buchsilva_final0.entity.MoedaAgenda;
import br.com.calculos.buchsilva_final0.entity.TipoAgenda;

import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.Digits;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

public record AgendaCadastroRequest(

    @NotBlank(message = "Informe o nome da conta")
    @Size(
        max = 150,
        message = "O nome deve ter no maximo 150 caracteres"
    )
    String nome,

    @NotNull(message = "Informe o valor")
    @DecimalMin(
        value = "0.01",
        message = "O valor deve ser maior que zero"
    )
    @Digits(
        integer = 30,
        fraction = 2,
        message = "O valor deve ter no maximo 2 casas decimais"
    )
    BigDecimal valor,

    @NotNull(message = "Selecione a moeda")
    MoedaAgenda moeda,

    @NotNull(message = "Informe a data e hora")
    LocalDateTime dataHora,

    @NotNull(message = "Informe o tipo da agenda")
    TipoAgenda tipo

) {
}