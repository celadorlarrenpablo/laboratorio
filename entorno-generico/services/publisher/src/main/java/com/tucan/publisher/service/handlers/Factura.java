package com.tucan.publisher.service.handlers;

import org.springframework.stereotype.Service;

import com.tucan.publisher.dto.FacturaDTO; // <-- Usa FacturaDTO
import com.tucan.publisher.service.TopicHandler;

import lombok.extern.slf4j.Slf4j;

@Slf4j
@Service
public class Factura implements TopicHandler<FacturaDTO> { // <-- Cambia el genérico

    @Override
    public String getTopicToHandle() {
        return "tema.facturacion"; // <-- ESTE DEBE SER SU TÓPICO
    }

    @Override
    public Class<FacturaDTO> getPayloadClass() {
        return FacturaDTO.class;
    }

    @Override
    public void process(FacturaDTO factura) {
        log.info("Procesando factura ID: [{}] para el cliente: [{}]", 
                 factura.idFactura(), factura.cliente());
    }
}