package org.furb.servicoexpedicao.service;

import org.furb.servicoexpedicao.model.Pedido;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.stereotype.Service;

@Service
public class NotificacaoService {
	private static final String EXCHANGE_PEDIDO = "exchange.pedido";
	private static final String RK_PEDIDO_ENVIADO = "pedido.enviado";
	
	private final RabbitTemplate rabbitTemplate;
	
	public NotificacaoService(RabbitTemplate rabbitTemplate) {
		this.rabbitTemplate = rabbitTemplate;
	}
	
	public void notificarExpedicao(Pedido pedido) {
		rabbitTemplate.convertAndSend(EXCHANGE_PEDIDO, RK_PEDIDO_ENVIADO, pedido);
        System.out.println("Pedido enviado: " + pedido);
	}
}
