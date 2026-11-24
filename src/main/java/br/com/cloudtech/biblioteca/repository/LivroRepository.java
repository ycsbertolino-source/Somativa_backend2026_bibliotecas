package br.com.cloudtech.biblioteca.repository;

import br.com.cloudtech.biblioteca.models.Livro;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Optional;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.concurrent.atomic.AtomicLong;

/**
 * Armazenamento em memória do acervo.
 * (Os dados são reiniciados quando a aplicação reinicia.)
 */
@Component
public class LivroRepository {

    private final List<Livro> livros = new CopyOnWriteArrayList<>();
    private final AtomicLong sequencia = new AtomicLong();

    public LivroRepository() {
        // Dados iniciais de exemplo
        salvar(new Livro(null, "Introdução à Programação", "João Silva", "Tech Books", 2022, "Tecnologia", "Disponível"));
        salvar(new Livro(null, "Redes de Computadores", "Carlos Mendes", "Net Books", 2021, "Redes", "Disponível"));
    }

    public List<Livro> listar() {
        return livros;
    }

    public Optional<Livro> buscarPorId(Long id) {
        return livros.stream().filter(l -> l.getId().equals(id)).findFirst();
    }

    public Livro salvar(Livro livro) {
        livro.setId(sequencia.incrementAndGet());
        livros.add(livro);
        return livro;
    }

    public boolean remover(Long id) {
        return livros.removeIf(l -> l.getId().equals(id));
    }
}
