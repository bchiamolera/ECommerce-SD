package org.furb.servicopedido.service;

import org.furb.servicopedido.model.Pedido;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.stereotype.Service;

@Service
public class PedidoProducer {
    public static final String EXCHANGE_PEDIDO = "exchange.pedido";
    public static final String RK_PEDIDO_CRIADO = "pedido.criado";

    private final RabbitTemplate rabbitTemplate;

    public PedidoProducer(RabbitTemplate rabbitTemplate) {
        this.rabbitTemplate = rabbitTemplate;
    }

    public void enviarPedido(Pedido pedido) {
        rabbitTemplate.convertAndSend(EXCHANGE_PEDIDO, RK_PEDIDO_CRIADO, pedido);
        System.out.println("Pedido publicado: " + pedido);
    }
}
