package org.furb.servicoexpedicao.config;

import org.springframework.amqp.core.*;
import org.springframework.amqp.support.converter.Jackson2JsonMessageConverter;
import org.springframework.amqp.support.converter.MessageConverter;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class RabbitConfig {
    // Exchange Principal
    public static final String EXCHANGE_PEDIDO = "exchange.pedido";

    // Filas
    public static final String FILA_EXPEDICAO = "fila.expedicao";
    public static final String FILA_RETRY = "fila.retry";
    public static final String FILA_DLQ = "fila.dlq";

    // Exchanges DLX
    public static final String RETRY_DLX = "exchange.retry-dlx";
    public static final String FINAL_DLX = "exchange.final-dlx";

    // Routing Keys
    public static final String RK_PEDIDO_CRIADO = "pedido.criado";
    public static final String RK_PEDIDO_RETRY = "pedido.retry";
    public static final String RK_PEDIDO_DLX = "pedido.dlx";

    // TTL
    private static final Integer TTL_RETRY_FILA = 10000; // 10 segundos

    // --- Exchange Principal ---
    @Bean
    public TopicExchange pedidoExchange() {
        return new TopicExchange(EXCHANGE_PEDIDO);
    }

    // --- Configuração da Fila Principal de Expedição ---
    @Bean
    public Queue expedicaoQueue() {
        return QueueBuilder.durable(FILA_EXPEDICAO)
                .withArgument("x-dead-letter-exchange", RETRY_DLX)
                .withArgument("x-dead-letter-routing-key", RK_PEDIDO_RETRY)
                .build();
    }

    @Bean
    public Binding expedicaoBinding(TopicExchange pedidoExchange, Queue expedicaoQueue) {
        return BindingBuilder.bind(expedicaoQueue)
                .to(pedidoExchange)
                .with(RK_PEDIDO_CRIADO);
    }

    // --- Configuração do Fluxo de Retry ---
    @Bean
    public TopicExchange retryDlx() {
        return new TopicExchange(RETRY_DLX);
    }

    @Bean
    public Queue retryQueue() {
        // A mensagem nesta fila expira após TTL e é enviada para a exchange principal
        return QueueBuilder.durable(FILA_RETRY)
                .withArgument("x-dead-letter-exchange", EXCHANGE_PEDIDO)
                .withArgument("x-dead-letter-routing-key", RK_PEDIDO_CRIADO) // Volta para a fila original
                .withArgument("x-message-ttl", TTL_RETRY_FILA)
                .build();
    }

    @Bean
    public Binding retryBinding(TopicExchange retryDlx, Queue retryQueue) {
        return BindingBuilder.bind(retryQueue)
                .to(retryDlx)
                .with(RK_PEDIDO_RETRY);
    }

    // --- Configuração do Fluxo Final de Falha (DLQ) ---
    @Bean
    public TopicExchange finalDlx() {
        return new TopicExchange(FINAL_DLX);
    }

    @Bean
    public Queue finalDlq() {
        return new Queue(FILA_DLQ);
    }

    @Bean
    public Binding finalDlqBinding(TopicExchange finalDlx, Queue finalDlq) {
        // Roteia qualquer mensagem nesta exchange para a fila final
        return BindingBuilder.bind(finalDlq)
                .to(finalDlx)
                .with(RK_PEDIDO_DLX);
    }

    @Bean
    public MessageConverter jsonMessageConverter() {
        return new Jackson2JsonMessageConverter();
    }
}
