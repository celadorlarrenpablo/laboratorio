package com.tucan.publisher.controller;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.tucan.publisher.dto.EmailDTO;
import com.tucan.publisher.dto.FacturaDTO;
import com.tucan.publisher.messaging.NatsPublisher;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/api/mensajes")
@RequiredArgsConstructor
@Tag(name = "Publicador de Mensajes", description = "Endpoints para inyectar eventos JSON directamente en NATS")
public class MensajeController {

    private final NatsPublisher natsPublisher;

    @PostMapping("/factura")
    @Operation(
        summary = "Publicar evento de Factura", 
        description = "Envía un JSON estructurado de factura hacia el tópico 'tema.facturacion'"
    )
    public ResponseEntity<String> publicarFactura(@RequestBody FacturaDTO factura) {
        // Delegamos al publicador, pasando el objeto directamente
        natsPublisher.publishMessage("tema.facturacion", factura);
        return ResponseEntity.ok("Evento de factura enviado a NATS correctamente.");
    }

    @PostMapping("/email")
    @Operation(
        summary = "Publicar evento de Email", 
        description = "Envía un JSON estructurado de email hacia el tópico 'tema.email'"
    )
    public ResponseEntity<String> publicarEmail(@RequestBody EmailDTO email) {
        // Delegamos al publicador, pasando el objeto directamente
        natsPublisher.publishMessage("tema.email", email);
        return ResponseEntity.ok("Evento de email enviado a NATS correctamente.");
    }
}