package org.furb.servicoexpedicao.service;

import java.io.IOException;

import org.furb.servicoexpedicao.model.Pedido;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.amqp.core.Message;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.amqp.support.AmqpHeaders;
import org.springframework.messaging.handler.annotation.Header;
import org.springframework.stereotype.Service;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.rabbitmq.client.Channel;

@Service
public class ExpedicaoConsumer {
	// Filas
    public static final String FILA_EXPEDICAO = "fila.expedicao";
    public static final String FILA_RETRY = "fila.retry";
    public static final String FILA_DLQ = "fila.dlq";

    // Exchanges DLX
    public static final String FINAL_DLX = "exchange.final-dlx";

    // Routing Keys
    public static final String RK_PEDIDO_FALHA = "pedido.dlx";

    private static final Logger log = LoggerFactory.getLogger(ExpedicaoConsumer.class);
    private static final int MAX_RETRIES = 3;
    private final RabbitTemplate rabbitTemplate;
    private final ObjectMapper objectMapper;
    
    public ExpedicaoConsumer(RabbitTemplate rabbitTemplate) {
    	this.rabbitTemplate = rabbitTemplate;
    	this.objectMapper = new ObjectMapper();
    }

    @RabbitListener(queues = FILA_EXPEDICAO)
    public void consumirMensagem(String pedidoJson, Message message, Channel channel, @Header(AmqpHeaders.DELIVERY_TAG) long deliveryTag) throws IOException {
    	log.info("Recebida mensagem para expedicao: {}", pedidoJson);
    	// channel.basicAck(deliveryTag, false);
    }
}
