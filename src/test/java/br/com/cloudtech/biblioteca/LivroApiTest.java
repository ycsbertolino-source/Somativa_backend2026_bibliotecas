package br.com.cloudtech.biblioteca;

import com.jayway.jsonpath.JsonPath;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;

import java.nio.charset.StandardCharsets;

import static org.hamcrest.Matchers.greaterThanOrEqualTo;
import static org.hamcrest.Matchers.hasItems;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

/**
 * Testes automatizados dos endpoints com JUnit 5 + MockMvc.
 */
@SpringBootTest
@AutoConfigureMockMvc
class LivroApiTest {

    @Autowired
    private MockMvc mockMvc;

    private static final String LIVRO_JSON = """
            {
              "titulo": "Banco de Dados para Iniciantes",
              "autor": "Maria Souza",
              "editora": "EduTech",
              "ano": 2023,
              "categoria": "Informatica",
              "status": "Disponivel"
            }
            """;

    /** Cadastra um livro e devolve o id gerado. */
    private int cadastrarLivro() throws Exception {
        MvcResult resultado = mockMvc.perform(post("/livros/cadastrar")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(LIVRO_JSON))
                .andExpect(status().isCreated())
                .andReturn();
        return JsonPath.read(resultado.getResponse().getContentAsString(StandardCharsets.UTF_8), "$.id");
    }

    @Test
    @DisplayName("1. GET / retorna HTTP 200")
    void paginaInicial() throws Exception {
        mockMvc.perform(get("/"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.mensagem").exists());
    }

    @Test
    @DisplayName("2. GET /livros retorna lista e HTTP 200")
    void listarLivros() throws Exception {
        mockMvc.perform(get("/livros"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$").isArray())
                .andExpect(jsonPath("$.length()", greaterThanOrEqualTo(1)));
    }

    @Test
    @DisplayName("3. POST /livros/cadastrar retorna HTTP 201")
    void cadastrarLivro_retorna201() throws Exception {
        mockMvc.perform(post("/livros/cadastrar")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(LIVRO_JSON))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.id").exists())
                .andExpect(jsonPath("$.titulo").value("Banco de Dados para Iniciantes"))
                .andExpect(jsonPath("$.autor").value("Maria Souza"))
                .andExpect(jsonPath("$.ano").value(2023));
    }

    @Test
    @DisplayName("4. GET /livros/{id} retorna os dados do livro")
    void consultarPorId() throws Exception {
        int id = cadastrarLivro();
        mockMvc.perform(get("/livros/" + id))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(id))
                .andExpect(jsonPath("$.titulo").value("Banco de Dados para Iniciantes"));
    }

    @Test
    @DisplayName("5. PUT /livros/alterar/{id} atualiza o livro")
    void alterarLivro() throws Exception {
        int id = cadastrarLivro();
        String alterado = """
                {
                  "titulo": "Banco de Dados Essencial",
                  "autor": "Maria Souza",
                  "editora": "EduTech",
                  "ano": 2024,
                  "categoria": "Banco de Dados",
                  "status": "Emprestado"
                }
                """;
        mockMvc.perform(put("/livros/alterar/" + id)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(alterado))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.titulo").value("Banco de Dados Essencial"))
                .andExpect(jsonPath("$.ano").value(2024))
                .andExpect(jsonPath("$.status").value("Emprestado"));

        // confirma que a alteração foi gravada
        mockMvc.perform(get("/livros/" + id))
                .andExpect(jsonPath("$.titulo").value("Banco de Dados Essencial"));
    }

    @Test
    @DisplayName("6. GET /livros/{id} inexistente retorna HTTP 404")
    void livroInexistente_retorna404() throws Exception {
        mockMvc.perform(get("/livros/9999"))
                .andExpect(status().isNotFound());
    }

    @Test
    @DisplayName("7. GET /documentacao retorna os endpoints")
    void documentacao() throws Exception {
        mockMvc.perform(get("/documentacao"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.endpoints").isArray())
                .andExpect(jsonPath("$.endpoints", hasItems("/", "/livros", "/livros/cadastrar",
                        "/livros/alterar/{id}", "/livros/{id}", "/documentacao")));
    }

    @Test
    @DisplayName("Extra: PUT em livro inexistente retorna HTTP 404")
    void alterarInexistente_retorna404() throws Exception {
        mockMvc.perform(put("/livros/alterar/9999")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(LIVRO_JSON))
                .andExpect(status().isNotFound());
    }

    @Test
    @DisplayName("Extra: DELETE /livros/excluir/{id} retorna HTTP 204")
    void excluirLivro() throws Exception {
        int id = cadastrarLivro();
        mockMvc.perform(delete("/livros/excluir/" + id))
                .andExpect(status().isNoContent());
        mockMvc.perform(get("/livros/" + id))
                .andExpect(status().isNotFound());
    }
}
