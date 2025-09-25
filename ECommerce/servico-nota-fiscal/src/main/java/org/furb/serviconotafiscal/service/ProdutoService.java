package org.furb.serviconotafiscal.service;

import org.furb.serviconotafiscal.model.Produto;
import org.furb.serviconotafiscal.repository.ProdutoRepository;
import org.springframework.stereotype.Service;

@Service
public class ProdutoService {
    private final ProdutoRepository repository;

    public ProdutoService(ProdutoRepository repository) {
        this.repository = repository;
    }

    public Produto getById(long id){
        return repository.findById(id).get();
    }
}
