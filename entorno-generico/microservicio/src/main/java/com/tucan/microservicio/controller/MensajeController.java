package com.tucan.microservicio.controller;

import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.tucan.microservicio.messaging.NatsPublisher;

import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/api/mensajes")
@RequiredArgsConstructor
public class MensajeController {

    private final NatsPublisher natsPublisher;

    @PostMapping("/publicar")
    public String publicarMensaje(@RequestParam String asunto, @RequestParam String contenido) {
        natsPublisher.publishMessage(asunto, contenido);
        return "Mensaje enviado a NATS con el asunto: " + asunto;
    }
}