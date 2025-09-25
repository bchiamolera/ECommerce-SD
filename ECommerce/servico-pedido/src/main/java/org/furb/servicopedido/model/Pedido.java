package org.furb.servicopedido.model;

import java.io.Serializable;

public class Pedido implements Serializable {
    private String id;
    private String clienteEmail;
    private double valor;

    public Pedido() {}

    public Pedido(String id, String clienteEmail, double valor) {
        this.id = id;
        this.clienteEmail = clienteEmail;
        this.valor = valor;
    }

    public String getId() {
        return id;
    }

    public String getClienteEmail() {
        return clienteEmail;
    }

    public double getValor() {
        return valor;
    }

    @Override
    public String toString() {
        return "Pedido{" + "id=" + id + ", cliente=" + clienteEmail + ", valor=" + valor + '}';
    }
}
