package com.tucan.microservicio.messaging;

import java.nio.charset.StandardCharsets;

import org.springframework.stereotype.Component;

import io.nats.client.Connection;
import io.nats.client.Dispatcher;
import io.nats.client.JetStream;
import io.nats.client.Message;
import jakarta.annotation.PostConstruct;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Component
@RequiredArgsConstructor
public class NatsSubscriber {

    private final Connection natsConnection;

    @PostConstruct
    public void initSubscriber() {
        try {
            JetStream js = natsConnection.jetStream();
            Dispatcher dispatcher = natsConnection.createDispatcher();
            
            js.subscribe("tema.>", dispatcher, this::processMessage, false);
            log.info("Suscrito correctamente a JetStream en: com.tucan.eventos.>");
        } catch (Exception e) {
            log.error("Error al suscribirse a NATS", e);
        }
    }

    private void processMessage(Message msg) {
        String data = new String(msg.getData(), StandardCharsets.UTF_8);
        
        String traceId = msg.getHeaders() != null ? msg.getHeaders().getFirst("b3") : "Sin traza";
        
        log.info("Mensaje recibido en [{}]. Traza origen: [{}]. Data: {}", msg.getSubject(), traceId, data);
        msg.ack();
    }
}