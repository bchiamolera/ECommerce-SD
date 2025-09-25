package org.furb.servicoestoque.config;

import org.furb.servicoestoque.model.Produto;
import org.furb.servicoestoque.repository.ProdutoRepository;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class DataInitializer {
    @Bean
    CommandLineRunner initDatabase(ProdutoRepository repository) {
        return args -> {
            repository.save(new Produto(null, "Caderno", 50));
            repository.save(new Produto(null, "Caneta", 100));
            repository.save(new Produto(null, "Lápis", 150));
        };
    }
}
