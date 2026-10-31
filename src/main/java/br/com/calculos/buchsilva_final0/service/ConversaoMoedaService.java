
package br.com.calculos.buchsilva_final0.service;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDate;
import java.util.Locale;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

@Service
public class ConversaoMoedaService {

    private final BigDecimal cotacaoBrl;
    private final BigDecimal cotacaoPyg;

    // COTACOES DEFINIDAS NO APPLICATION.PROPERTIES
    public ConversaoMoedaService(
            @Value("${app.cambio.brl-por-usd}") BigDecimal cotacaoBrl,
            @Value("${app.cambio.pyg-por-usd}") BigDecimal cotacaoPyg) {

        this.cotacaoBrl = cotacaoBrl;
        this.cotacaoPyg = cotacaoPyg;

        if (cotacaoBrl.signum() <= 0 || cotacaoPyg.signum() <= 0) {
            throw new IllegalArgumentException(
                "As cotacoes configuradas devem ser maiores que zero"
            );
        }
    }

    // RESULTADO QUE SERA SALVO EM ENTRADAS OU SAIDAS
    public record ResultadoConversao(
        BigDecimal cotacaoPorUsd,
        BigDecimal valorUsd,
        String origemCotacao,
        LocalDate dataCotacao
    ) {}

    // CONVERSAO PRINCIPAL
    public ResultadoConversao converter(
            BigDecimal valorOriginal,
            String moedaOriginal,
            BigDecimal cotacaoManual) {

        // VALIDAR O VALOR
        if (valorOriginal == null ||
                valorOriginal.signum() <= 0) {

            throw new IllegalArgumentException(
                "O valor deve ser maior que zero"
            );
        }

        // VALIDAR A MOEDA
        if (moedaOriginal == null ||
                moedaOriginal.isBlank()) {

            throw new IllegalArgumentException(
                "Selecione uma moeda: BRL, PYG ou USD"
            );
        }

        String moeda = moedaOriginal
                .trim()
                .toUpperCase(Locale.ROOT);

        BigDecimal cotacao;
        String origem;

        // DEFINIR A COTACAO
        switch (moeda) {

            case "BRL":
                cotacao = cotacaoBrl;
                break;

            case "PYG":
                cotacao = cotacaoPyg;
                break;

            case "USD":
                cotacao = BigDecimal.ONE;
                break;

            default:
                throw new IllegalArgumentException(
                    "Moeda invalida: " + moeda
                );
        }

        // COTACAO MANUAL
        if (cotacaoManual != null) {

            if (moeda.equals("USD")) {
                throw new IllegalArgumentException(
                    "USD nao permite cotacao manual"
                );
            }

            if (cotacaoManual.signum() <= 0) {
                throw new IllegalArgumentException(
                    "A cotacao manual deve ser maior que zero"
                );
            }

            cotacao = cotacaoManual;
            origem = "MANUAL";

        } else {
            origem = "AUTOMATICA";
        }

        // CONVERTER PARA DOLAR
        BigDecimal valorUsd = valorOriginal.divide(
            cotacao,
            2,
            RoundingMode.HALF_UP
        );

        // DEVOLVER O RESULTADO
        return new ResultadoConversao(
            cotacao,
            valorUsd,
            origem,
            LocalDate.now()
        );
    }
}
