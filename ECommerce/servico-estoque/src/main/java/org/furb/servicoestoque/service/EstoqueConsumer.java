package org.furb.servicoestoque.service;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.rabbitmq.client.Channel;
import org.furb.servicoestoque.model.Pedido;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.amqp.core.Message;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.amqp.support.AmqpHeaders;
import org.springframework.messaging.handler.annotation.Header;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.util.List;
import java.util.Map;
import java.util.Optional;

@Service
public class EstoqueConsumer {
    // Filas
    public static final String FILA_ESTOQUE = "fila.estoque";
    public static final String FILA_RETRY = "fila.retry";
    public static final String FILA_DLQ = "fila.dlq";

    // Exchanges DLX
    public static final String FINAL_DLX = "exchange.final-dlx";

    // Routing Keys
    public static final String RK_PEDIDO_FALHA = "pedido.dlx";

    private static final Logger log = LoggerFactory.getLogger(EstoqueConsumer.class);
    private static final int MAX_RETRIES = 3;
    private final RabbitTemplate rabbitTemplate;
    private final ProdutoService produtoService;
    private final ObjectMapper objectMapper;

    public EstoqueConsumer(RabbitTemplate rabbitTemplate, ProdutoService produtoService) {
        this.rabbitTemplate = rabbitTemplate;
        this.produtoService = produtoService;
        this.objectMapper = new ObjectMapper();
    }

    @RabbitListener(queues = FILA_ESTOQUE)
    public void consumirMensagem(String pedidoJson, Message message, Channel channel, @Header(AmqpHeaders.DELIVERY_TAG) long deliveryTag) throws IOException {
        log.info("Recebida mensagem para estoque: {}", pedidoJson);

        try {
            Pedido pedido = objectMapper.readValue(pedidoJson, Pedido.class);
            produtoService.processarPedido(pedido);
            log.info("Estoque atualizado com sucesso para o pedido: {}", pedidoJson);

            // Confirma (ACK) a mensagem explicitamente
            channel.basicAck(deliveryTag, false);
            log.info("Mensagem confirmada (ACK) com deliveryTag: {}", deliveryTag);
        } catch (Exception e) {
            long retryCount = getRetryCount(message.getMessageProperties().getXDeathHeader());
            log.warn("Falha ao processar mensagem. Causa: {}. Contagem de retries: {}", e.getMessage(), retryCount);

            if (retryCount < MAX_RETRIES) {
                log.info("Rejeitando mensagem (NACK) para nova tentativa. DeliveryTag: {}", deliveryTag);
                // Rejeita a mensagem. Como a fila tem uma DLX, ela será movida para lá (fila de retry)
                channel.basicReject(deliveryTag, false);
            } else {
                log.error("Máximo de retentativas ({}) atingido. Enviando para a DLQ final. DeliveryTag: {}", MAX_RETRIES, deliveryTag);
                rabbitTemplate.convertAndSend(
                        FINAL_DLX,
                        RK_PEDIDO_FALHA,
                        message
                );
                // Confirma a mensagem original para removê-la da fila de notificação
                channel.basicAck(deliveryTag, false);
                log.info("Mensagem confirmada (ACK) e movida para a DLQ final. DeliveryTag: {}", deliveryTag);
            }
        }
    }

    /**
     * Calcula o número de tentativas com base no header 'x-death' injetado pelo RabbitMQ.
     * Este header é uma lista de eventos de "morte" da mensagem.
     */
    private long getRetryCount(List<Map<String, ?>> xDeathHeader) {
        return Optional.ofNullable(xDeathHeader)
                .flatMap(headers -> headers.stream()
                        .filter(header -> FILA_RETRY.equals(header.get("queue")))
                        .findFirst()
                )
                .map(header -> (long) header.get("count"))
                .orElse(0L);
    }

    /**
     * Listener para a fila final (DLQ), apenas para observar as mensagens que falharam permanentemente.
     */
    @RabbitListener(queues = FILA_DLQ)
    public void processarMensagemDlq(Message message, Channel channel, @Header(AmqpHeaders.DELIVERY_TAG) long deliveryTag) throws IOException {
        String payload = new String(message.getBody());
        log.error("[DLQ FINAL] Recebida mensagem com falha permanente: {}", payload);
        // Confirma o recebimento da mensagem na DLQ
        channel.basicAck(deliveryTag, false);
    }
}