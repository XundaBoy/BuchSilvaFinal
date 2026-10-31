
package br.com.calculos.buchsilva_final0.service;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Locale;

import org.springframework.stereotype.Service;

import br.com.calculos.buchsilva_final0.entity.Saidas;
import br.com.calculos.buchsilva_final0.repository.SaidasRepository;

@Service
public class SaidasService {

    private final SaidasRepository saidasRepository;
    private final ConversaoMoedaService conversaoMoedaService;

    public SaidasService(
            SaidasRepository saidasRepository,
            ConversaoMoedaService conversaoMoedaService) {

        this.saidasRepository = saidasRepository;
        this.conversaoMoedaService = conversaoMoedaService;
    }

    // CADASTRAR NOVA SAIDA
    public String save(Saidas saida) {

        validarSaida(saida);

        saida.setId(null);
        saida.setDataHora(LocalDateTime.now());

        aplicarConversao(
            saida,
            saida.getMoedaOriginal(),
            saida.getOrigemCotacao(),
            saida.getCotacaoPorUsd()
        );

        saidasRepository.save(saida);

        return "Saida salva com sucesso";
    }

    // EDITAR SAIDA EXISTENTE
    public String updateById(Saidas saida, Long id) {

        validarSaida(saida);

        Saidas existente = saidasRepository.findById(id)
            .orElseThrow(() ->
                new RuntimeException("Saida nao encontrada")
            );

        String moedaNova = normalizarMoeda(
            saida.getMoedaOriginal()
        );

        boolean alterouValor =
            existente.getValor().compareTo(saida.getValor()) != 0;

        boolean alterouMoeda =
            !moedaNova.equals(existente.getMoedaOriginal());

        String origemNova;

        if (saida.getOrigemCotacao() == null ||
            saida.getOrigemCotacao().isBlank()) {

            origemNova = alterouMoeda
                ? "AUTOMATICA"
                : existente.getOrigemCotacao();

        } else {
            origemNova = normalizarOrigem(
                saida.getOrigemCotacao()
            );
        }

        if (origemNova == null) {
            origemNova = "AUTOMATICA";
        }

        boolean alterouOrigem =
            !origemNova.equals(existente.getOrigemCotacao());

        BigDecimal cotacaoManual = null;

        if ("MANUAL".equals(origemNova)) {

            cotacaoManual = saida.getCotacaoPorUsd();

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

        existente.setNomeSaida(saida.getNomeSaida());
        existente.setValor(saida.getValor());

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

        // Preserva a data original da saida.
        saidasRepository.save(existente);

        return "Saida atualizada com sucesso";
    }

    // APLICAR RESULTADO DA CONVERSAO
    private void aplicarConversao(
            Saidas saida,
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
            saida.getValor(),
            moedaNormalizada,
            cotacaoManual
        );

        saida.setMoedaOriginal(moedaNormalizada);
        saida.setCotacaoPorUsd(resultado.cotacaoPorUsd());
        saida.setValorUsd(resultado.valorUsd());
        saida.setOrigemCotacao(resultado.origemCotacao());
        saida.setDataCotacao(resultado.dataCotacao());
    }

    // VALIDAR OS DADOS
    private void validarSaida(Saidas saida) {

        if (saida == null ||
            saida.getNomeSaida() == null ||
            saida.getNomeSaida().isBlank()) {

            throw new IllegalArgumentException(
                "O nome da saida e obrigatorio"
            );
        }

        if (saida.getValor() == null ||
            saida.getValor().signum() <= 0) {

            throw new IllegalArgumentException(
                "O valor deve ser maior que zero"
            );
        }

        normalizarMoeda(saida.getMoedaOriginal());
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

    // EXCLUIR SAIDA
    public String deleteById(Long id) {

        if (!saidasRepository.existsById(id)) {
            throw new RuntimeException(
                "Saida nao encontrada"
            );
        }

        saidasRepository.deleteById(id);

        return "Saida deletada com sucesso";
    }

    // LISTAR SAIDAS
    public List<Saidas> findAll() {
        return saidasRepository.findAll();
    }
}
