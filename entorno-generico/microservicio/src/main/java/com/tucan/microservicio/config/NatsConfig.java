package com.tucan.microservicio.config;

import java.io.IOException;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import io.nats.client.Connection;
import io.nats.client.JetStreamApiException;
import io.nats.client.JetStreamManagement;
import io.nats.client.Nats;
import io.nats.client.Options;
import io.nats.client.api.StorageType;
import io.nats.client.api.StreamConfiguration;

@Configuration
public class NatsConfig {

    @Value("${nats.url:nats://localhost:4222}")
    private String natsUrl;
    
    @Bean
    public Connection natsConnection() throws IOException, InterruptedException {
        Options options = new Options.Builder()
                .server(natsUrl)
                .maxReconnects(-1)
                .build();
        
        Connection nc = Nats.connect(options);
        
        JetStreamManagement jsm = nc.jetStreamManagement();
        
        try {
            jsm.getStreamInfo("STREAM_PRUEBAS");
        } catch (JetStreamApiException e) {
            if (e.getApiErrorCode() == 10059) { 
                System.out.println("El Stream no existe. Creándolo ahora...");
                StreamConfiguration streamConfig = StreamConfiguration.builder()
                        .name("STREAM_PRUEBAS")
                        .subjects("tema.>") 
                        .storageType(StorageType.File) 
                        .build();
                try {
                    jsm.addStream(streamConfig);
                } catch (JetStreamApiException ex) {
                    System.err.println("Error creando el stream: " + ex.getMessage());
                }
            }
        }
        
        return nc;
    }
}