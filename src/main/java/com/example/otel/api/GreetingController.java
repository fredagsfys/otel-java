package com.example.otel.api;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

/**
 * Example endpoint. Spring MVC requests are instrumented automatically by the OpenTelemetry starter
 * (server span + {@code http.server.*} metrics), so there is no manual span or metric code here.
 * Logs go through SLF4J; the starter bridges them to OpenTelemetry, correlated with the active
 * span.
 *
 * <p>Health is served by Spring Boot Actuator at {@code /actuator/health} (with liveness/readiness
 * probes), not a hand-rolled endpoint.
 */
@RestController
public class GreetingController {

  private static final Logger log = LoggerFactory.getLogger(GreetingController.class);

  /** Response body for {@code GET /hello}. */
  public record HelloResponse(String message) {}

  @GetMapping("/hello")
  public HelloResponse hello(@RequestParam(defaultValue = "World") String name) {
    log.info("served greeting name={}", name);
    return new HelloResponse("Hello, " + name + "!");
  }
}
