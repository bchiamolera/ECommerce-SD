package org.furb.serviconotificacao.service;

import org.furb.serviconotificacao.model.Pedido;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.stereotype.Service;

@Service
public class EmailService {
    private static final Logger log = LoggerFactory.getLogger(EmailService.class);
    private final JavaMailSender mailSender;

    public EmailService(JavaMailSender mailSender) {
        this.mailSender = mailSender;
    }

    public void enviarEmail(Pedido pedido, String tipo) {
        try {
            SimpleMailMessage message = new SimpleMailMessage();
            message.setTo(pedido.getCliente()); // aqui cliente deve ser email válido
            message.setSubject("Notificação do seu Pedido: " + pedido.getId());
            message.setText(buildCorpoEmail(pedido, tipo));

            mailSender.send(message);
            log.info("E-mail enviado para {}", pedido.getCliente());
        } catch (Exception e) {
            log.error("Falha ao enviar e-mail para {}", pedido.getCliente(), e);
            throw new RuntimeException("Falha no serviço de envio de e-mail", e);
        }
    }

    private String buildCorpoEmail(Pedido pedido, String tipo) {
        if ("criado".equals(tipo)) {
            return "Olá, recebemos seu pedido #" + pedido.getId() +
                    " no valor de R$ " + pedido.getValor() +
                    ". Em breve será processado.";
        } else if ("enviado".equals(tipo)) {
            return "Seu pedido #" + pedido.getId() + " foi expedido e está a caminho!";
        }
        return "Atualização sobre seu pedido #" + pedido.getId();
    }
}
