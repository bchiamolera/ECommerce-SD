package org.furb.serviconotafiscal.config;

import org.furb.serviconotafiscal.model.Produto;
import org.furb.serviconotafiscal.repository.ProdutoRepository;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class DataInitializer {
    @Bean
    CommandLineRunner initDatabase(ProdutoRepository repository) {
        return args -> {
            repository.save(new Produto(null, "Caderno", 5));
            repository.save(new Produto(null, "Caneta", 2));
            repository.save(new Produto(null, "Lápis", 2.5));
        };
    }
}