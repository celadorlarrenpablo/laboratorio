package com.tucan.subscriber.messaging;

import java.nio.charset.StandardCharsets;

import org.springframework.stereotype.Component;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.tucan.subscriber.observabilidad.NatsWrapper;
import com.tucan.subscriber.service.MessageRouter; // <-- ¡ESTE ES EL IMPORT QUE FALTABA!

import io.nats.client.Connection;
import io.nats.client.Dispatcher;
import io.nats.client.JetStream;
import io.nats.client.Message;
import io.nats.client.PushSubscribeOptions;
import jakarta.annotation.PostConstruct;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Component
@RequiredArgsConstructor
public class NatsSubscriber {

    private final Connection natsConnection;
    private final MessageRouter messageRouter;
    private final NatsWrapper observabilityWrapper;

    private final String tema = "tema.>";
    private final String grupoSuscriptores = "subscriber-group";

    @PostConstruct
    public void initSubscriber() {
        try {
            JetStream js = natsConnection.jetStream();
            Dispatcher dispatcher = natsConnection.createDispatcher();
            PushSubscribeOptions opciones = PushSubscribeOptions.builder().stream("STREAM_PRUEBAS").build();
            
            js.subscribe(tema, grupoSuscriptores, dispatcher, this::processMessage, false, opciones);
            log.info("Suscrito correctamente a JetStream en el tópico: [{}] con grupo: [{}]", tema, grupoSuscriptores);
        } catch (Exception e) {
            log.error("Error al suscribirse a NATS", e);
        }
    }

    private void processMessage(Message msg) {
        // Envolvemos toda la lógica en la abstracción de monitorización
        observabilityWrapper.processWithTracing(msg, "procesar_evento_nats", () -> {
            
            String data = new String(msg.getData(), StandardCharsets.UTF_8);
            String topic = msg.getSubject();
            
            log.info("Iniciando enrutamiento del mensaje..."); 
            
            try {
                messageRouter.route(topic, data);
                msg.ack();
                
            } catch (IllegalArgumentException e) {
                log.error("Tópico desconocido [{}]. Hacemos ack para descartarlo.", topic);
                msg.ack();
                
            } catch (JsonProcessingException e) {
                log.error("POISON PILL detectada: El payload no es un JSON válido. Tópico [{}]. Data: {}", topic, data);
                msg.ack();
                
            } catch (Exception e) {
                log.error("Error inesperado procesando tópico [{}]. NATS lo reintentará.", topic, e);
            }
        });
    }
}