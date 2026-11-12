
package br.com.calculos.buchsilva_final0.integration;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import java.math.BigDecimal;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.transaction.annotation.Transactional;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;

import br.com.calculos.buchsilva_final0.entity.Entradas;
import br.com.calculos.buchsilva_final0.entity.Saidas;
import br.com.calculos.buchsilva_final0.repository.EntradasRepository;
import br.com.calculos.buchsilva_final0.repository.SaidasRepository;

@SpringBootTest
@AutoConfigureMockMvc
@ActiveProfiles("test")
@Transactional
@WithMockUser(roles = "ADMIN")
public class SaldoIntegrationTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private EntradasRepository entradasRepository;

    @Autowired
    private SaidasRepository saidasRepository;

    @Autowired
    private ObjectMapper objectMapper;

    // =====================================
    // PREPARAR O BANCO DE TESTES
    // =====================================

    @BeforeEach
    void prepararBanco() {

        saidasRepository.deleteAll();
        entradasRepository.deleteAll();

        saidasRepository.flush();
        entradasRepository.flush();
    }

    // =====================================
    // METODO AUXILIAR - CADASTRAR ENTRADA
    // =====================================

    private void cadastrarEntrada(String nome, String valor) {

        Entradas entrada = new Entradas();

        entrada.setNomeEntrada(nome);

        BigDecimal valorOriginal = new BigDecimal(valor);

        entrada.setValor(valorOriginal);

        // OS TESTES UTILIZAM VALORES EM USD
        entrada.setValorUsd(valorOriginal);

        entradasRepository.saveAndFlush(entrada);
    }

    // =====================================
    // METODO AUXILIAR - CADASTRAR SAIDA
    // =====================================

    private void cadastrarSaida(String nome, String valor) {

        Saidas saida = new Saidas();

        saida.setNomeSaida(nome);

        BigDecimal valorOriginal = new BigDecimal(valor);

        saida.setValor(valorOriginal);

        // OS TESTES UTILIZAM VALORES EM USD
        saida.setValorUsd(valorOriginal);

        saidasRepository.saveAndFlush(saida);
    }

    // =====================================
    // METODO AUXILIAR - CONSULTAR SALDO
    // =====================================

    private BigDecimal consultarSaldo() throws Exception {

        String resposta = mockMvc.perform(get("/api/saldo"))
                .andExpect(status().isOk())
                .andReturn()
                .getResponse()
                .getContentAsString();

        JsonNode json = objectMapper.readTree(resposta);

        // CASO A API RETORNE UM NUMERO
        if (json.isNumber()) {
            return json.decimalValue();
        }

        // CASO A API RETORNE UM OBJETO
        if (json.isObject() && json.has("saldo")) {
            return new BigDecimal(
                    json.get("saldo").asText()
            );
        }

        throw new AssertionError(
                "Formato inesperado da resposta: " + resposta
        );
    }

    // =====================================
    // TESTE 1 - SALDO SEM MOVIMENTACOES
    // =====================================

    @Test
    void deveRetornarSaldoZeroSemMovimentacoes() throws Exception {

        BigDecimal saldo = consultarSaldo();

        assertEquals(
                0,
                saldo.compareTo(new BigDecimal("0.00"))
        );
    }

    // =====================================
    // TESTE 2 - SALDO COM APENAS ENTRADAS
    // =====================================

    @Test
    void deveCalcularSaldoComApenasEntradas() throws Exception {

        cadastrarEntrada("Salário", "1500.00");

        BigDecimal saldo = consultarSaldo();

        assertEquals(
                0,
                saldo.compareTo(new BigDecimal("1500.00"))
        );
    }

    // =====================================
    // TESTE 3 - ENTRADAS MENOS SAIDAS
    // =====================================

    @Test
    void deveSubtrairSaidasDasEntradas() throws Exception {

        cadastrarEntrada("Salário", "2000.00");

        cadastrarSaida("Aluguel", "500.00");

        BigDecimal saldo = consultarSaldo();

        assertEquals(
                0,
                saldo.compareTo(new BigDecimal("1500.00"))
        );
    }

    // =====================================
    // TESTE 4 - SALDO NEGATIVO
    // =====================================

    @Test
    void deveRetornarSaldoNegativo() throws Exception {

        cadastrarEntrada("Pagamento", "300.00");

        cadastrarSaida("Despesa", "800.00");

        BigDecimal saldo = consultarSaldo();

        assertEquals(
                0,
                saldo.compareTo(new BigDecimal("-500.00"))
        );
    }

    // =====================================
    // TESTE 5 - VARIAS MOVIMENTACOES
    // =====================================

    @Test
    void deveSomarTodasAsMovimentacoes() throws Exception {

        cadastrarEntrada("Salário", "2000.00");
        cadastrarEntrada("Freelance", "1000.00");
        cadastrarEntrada("Venda", "500.00");

        cadastrarSaida("Aluguel", "900.00");
        cadastrarSaida("Internet", "150.00");
        cadastrarSaida("Energia", "200.00");

        BigDecimal saldo = consultarSaldo();

        assertEquals(
                0,
                saldo.compareTo(new BigDecimal("2250.00"))
        );
    }

    // =====================================
    // TESTE 6 - VALORES COM CENTAVOS
    // =====================================

    @Test
    void deveCalcularSaldoComCentavos() throws Exception {

        cadastrarEntrada("Venda", "150.75");

        cadastrarSaida("Compra", "50.25");

        BigDecimal saldo = consultarSaldo();

        assertEquals(
                0,
                saldo.compareTo(new BigDecimal("100.50"))
        );
    }
}
