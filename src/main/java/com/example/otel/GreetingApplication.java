package com.example.otel;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

/**
 * Entry point for the example greeting service.
 *
 * <p>There is no telemetry setup code: the {@code opentelemetry-spring-boot-starter} on the
 * classpath auto-configures the OpenTelemetry SDK and instruments HTTP, metrics, and logs, all
 * driven by standard {@code OTEL_*} environment variables / {@code otel.*} properties.
 */
@SpringBootApplication
public class GreetingApplication {

  public static void main(String[] args) {
    SpringApplication.run(GreetingApplication.class, args);
  }
}
