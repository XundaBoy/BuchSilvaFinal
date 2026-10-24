
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

import br.com.calculos.buchsilva_final0.entity.Entradas;
import br.com.calculos.buchsilva_final0.repository.EntradasRepository;

@SpringBootTest
@AutoConfigureMockMvc
@ActiveProfiles("test")
@Transactional
public class EntradasIntegrationTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private EntradasRepository entradasRepository;

    // TESTE 1 - CADASTRAR ENTRADA
    @Test
    void deveCadastrarEntrada() throws Exception {

        long antes = entradasRepository.count();

        String json = """
                {
                    "nomeEntrada": "Salário",
                    "valor": 2500.50
                }
                """;

        mockMvc.perform(post("/api/entradas/save")
                .contentType(MediaType.APPLICATION_JSON)
                .content(json))
                .andExpect(status().isOk());

        assertEquals(antes + 1, entradasRepository.count());
    }

    // TESTE 2 - ATUALIZAR ENTRADA
    @Test
    void deveAtualizarEntradaPreservandoData() throws Exception {

        Entradas entrada = new Entradas();
        entrada.setNomeEntrada("Salário");
        entrada.setValor(new BigDecimal("2500.00"));

        LocalDateTime dataOriginal =
                LocalDateTime.of(2026, 10, 20, 10, 30);

        entrada.setDataHora(dataOriginal);
        entrada = entradasRepository.save(entrada);

        String json = """
                {
                    "nomeEntrada": "Salário atualizado",
                    "valor": 3000.00
                }
                """;

        mockMvc.perform(put("/api/entradas/updateById/" + entrada.getId())
                .contentType(MediaType.APPLICATION_JSON)
                .content(json))
                .andExpect(status().isOk());

        Entradas atualizada = entradasRepository
                .findById(entrada.getId())
                .orElseThrow();

        assertEquals("Salário atualizado",
                atualizada.getNomeEntrada());

        assertEquals(0, atualizada.getValor()
                .compareTo(new BigDecimal("3000.00")));

        assertEquals(dataOriginal, atualizada.getDataHora());
    }

    // TESTE 3 - EXCLUIR ENTRADA
    @Test
    void deveExcluirEntrada() throws Exception {

        Entradas entrada = new Entradas();
        entrada.setNomeEntrada("Entrada para excluir");
        entrada.setValor(new BigDecimal("100.00"));

        entrada = entradasRepository.save(entrada);

        Long id = entrada.getId();

        mockMvc.perform(delete("/api/entradas/deleteById/" + id))
                .andExpect(status().isOk());

        assertFalse(entradasRepository.existsById(id));
    }

    // TESTE 4 - LISTAR ENTRADAS
    @Test
    void deveListarEntradas() throws Exception {

        Entradas entrada = new Entradas();
        entrada.setNomeEntrada("Entrada listada");
        entrada.setValor(new BigDecimal("150.00"));

        entradasRepository.save(entrada);

        mockMvc.perform(get("/api/entradas/findAll"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[*].nomeEntrada",
                        hasItem("Entrada listada")));
    }

    // TESTE 5 - REJEITAR NOME VAZIO
    @Test
    void naoDeveCadastrarNomeVazio() throws Exception {

        String json = """
                {
                    "nomeEntrada": "",
                    "valor": 150.00
                }
                """;

        long antes = entradasRepository.count();

        mockMvc.perform(post("/api/entradas/save")
                .contentType(MediaType.APPLICATION_JSON)
                .content(json))
                .andExpect(status().isBadRequest());

        assertEquals(antes, entradasRepository.count());
    }

    // TESTE 6 - REJEITAR VALOR NEGATIVO
    @Test
    void naoDeveCadastrarValorNegativo() throws Exception {

        String json = """
                {
                    "nomeEntrada": "Teste negativo",
                    "valor": -100.00
                }
                """;

        long antes = entradasRepository.count();

        mockMvc.perform(post("/api/entradas/save")
                .contentType(MediaType.APPLICATION_JSON)
                .content(json))
                .andExpect(status().isBadRequest());

        assertEquals(antes, entradasRepository.count());
    }

    // TESTE 7 - REJEITAR MAIS DE DUAS CASAS DECIMAIS
    @Test
    void naoDeveCadastrarValorComTresCasas() throws Exception {

        String json = """
                {
                    "nomeEntrada": "Teste decimal",
                    "valor": 24.999
                }
                """;

        long antes = entradasRepository.count();

        mockMvc.perform(post("/api/entradas/save")
                .contentType(MediaType.APPLICATION_JSON)
                .content(json))
                .andExpect(status().isBadRequest());

        assertEquals(antes, entradasRepository.count());
    }

    // TESTE 8 - ATUALIZAR ID INEXISTENTE
    @Test
    void naoDeveAtualizarIdInexistente() throws Exception {

        String json = """
                {
                    "nomeEntrada": "Entrada inexistente",
                    "valor": 200.00
                }
                """;

        mockMvc.perform(put("/api/entradas/updateById/999999")
                .contentType(MediaType.APPLICATION_JSON)
                .content(json))
                .andExpect(status().isBadRequest());
    }
}
