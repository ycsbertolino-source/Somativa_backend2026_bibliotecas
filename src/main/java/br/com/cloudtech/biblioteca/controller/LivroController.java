package br.com.cloudtech.biblioteca.controller;

import br.com.cloudtech.biblioteca.models.Livro;
import br.com.cloudtech.biblioteca.repository.LivroRepository;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

/**
 * Gerenciamento dos livros do acervo.
 */
@RestController
@RequestMapping("/livros")
public class LivroController {

    private final LivroRepository repository;

    public LivroController(LivroRepository repository) {
        this.repository = repository;
    }

    // b) Consultar livros
    @GetMapping
    public List<Livro> listar() {
        return repository.listar();
    }

    // e) Consultar livro por ID (404 se não existir)
    @GetMapping("/{id}")
    public ResponseEntity<?> buscarPorId(@PathVariable Long id) {
        return repository.buscarPorId(id)
                .<ResponseEntity<?>>map(ResponseEntity::ok)
                .orElseGet(() -> naoEncontrado(id));
    }

    // c) Cadastrar novo livro (201 Created)
    @PostMapping("/cadastrar")
    public ResponseEntity<?> cadastrar(@RequestBody Livro livro) {
        if (livro.getTitulo() == null || livro.getAutor() == null) {
            return ResponseEntity.badRequest().body(Map.of("erro", "Os campos titulo e autor são obrigatórios"));
        }
        if (livro.getStatus() == null) {
            livro.setStatus("Disponível");
        }
        Livro salvo = repository.salvar(livro);
        return ResponseEntity.status(HttpStatus.CREATED).body(salvo);
    }

    // d) Alterar informações de um livro (200 OK)
    @PutMapping("/alterar/{id}")
    public ResponseEntity<?> alterar(@PathVariable Long id, @RequestBody Livro dados) {
        return repository.buscarPorId(id)
                .<ResponseEntity<?>>map(livro -> {
                    livro.setTitulo(dados.getTitulo());
                    livro.setAutor(dados.getAutor());
                    livro.setEditora(dados.getEditora());
                    livro.setAno(dados.getAno());
                    livro.setCategoria(dados.getCategoria());
                    livro.setStatus(dados.getStatus());
                    return ResponseEntity.ok(livro);
                })
                .orElseGet(() -> naoEncontrado(id));
    }

    // Extra: excluir livro (204 No Content)
    @DeleteMapping("/excluir/{id}")
    public ResponseEntity<?> excluir(@PathVariable Long id) {
        if (repository.remover(id)) {
            return ResponseEntity.noContent().build();
        }
        return naoEncontrado(id);
    }

    private ResponseEntity<?> naoEncontrado(Long id) {
        return ResponseEntity.status(HttpStatus.NOT_FOUND)
                .body(Map.of("erro", "Livro com id " + id + " não encontrado"));
    }
}
