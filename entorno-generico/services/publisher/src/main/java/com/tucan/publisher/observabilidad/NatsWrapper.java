package com.tucan.publisher.observabilidad;

import org.springframework.stereotype.Component;

import io.micrometer.tracing.Span;
import io.micrometer.tracing.Tracer;
import io.micrometer.tracing.propagation.Propagator;
import io.nats.client.Message;
import io.nats.client.impl.Headers;
import lombok.RequiredArgsConstructor;

@Component
@RequiredArgsConstructor
public class NatsWrapper {

    private final Tracer tracer;
    private final Propagator propagator;

    // PARA EL SUSCRIPTOR: Cómo LEER las cabeceras de un mensaje de NATS
    private final Propagator.Getter<Message> getter = (msg, key) -> 
            msg.getHeaders() != null ? msg.getHeaders().getFirst(key) : null;

    // PARA EL PUBLISHER: Cómo ESCRIBIR las cabeceras en un objeto Headers de NATS
    private final Propagator.Setter<Headers> setter = (headers, key, value) -> 
            headers.put(key, value);

    /**
     * SUBSCRIBER: Envuelve la recepción del mensaje en un Span
     */
    public void processWithTracing(Message msg, String operationName, Runnable action) {
        Span.Builder spanBuilder = propagator.extract(msg, getter);
        Span span = spanBuilder.name(operationName).start();

        try (Tracer.SpanInScope ws = tracer.withSpan(span)) {
            action.run();
        } catch (Exception e) {
            span.error(e);
            throw e;
        } finally {
            span.end();
        }
    }

    /**
     * PUBLISHER: Inyecta el contexto de la traza actual en las cabeceras de NATS
     */
    public Headers injectTracingHeaders(Headers headers) {
        if (headers == null) {
            headers = new Headers();
        }
        // Dejamos que Micrometer escriba TODOS los campos necesarios (TraceId, SpanId, etc.)
        propagator.inject(tracer.currentTraceContext().context(), headers, setter);
        return headers;
    }
}