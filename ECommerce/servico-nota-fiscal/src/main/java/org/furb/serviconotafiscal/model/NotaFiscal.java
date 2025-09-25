package org.furb.serviconotafiscal.model;

import java.util.List;

public class NotaFiscal {
    private String cliente;
    private List<ItemNotaFiscal> itens;
    private double frete;
    private double precoTotal;

    public NotaFiscal(String cliente, List<ItemNotaFiscal> itens, double precoTotal) {
        this.cliente = cliente;
        this.itens = itens;
        this.frete = precoTotal - calcularPrecoItems();
        this.precoTotal = precoTotal;
    }

    private double calcularPrecoItems() {
        double preco = 0;
        for (ItemNotaFiscal item : itens) {
            preco += item.getPreco() * item.getQuantidade();
        }
        return preco;
    }

    public String getCliente() {
        return cliente;
    }
    public void setCliente(String cliente) {
        this.cliente = cliente;
    }

    public List<ItemNotaFiscal> getItens() {
        return itens;
    }
    public void setItens(List<ItemNotaFiscal> itens) {
        this.itens = itens;
    }

    public double getFrete() {
        return frete;
    }
    public void setFrete(double frete) {
        this.frete = frete;
    }

    public double getPrecoTotal() {
        return precoTotal;
    }
    public void setPrecoTotal(double precoTotal) {
        this.precoTotal = precoTotal;
    }

    @Override
    public String toString() {
        String str = "\n________________________________________" +
                "\nNOTA FISCAL" +
                "\n----------------------------------------" +
                "\nCliente: " + cliente +
                "\n----------------------------------------" +
                "\nItens: ";
        for (ItemNotaFiscal item : itens) {
            str += item.toString() +
                    "\n****************************************";
        }
        return str +
                "\n----------------------------------------" +
                "\nFrete: " + frete +
                "\n----------------------------------------" +
                "\nPreço Total: " + precoTotal +
                "\n________________________________________\n";
    }
}
