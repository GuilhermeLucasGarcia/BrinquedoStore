package com.brinquedostore.api.repository;

import com.brinquedostore.api.model.Categoria;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.test.context.TestPropertySource;

import static org.assertj.core.api.Assertions.assertThat;

@DataJpaTest
@TestPropertySource(properties = "spring.jpa.hibernate.ddl-auto=create-drop")
class CategoriaRepositoryTest {

    @Autowired
    private CategoriaRepository categoriaRepository;

    @Test
    void deveFiltrarPorNomeEStatusComPaginacao() {
        categoriaRepository.save(criarCategoria("Natal 2026", true));
        categoriaRepository.save(criarCategoria("Volta às Aulas", false));
        categoriaRepository.save(criarCategoria("Natal Kids", true));

        Page<Categoria> pagina = categoriaRepository.buscarComFiltros("Natal", true, PageRequest.of(0, 10));

        assertThat(pagina.getTotalElements()).isEqualTo(2);
        assertThat(pagina.getContent()).extracting(Categoria::getNome)
                .containsExactlyInAnyOrder("Natal 2026", "Natal Kids");
    }

    @Test
    void deveListarApenasCategoriasAtivasEmOrdemAlfabetica() {
        categoriaRepository.save(criarCategoria("Brinquedos Educativos", true));
        categoriaRepository.save(criarCategoria("Jogos Noturnos", false));
        categoriaRepository.save(criarCategoria("Aventura Kids", true));

        assertThat(categoriaRepository.findByAtivoTrueOrderByNomeAsc())
                .extracting(Categoria::getNome)
                .containsExactly("Aventura Kids", "Brinquedos Educativos");
    }

    private Categoria criarCategoria(String nome, boolean ativo) {
        Categoria categoria = new Categoria();
        categoria.setNome(nome);
        categoria.setDescricao("Descrição de " + nome);
        categoria.setAtivo(ativo);
        categoria.setImgUrl("https://exemplo.com/" + nome.replace(" ", "-") + ".jpg");
        return categoria;
    }
}
