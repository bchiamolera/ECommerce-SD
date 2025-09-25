package org.furb.serviconotificacao.config;

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
    public static final String FILA_NOTIFICACAO = "fila.notificacao";
    public static final String FILA_RETRY_NOTIFICACAO = "fila.retry";
    public static final String FILA_DLQ_NOTIFICACAO = "filao.dlq";

    // Exchanges DLX
    public static final String RETRY_DLX_NOTIFICACAO = "exchange.retry-dlx";
    public static final String FINAL_DLX_NOTIFICACAO = "exchange.final-dlx";

    // Routing Keys
    public static final String RK_PEDIDO_CRIADO = "pedido.criado";
    public static final String RK_PEDIDO_RETRY = "pedido.retry";

    // TTL
    private static final Integer TTL_RETRY_FILA = 10000; // 10 segundos

    // --- Exchange Principal ---
    @Bean
    public TopicExchange pedidoExchange() {
        return new TopicExchange(EXCHANGE_PEDIDO);
    }

    // --- Configuração da Fila Principal de Notificação ---
    @Bean
    public Queue notificacaoQueue() {
        return QueueBuilder.durable(FILA_NOTIFICACAO)
                .withArgument("x-dead-letter-exchange", RETRY_DLX_NOTIFICACAO)
                .withArgument("x-dead-letter-routing-key", RK_PEDIDO_RETRY)
                .build();
    }

    @Bean
    public Binding notificacaoBinding(TopicExchange pedidoExchange, Queue notificacaoQueue) {
        return BindingBuilder.bind(notificacaoQueue)
                .to(pedidoExchange)
                .with(RK_PEDIDO_CRIADO);
    }

    // --- Configuração do Fluxo de Retry ---
    @Bean
    public TopicExchange retryDlx() {
        return new TopicExchange(RETRY_DLX_NOTIFICACAO);
    }

    @Bean
    public Queue retryQueue() {
        // A mensagem nesta fila expira após TTL e é enviada para a exchange principal
        return QueueBuilder.durable(FILA_RETRY_NOTIFICACAO)
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
        return new TopicExchange(FINAL_DLX_NOTIFICACAO);
    }

    @Bean
    public Queue finalDlq() {
        return new Queue(FILA_DLQ_NOTIFICACAO);
    }

    @Bean
    public Binding finalDlqBinding(TopicExchange finalDlx, Queue finalDlq) {
        // Roteia qualquer mensagem nesta exchange para a fila final
        return BindingBuilder.bind(finalDlq)
                .to(finalDlx)
                .with("#");
    }

    @Bean
    public MessageConverter jsonMessageConverter() {
        return new Jackson2JsonMessageConverter();
    }
}
