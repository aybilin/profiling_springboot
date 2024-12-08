package com.profiling.profiling_project;

import io.opentelemetry.api.OpenTelemetry;
import io.opentelemetry.context.Context;
import io.opentelemetry.context.Scope;
import io.opentelemetry.context.propagation.TextMapGetter;
import io.opentelemetry.context.propagation.TextMapPropagator;
import io.opentelemetry.api.trace.Tracer;
import io.opentelemetry.api.trace.Span;
import jakarta.servlet.Filter;
import jakarta.servlet.FilterChain;
import jakarta.servlet.FilterConfig;
import jakarta.servlet.ServletException;
import jakarta.servlet.ServletRequest;
import jakarta.servlet.ServletResponse;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.stereotype.Component;

import java.io.IOException;

@Component
public class TraceFilter implements Filter {

    private final Tracer tracer;
    private final TextMapPropagator propagator;

    public TraceFilter(Tracer tracer, OpenTelemetry openTelemetry) {
        this.tracer = tracer;
        this.propagator = openTelemetry.getPropagators().getTextMapPropagator();
    }

    @Override
    public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain) throws IOException, ServletException {
        HttpServletRequest httpRequest = (HttpServletRequest) request;

        TextMapGetter<HttpServletRequest> getter = new TextMapGetter<>() {
            @Override
            public Iterable<String> keys(HttpServletRequest carrier) {
                return carrier.getHeaderNames() != null ?
                        java.util.Collections.list(carrier.getHeaderNames()) :
                        java.util.Collections.emptyList();
            }

            @Override
            public String get(HttpServletRequest carrier, String key) {
                return carrier.getHeader(key);
            }
        };

        // Extraire le contexte de traçage
        Context extractedContext = propagator.extract(Context.current(), httpRequest, getter);

        // Créer un span en utilisant le contexte extrait
        Span span = tracer.spanBuilder(httpRequest.getRequestURI())
                .setParent(extractedContext)
                .startSpan();

        try (Scope scope = span.makeCurrent()) {
            chain.doFilter(request, response);
        } finally {
            span.end();
        }
    }

    @Override
    public void init(FilterConfig filterConfig) {
    }

    @Override
    public void destroy() {
    }
}

