
package br.com.calculos.buchsilva_final0.integration;

import static org.junit.jupiter.api.Assertions.*;
import static org.hamcrest.Matchers.hasItem;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

import java.math.BigDecimal;
import java.time.LocalDateTime;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.transaction.annotation.Transactional;

import br.com.calculos.buchsilva_final0.entity.Saidas;
import br.com.calculos.buchsilva_final0.repository.SaidasRepository;

import jakarta.persistence.EntityManager;

@SpringBootTest
@AutoConfigureMockMvc
@ActiveProfiles("test")
@Transactional
public class SaidasIntegrationTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private SaidasRepository saidasRepository;

    @Autowired
    private EntityManager entityManager;

    // TESTE 1 - CADASTRAR SAIDA
    @Test
    void deveCadastrarSaida() throws Exception {

        long antes = saidasRepository.count();

        String json = """
                {
                    "nomeSaida": "Conta de luz",
                    "valor": 250.50
                }
                """;

        mockMvc.perform(post("/api/saidas/save")
                .contentType(MediaType.APPLICATION_JSON)
                .content(json))
                .andExpect(status().isOk());

        assertEquals(antes + 1, saidasRepository.count());
    }

    // TESTE 2 - ATUALIZAR SAIDA
    @Test
    void deveAtualizarSaidaPreservandoData() throws Exception {

        Saidas saida = new Saidas();
        saida.setNomeSaida("Conta de luz");
        saida.setValor(new BigDecimal("250.00"));

        LocalDateTime dataOriginal =
                LocalDateTime.of(2026, 10, 20, 10, 30);

        saida.setDataHora(dataOriginal);
        saida = saidasRepository.saveAndFlush(saida);

        Long id = saida.getId();
        dataOriginal = saida.getDataHora();

        entityManager.clear();

        String json = """
                {
                    "nomeSaida": "Conta de água",
                    "valor": 180.00
                }
                """;

        mockMvc.perform(put("/api/saidas/updateById/" + id)
                .contentType(MediaType.APPLICATION_JSON)
                .content(json))
                .andExpect(status().isOk());

        entityManager.flush();
        entityManager.clear();

        Saidas atualizada = saidasRepository
                .findById(id)
                .orElseThrow();

        assertEquals("Conta de água",
                atualizada.getNomeSaida());

        assertEquals(0, atualizada.getValor()
                .compareTo(new BigDecimal("180.00")));

        assertEquals(dataOriginal, atualizada.getDataHora());
    }

    // TESTE 3 - EXCLUIR SAIDA
    @Test
    void deveExcluirSaida() throws Exception {

        Saidas saida = new Saidas();
        saida.setNomeSaida("Saída para excluir");
        saida.setValor(new BigDecimal("100.00"));

        saida = saidasRepository.save(saida);

        Long id = saida.getId();

        mockMvc.perform(delete("/api/saidas/deleteById/" + id))
                .andExpect(status().isOk());

        assertFalse(saidasRepository.existsById(id));
    }

    // TESTE 4 - LISTAR SAIDAS
    @Test
    void deveListarSaidas() throws Exception {

        Saidas saida = new Saidas();
        saida.setNomeSaida("Saída listada");
        saida.setValor(new BigDecimal("150.00"));

        saidasRepository.save(saida);

        mockMvc.perform(get("/api/saidas/findAll"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[*].nomeSaida",
                        hasItem("Saída listada")));
    }

    // TESTE 5 - REJEITAR NOME VAZIO
    @Test
    void naoDeveCadastrarNomeVazio() throws Exception {

        String json = """
                {
                    "nomeSaida": "",
                    "valor": 150.00
                }
                """;

        long antes = saidasRepository.count();

        mockMvc.perform(post("/api/saidas/save")
                .contentType(MediaType.APPLICATION_JSON)
                .content(json))
                .andExpect(status().isBadRequest());

        assertEquals(antes, saidasRepository.count());
    }

    // TESTE 6 - REJEITAR VALOR NEGATIVO
    @Test
    void naoDeveCadastrarValorNegativo() throws Exception {

        String json = """
                {
                    "nomeSaida": "Teste negativo",
                    "valor": -100.00
                }
                """;

        long antes = saidasRepository.count();

        mockMvc.perform(post("/api/saidas/save")
                .contentType(MediaType.APPLICATION_JSON)
                .content(json))
                .andExpect(status().isBadRequest());

        assertEquals(antes, saidasRepository.count());
    }

    // TESTE 7 - REJEITAR MAIS DE DUAS CASAS DECIMAIS
    @Test
    void naoDeveCadastrarValorComTresCasas() throws Exception {

        String json = """
                {
                    "nomeSaida": "Teste decimal",
                    "valor": 24.999
                }
                """;

        long antes = saidasRepository.count();

        mockMvc.perform(post("/api/saidas/save")
                .contentType(MediaType.APPLICATION_JSON)
                .content(json))
                .andExpect(status().isBadRequest());

        assertEquals(antes, saidasRepository.count());
    }

    // TESTE 8 - ATUALIZAR ID INEXISTENTE
    @Test
    void naoDeveAtualizarIdInexistente() throws Exception {

        String json = """
                {
                    "nomeSaida": "Saída inexistente",
                    "valor": 200.00
                }
                """;

        mockMvc.perform(put("/api/saidas/updateById/999999")
                .contentType(MediaType.APPLICATION_JSON)
                .content(json))
                .andExpect(status().isBadRequest());

    }
}
