package com.tucan.publisher.service;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import org.springframework.stereotype.Component;

import com.fasterxml.jackson.databind.ObjectMapper;

import lombok.extern.slf4j.Slf4j;

@Slf4j
@Component
public class MessageRouter {

    private final Map<String, TopicHandler> handlersMap;
    private final ObjectMapper objectMapper;

    public MessageRouter(List<TopicHandler> handlers, ObjectMapper objectMapper) {
        this.objectMapper = objectMapper;
        this.handlersMap = handlers.stream()
            .collect(Collectors.toMap(TopicHandler::getTopicToHandle, handler -> handler));
    }

    @SuppressWarnings("unchecked")
    public void route(String topic, String payloadJson) throws Exception {
        TopicHandler handler = handlersMap.get(topic);

        if (handler != null) {
            Class<?> targetClass = handler.getPayloadClass();
            Object dto = objectMapper.readValue(payloadJson, targetClass);
            
            // Ejecutamos el handler pasando SOLO el objeto
            handler.process(dto);
        } else {
            log.warn("No hay un handler configurado para el tópico: [{}]", topic);
            throw new IllegalArgumentException("Tópico no soportado: " + topic);
        }
    }
}