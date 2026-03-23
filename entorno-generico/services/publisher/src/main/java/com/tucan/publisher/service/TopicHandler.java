package com.tucan.publisher.service;

public interface TopicHandler<T> {
    
    // Indica qué tópico maneja esta clase
    String getTopicToHandle();
    
    // Le dice al Router a qué clase (DTO) debe convertir el JSON
    Class<T> getPayloadClass();
    
    // La lógica de negocio real, limpia de parámetros de infraestructura
    void process(T payload);
}