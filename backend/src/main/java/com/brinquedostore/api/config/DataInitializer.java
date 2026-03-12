package com.brinquedostore.api.config;

import com.brinquedostore.api.model.Brinquedo;
import com.brinquedostore.api.model.Categoria;
import com.brinquedostore.api.model.Marca;
import com.brinquedostore.api.repository.BrinquedoRepository;
import com.brinquedostore.api.repository.CategoriaRepository;
import com.brinquedostore.api.repository.MarcaRepository;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Configuration;

import java.util.Arrays;

@Configuration
public class DataInitializer implements CommandLineRunner {

    private final BrinquedoRepository brinquedoRepository;
    private final CategoriaRepository categoriaRepository;
    private final MarcaRepository marcaRepository;

    public DataInitializer(BrinquedoRepository brinquedoRepository, 
                           CategoriaRepository categoriaRepository,
                           MarcaRepository marcaRepository) {
        this.brinquedoRepository = brinquedoRepository;
        this.categoriaRepository = categoriaRepository;
        this.marcaRepository = marcaRepository;
    }

    @Override
    public void run(String... args) throws Exception {
        if (brinquedoRepository.count() > 0) {
            return;
        }

        // Criar Categorias
        Categoria catVeiculos = new Categoria();
        catVeiculos.setNome("Veículos");
        catVeiculos.setImgUrl("https://via.placeholder.com/150");
        
        Categoria catBonecas = new Categoria();
        catBonecas.setNome("Bonecas");
        catBonecas.setImgUrl("https://via.placeholder.com/150");

        Categoria catJogos = new Categoria();
        catJogos.setNome("Jogos");
        catJogos.setImgUrl("https://via.placeholder.com/150");

        Categoria catBlocos = new Categoria();
        catBlocos.setNome("Blocos de Montar");
        catBlocos.setImgUrl("https://via.placeholder.com/150");

        Categoria catEsportes = new Categoria();
        catEsportes.setNome("Esportes");
        catEsportes.setImgUrl("https://via.placeholder.com/150");

        categoriaRepository.saveAll(Arrays.asList(catVeiculos, catBonecas, catJogos, catBlocos, catEsportes));

        // Criar Marca Genérica
        Marca marcaGenerica = new Marca();
        marcaGenerica.setNome("Genérica");
        marcaRepository.save(marcaGenerica);

        // Criar Brinquedos
        Brinquedo b1 = new Brinquedo();
        b1.setNome("Carrinho de Controle Remoto");
        b1.setDescricao("Carrinho esportivo vermelho com controle remoto de longo alcance.");
        b1.setValor(129.90);
        b1.setImagemUrl("https://m.media-amazon.com/images/I/71wF7YDIQkL._AC_SX679_.jpg");
        b1.setCategoria(catVeiculos);
        b1.setMarca(marcaGenerica);
        b1.setCodigo("CARR001");
        b1.setEmDestaque(true);

        Brinquedo b2 = new Brinquedo();
        b2.setNome("Boneca Articulada");
        b2.setDescricao("Boneca com articulações e roupas trocáveis.");
        b2.setValor(89.90);
        b2.setImagemUrl("https://m.media-amazon.com/images/I/61bK6PMOC3L._AC_SX679_.jpg");
        b2.setCategoria(catBonecas);
        b2.setMarca(marcaGenerica);
        b2.setCodigo("BON001");
        b2.setEmDestaque(false);

        Brinquedo b3 = new Brinquedo();
        b3.setNome("Jogo de Tabuleiro Estratégia");
        b3.setDescricao("Jogo clássico de estratégia e conquista de territórios.");
        b3.setValor(149.90);
        b3.setImagemUrl("https://m.media-amazon.com/images/I/81+d6eSA0eL._AC_SX679_.jpg");
        b3.setCategoria(catJogos);
        b3.setMarca(marcaGenerica);
        b3.setCodigo("JOG001");
        b3.setEmDestaque(true);

        Brinquedo b4 = new Brinquedo();
        b4.setNome("Lego Creator");
        b4.setDescricao("Conjunto de montar 3 em 1: Dinossauro, Avião ou Carro.");
        b4.setValor(199.90);
        b4.setImagemUrl("https://m.media-amazon.com/images/I/81wFz-yWw+L._AC_SX679_.jpg");
        b4.setCategoria(catBlocos);
        b4.setMarca(marcaGenerica);
        b4.setCodigo("LEG001");
        b4.setEmDestaque(true);
        
        Brinquedo b5 = new Brinquedo();
        b5.setNome("Bola de Futebol Oficial");
        b5.setDescricao("Bola de futebol de campo tamanho oficial.");
        b5.setValor(59.90);
        b5.setImagemUrl("https://m.media-amazon.com/images/I/51+uK8F-wmL._AC_SX679_.jpg");
        b5.setCategoria(catEsportes);
        b5.setMarca(marcaGenerica);
        b5.setCodigo("BOL001");
        b5.setEmDestaque(false);

        brinquedoRepository.saveAll(Arrays.asList(b1, b2, b3, b4, b5));
    }
}
