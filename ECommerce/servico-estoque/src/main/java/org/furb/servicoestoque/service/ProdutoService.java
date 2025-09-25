package org.furb.servicoestoque.service;

import org.furb.servicoestoque.model.ItemPedido;
import org.furb.servicoestoque.model.Pedido;
import org.furb.servicoestoque.model.Produto;
import org.furb.servicoestoque.repository.ProdutoRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

@Service
public class ProdutoService {
    private final ProdutoRepository repository;

    public ProdutoService(ProdutoRepository repository) {
        this.repository = repository;
    }

    @Transactional
    public void processarPedido(Pedido pedido) {
        for (ItemPedido item : pedido.getItems()) {
            Optional<Produto> produtoOpt = repository.findById((long) item.getProdutoId());
            if (produtoOpt.isPresent()) {
                Produto produto = produtoOpt.get();
                int quantidadeAtual = produto.getQuantidade();

                if (quantidadeAtual >= item.getQuantidade()) {
                    produto.setQuantidade(quantidadeAtual - item.getQuantidade());
                    repository.save(produto);
                } else {
                    throw new RuntimeException("Estoque insuficiente para o produto: " + produto.getNome());
                }
            } else {
                throw new RuntimeException("Produto não encontrado com ID: " + item.getProdutoId());
            }
        }
    }

    public List<Produto> listarProdutos() {
        return repository.findAll();
    }
}
