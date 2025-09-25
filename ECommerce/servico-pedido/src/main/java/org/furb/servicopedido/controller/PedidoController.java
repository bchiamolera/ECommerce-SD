package org.furb.servicopedido.controller;

import org.furb.servicopedido.model.Pedido;
import org.furb.servicopedido.service.PedidoProducer;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/pedidos")
public class PedidoController {
    private final PedidoProducer pedidoProducer;

    public PedidoController(PedidoProducer pedidoProducer) {
        this.pedidoProducer = pedidoProducer;
    }

    @PostMapping
    public String criarPedido(@RequestBody Pedido pedido) {
        pedidoProducer.enviarPedido(pedido);
        return "Pedido recebido: " + pedido.getId();
    }
}
