package com.tucan.microservicio.service.handlers;

import org.springframework.stereotype.Service;

import com.tucan.microservicio.dto.EmailDTO;
import com.tucan.microservicio.service.TopicHandler;

import lombok.extern.slf4j.Slf4j;

@Slf4j
@Service
public class Email implements TopicHandler<EmailDTO> {

    @Override
    public String getTopicToHandle() {
        return "tema.email";
    }

    @Override
    public Class<EmailDTO> getPayloadClass() {
        return EmailDTO.class;
    }

    @Override
    public void process(EmailDTO email) {
        // Fíjate que ya no hace falta imprimir la traza a mano, Spring lo hace por ti
        log.info("Preparando envío de email. Destinatario: [{}], Asunto: [{}]", 
                 email.destinatario(), email.asunto());
        
        log.debug("Cuerpo del mensaje: {}", email.cuerpo());
    }
}