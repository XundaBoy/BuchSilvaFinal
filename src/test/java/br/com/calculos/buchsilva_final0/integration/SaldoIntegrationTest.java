
package br.com.calculos.buchsilva_final0.integration;

import static org.junit.jupiter.api.Assertions.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

import java.math.BigDecimal;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
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
public class SaldoIntegrationTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private EntradasRepository entradasRepository;

    @Autowired
    private SaidasRepository saidasRepository;

    @Autowired
    private ObjectMapper objectMapper;

    // LIMPA OS REGISTROS PARA CADA TESTE
    @BeforeEach
    void prepararBanco() {

        saidasRepository.deleteAll();
        entradasRepository.deleteAll();

        saidasRepository.flush();
        entradasRepository.flush();
    }

    // MÉTODO AUXILIAR - CADASTRAR ENTRADA
    private void cadastrarEntrada(String nome, String valor) {

        Entradas entrada = new Entradas();
        entrada.setNomeEntrada(nome);
        entrada.setValor(new BigDecimal(valor));

        entradasRepository.saveAndFlush(entrada);
    }

    // MÉTODO AUXILIAR - CADASTRAR SAÍDA
    private void cadastrarSaida(String nome, String valor) {

        Saidas saida = new Saidas();
        saida.setNomeSaida(nome);
        saida.setValor(new BigDecimal(valor));

        saidasRepository.saveAndFlush(saida);
    }

    // MÉTODO AUXILIAR - CONSULTAR SALDO PELA API
    private BigDecimal consultarSaldo() throws Exception {

        String resposta = mockMvc.perform(get("/api/saldo"))
                .andExpect(status().isOk())
                .andReturn()
                .getResponse()
                .getContentAsString();

        JsonNode json = objectMapper.readTree(resposta);

        // Caso a API retorne diretamente um número
        if (json.isNumber()) {
            return json.decimalValue();
        }

        // Caso retorne um objeto com o campo "saldo"
        if (json.isObject() && json.has("saldo")) {
            return new BigDecimal(json.get("saldo").asText());
        }

        throw new AssertionError(
                "Formato inesperado da resposta: " + resposta);
    }

    // TESTE 1 - SALDO SEM MOVIMENTAÇÕES
    @Test
    void deveRetornarSaldoZeroSemMovimentacoes() throws Exception {

        BigDecimal saldo = consultarSaldo();

        assertEquals(0,
                saldo.compareTo(new BigDecimal("0.00")));
    }

    // TESTE 2 - SALDO COM APENAS ENTRADAS
    @Test
    void deveCalcularSaldoComApenasEntradas() throws Exception {

        cadastrarEntrada("Salário", "1500.00");

        BigDecimal saldo = consultarSaldo();

        assertEquals(0,
                saldo.compareTo(new BigDecimal("1500.00")));
    }

    // TESTE 3 - SALDO COM ENTRADAS E SAÍDAS
    @Test
    void deveSubtrairSaidasDasEntradas() throws Exception {

        cadastrarEntrada("Salário", "2000.00");

        cadastrarSaida("Aluguel", "500.00");

        BigDecimal saldo = consultarSaldo();

        assertEquals(0,
                saldo.compareTo(new BigDecimal("1500.00")));
    }

    // TESTE 4 - SALDO NEGATIVO
    @Test
    void deveRetornarSaldoNegativo() throws Exception {

        cadastrarEntrada("Pagamento", "300.00");

        cadastrarSaida("Despesa", "800.00");

        BigDecimal saldo = consultarSaldo();

        assertEquals(0,
                saldo.compareTo(new BigDecimal("-500.00")));
    }

    // TESTE 5 - VÁRIAS MOVIMENTAÇÕES
    @Test
    void deveSomarTodasAsMovimentacoes() throws Exception {

        cadastrarEntrada("Salário", "2000.00");
        cadastrarEntrada("Freelance", "1000.00");
        cadastrarEntrada("Venda", "500.00");

        cadastrarSaida("Aluguel", "900.00");
        cadastrarSaida("Internet", "150.00");
        cadastrarSaida("Energia", "200.00");

        BigDecimal saldo = consultarSaldo();

        assertEquals(0,
                saldo.compareTo(new BigDecimal("2250.00")));
    }

    // TESTE 6 - VALORES DECIMAIS
    @Test
    void deveCalcularSaldoComCentavos() throws Exception {

        cadastrarEntrada("Venda", "150.75");

        cadastrarSaida("Compra", "50.25");

        BigDecimal saldo = consultarSaldo();

        assertEquals(0,
                saldo.compareTo(new BigDecimal("100.50")));
    }
}
