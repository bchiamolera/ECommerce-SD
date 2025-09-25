package org.furb.servicoexpedicao.model;

import java.io.Serializable;
import java.util.List;

public class Pedido implements Serializable {
    private String id;
    private String cliente;
    private double valor;
    private List<ItemPedido> items; // adicionado

    public Pedido() {}

    public Pedido(String id, String cliente, double valor, List<ItemPedido> items) {
        this.id = id;
        this.cliente = cliente;
        this.valor = valor;
        this.items = items;
    }

    // getters / setters
    public String getId() { return id; }

    public String getCliente() { return cliente; }

    public double getValor() { return valor; }

    public List<ItemPedido> getItems() { return items; }
}
