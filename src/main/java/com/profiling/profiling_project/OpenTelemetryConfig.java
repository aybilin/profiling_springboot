package com.profiling.profiling_project;

import io.opentelemetry.api.OpenTelemetry;
import io.opentelemetry.api.trace.Tracer;
import io.opentelemetry.exporter.zipkin.ZipkinSpanExporter;
import io.opentelemetry.sdk.OpenTelemetrySdk;
import io.opentelemetry.sdk.resources.Resource;
import io.opentelemetry.sdk.trace.SdkTracerProvider;
import io.opentelemetry.sdk.trace.export.BatchSpanProcessor;
import io.opentelemetry.semconv.resource.attributes.ResourceAttributes;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class OpenTelemetryConfig {

    @Bean
    public OpenTelemetry openTelemetry() {
        // Configure the Zipkin exporter
        ZipkinSpanExporter zipkinExporter = ZipkinSpanExporter.builder()
                .setEndpoint("http://localhost:9411/api/v2/spans")
                .build();

        // Define the service resource with the desired service name
        Resource serviceResource = Resource.builder()
                .put(ResourceAttributes.SERVICE_NAME, "ProfilingService")
                .build();

        // Configure the SDK Tracer Provider with the resource
        SdkTracerProvider sdkTracerProvider = SdkTracerProvider.builder()
                .addSpanProcessor(BatchSpanProcessor.builder(zipkinExporter).build())
                .setResource(serviceResource)
                .build();

        // Create and return the OpenTelemetry SDK instance
        return OpenTelemetrySdk.builder()
                .setTracerProvider(sdkTracerProvider)
                .build();
    }

    @Bean
    public Tracer tracer(OpenTelemetry openTelemetry) {
        // Return the tracer for your application
        return openTelemetry.getTracer("backend-tracer");
    }
}
