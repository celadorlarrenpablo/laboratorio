package com.tucan.publisher.messaging;

import java.nio.charset.StandardCharsets;

import org.springframework.stereotype.Service;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.tucan.publisher.observabilidad.NatsWrapper; // <-- ¡ESTE ES EL IMPORT QUE FALTABA!

import io.nats.client.Connection;
import io.nats.client.JetStream;
import io.nats.client.Message;
import io.nats.client.impl.Headers;
import io.nats.client.impl.NatsMessage;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Service
@RequiredArgsConstructor
public class NatsPublisher {

    private final Connection natsConnection;
    private final ObjectMapper objectMapper;
    private final NatsWrapper natsWrapper;
    
    public void publishMessage(String subject, Object payload) {
        try {
            JetStream js = natsConnection.jetStream();
            
            // 1. Delegamos la creación de cabeceras de monitorización al Wrapper
            Headers headers = natsWrapper.injectTracingHeaders(new Headers());

            // 2. Convertimos a JSON
            String jsonPayload = objectMapper.writeValueAsString(payload);

            // 3. Montamos y enviamos el mensaje
            Message msg = NatsMessage.builder()
                    .subject(subject)
                    .headers(headers)
                    .data(jsonPayload.getBytes(StandardCharsets.UTF_8))
                    .build();

            js.publish(msg);
            log.info("Mensaje JSON publicado en {}. Payload: {}", subject, jsonPayload);
            
        } catch (Exception e) {
            log.error("Error al publicar JSON en NATS JetStream", e);
        }
    }
}