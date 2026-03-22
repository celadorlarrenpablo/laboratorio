package com.tucan.microservicio.messaging;

import java.nio.charset.StandardCharsets;

import org.springframework.stereotype.Service;

import io.micrometer.tracing.Tracer;
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
    private final Tracer tracer;

    public void publishMessage(String subject, String payload) {
        try {
            JetStream js = natsConnection.jetStream();
            
            Headers headers = new Headers();
            if (tracer.currentTraceContext().context() != null) {
                headers.put("b3", tracer.currentTraceContext().context().traceId()); 
            }

            Message msg = NatsMessage.builder()
                    .subject(subject)
                    .headers(headers)
                    .data(payload.getBytes(StandardCharsets.UTF_8))
                    .build();

            js.publish(msg);
            log.info("Mensaje publicado en {}. Payload: {}", subject, payload);
            
        } catch (Exception e) {
            log.error("Error al publicar en NATS JetStream", e);
        }
    }
}