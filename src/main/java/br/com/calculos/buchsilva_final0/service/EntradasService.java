
package br.com.calculos.buchsilva_final0.service;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Locale;

import org.springframework.stereotype.Service;

import br.com.calculos.buchsilva_final0.entity.Entradas;
import br.com.calculos.buchsilva_final0.repository.EntradasRepository;

@Service
public class EntradasService {

    private final EntradasRepository entradasRepository;
    private final ConversaoMoedaService conversaoMoedaService;

    public EntradasService(
            EntradasRepository entradasRepository,
            ConversaoMoedaService conversaoMoedaService) {

        this.entradasRepository = entradasRepository;
        this.conversaoMoedaService = conversaoMoedaService;
    }

    // CADASTRAR NOVA ENTRADA
    public String save(Entradas entrada) {

        validarEntrada(entrada);

        // O banco gera o ID e a data é definida pelo servidor.
        entrada.setId(null);
        entrada.setDataHora(LocalDateTime.now());

        // Calcular a conversao antes de salvar.
        aplicarConversao(
            entrada,
            entrada.getMoedaOriginal(),
            entrada.getOrigemCotacao(),
            entrada.getCotacaoPorUsd()
        );

        entradasRepository.save(entrada);

        return "Entrada salva com sucesso";
    }

    // EDITAR ENTRADA EXISTENTE
    public String updateById(Entradas entrada, Long id) {

        validarEntrada(entrada);

        Entradas existente = entradasRepository.findById(id)
            .orElseThrow(() ->
                new RuntimeException("Entrada nao encontrada")
            );

        String moedaNova = normalizarMoeda(
            entrada.getMoedaOriginal()
        );

        boolean alterouValor =
            existente.getValor().compareTo(entrada.getValor()) != 0;

        boolean alterouMoeda =
            !moedaNova.equals(existente.getMoedaOriginal());

        // Se a origem nao for informada, preservar a atual,
        // exceto quando o usuario escolher outra moeda.
        String origemNova;

        if (entrada.getOrigemCotacao() == null ||
            entrada.getOrigemCotacao().isBlank()) {

            origemNova = alterouMoeda
                ? "AUTOMATICA"
                : existente.getOrigemCotacao();

        } else {
            origemNova = normalizarOrigem(
                entrada.getOrigemCotacao()
            );
        }

        if (origemNova == null) {
            origemNova = "AUTOMATICA";
        }

        boolean alterouOrigem =
            !origemNova.equals(existente.getOrigemCotacao());

        BigDecimal cotacaoManual = null;

        if ("MANUAL".equals(origemNova)) {

            cotacaoManual = entrada.getCotacaoPorUsd();

            // Preservar a cotacao manual anterior, caso
            // a moeda continue igual e nenhuma nova taxa
            // tenha sido informada.
            if (cotacaoManual == null &&
                !alterouMoeda &&
                "MANUAL".equals(existente.getOrigemCotacao())) {

                cotacaoManual = existente.getCotacaoPorUsd();
            }
        }

        boolean alterouCotacaoManual = false;

        if ("MANUAL".equals(origemNova) &&
            cotacaoManual != null) {

            alterouCotacaoManual =
                existente.getCotacaoPorUsd() == null ||
                cotacaoManual.compareTo(
                    existente.getCotacaoPorUsd()
                ) != 0;
        }

        // Atualizar sempre descricao e valor.
        existente.setNomeEntrada(entrada.getNomeEntrada());
        existente.setValor(entrada.getValor());

        // Recalcular apenas quando houver alteracao financeira.
        if (alterouValor ||
            alterouMoeda ||
            alterouOrigem ||
            alterouCotacaoManual) {

            aplicarConversao(
                existente,
                moedaNova,
                origemNova,
                cotacaoManual
            );
        }

        // A data original da entrada permanece inalterada.
        entradasRepository.save(existente);

        return "Entrada atualizada com sucesso";
    }

    // APLICAR RESULTADO DA CONVERSAO
    private void aplicarConversao(
            Entradas entrada,
            String moeda,
            String origem,
            BigDecimal cotacaoInformada) {

        String moedaNormalizada = normalizarMoeda(moeda);
        String origemNormalizada = normalizarOrigem(origem);

        BigDecimal cotacaoManual = null;

        if ("MANUAL".equals(origemNormalizada)) {

            if (cotacaoInformada == null) {
                throw new IllegalArgumentException(
                    "Informe a cotacao manual"
                );
            }

            cotacaoManual = cotacaoInformada;
        }

        var resultado = conversaoMoedaService.converter(
            entrada.getValor(),
            moedaNormalizada,
            cotacaoManual
        );

        entrada.setMoedaOriginal(moedaNormalizada);
        entrada.setCotacaoPorUsd(resultado.cotacaoPorUsd());
        entrada.setValorUsd(resultado.valorUsd());
        entrada.setOrigemCotacao(resultado.origemCotacao());
        entrada.setDataCotacao(resultado.dataCotacao());
    }

    // VALIDAR OS DADOS
    private void validarEntrada(Entradas entrada) {

        if (entrada == null ||
            entrada.getNomeEntrada() == null ||
            entrada.getNomeEntrada().isBlank()) {

            throw new IllegalArgumentException(
                "O nome da entrada e obrigatorio"
            );
        }

        if (entrada.getValor() == null ||
            entrada.getValor().signum() <= 0) {

            throw new IllegalArgumentException(
                "O valor deve ser maior que zero"
            );
        }

        normalizarMoeda(entrada.getMoedaOriginal());
    }

    private String normalizarMoeda(String moeda) {

        if (moeda == null || moeda.isBlank()) {
            throw new IllegalArgumentException(
                "Selecione a moeda: BRL, PYG ou USD"
            );
        }

        String resultado = moeda.trim()
            .toUpperCase(Locale.ROOT);

        if (!resultado.equals("BRL") &&
            !resultado.equals("PYG") &&
            !resultado.equals("USD")) {

            throw new IllegalArgumentException(
                "Moeda invalida: " + resultado
            );
        }

        return resultado;
    }

    private String normalizarOrigem(String origem) {

        if (origem == null || origem.isBlank()) {
            return "AUTOMATICA";
        }

        String resultado = origem.trim()
            .toUpperCase(Locale.ROOT);

        if (!resultado.equals("AUTOMATICA") &&
            !resultado.equals("MANUAL")) {

            throw new IllegalArgumentException(
                "Origem de cotacao invalida"
            );
        }

        return resultado;
    }

    // EXCLUIR ENTRADA
    public String deleteById(Long id) {

        if (!entradasRepository.existsById(id)) {
            throw new RuntimeException(
                "Entrada nao encontrada"
            );
        }

        entradasRepository.deleteById(id);

        return "Entrada deletada com sucesso";
    }

    // LISTAR ENTRADAS
    public List<Entradas> findAll() {
        return entradasRepository.findAll();
    }
}
