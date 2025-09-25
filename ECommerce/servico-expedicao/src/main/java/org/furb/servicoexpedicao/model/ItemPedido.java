package org.furb.servicoexpedicao.model;

public class ItemPedido {
    private int produtoId;
    private int quantidade;

    public ItemPedido() {}

    public ItemPedido(int produtoId, int quantidade) {
        this.produtoId = produtoId;
        this.quantidade = quantidade;
    }

    public int getProdutoId() { return produtoId; }
    public void setProdutoId(int produtoId) { this.produtoId = produtoId; }

    public int getQuantidade() { return quantidade; }
    public void setQuantidade(int quantidade) { this.quantidade = quantidade; }
}
