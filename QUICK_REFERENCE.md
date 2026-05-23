# Quick Reference

## Run

```bash
make stack                          # Start collector + Tempo + Loki + Prometheus + Grafana
make server                         # Run the app (HTTP :8080)
# or: make run                      # both of the above
# native: ./gradlew bootRun
```

## Test

```bash
curl "http://localhost:8080/hello?name=World"
curl "http://localhost:8080/health"
open http://localhost:3000          # Grafana (admin/admin) → Explore
```

## Add OpenTelemetry to a Spring Boot service

```kotlin
// build.gradle.kts
implementation(platform("io.opentelemetry.instrumentation:opentelemetry-instrumentation-bom:2.28.1"))
implementation("io.opentelemetry.instrumentation:opentelemetry-spring-boot-starter")
```

That's it — Spring MVC, metrics, and logs are auto-instrumented. No SDK setup code.

## Optional: custom instrumentation

The app adds no manual spans/metrics — the starter covers HTTP. For your own logic,
inject the auto-configured beans:

```java
@RestController
class WorkController {
  private final Tracer tracer;
  private final LongCounter jobs;

  WorkController(OpenTelemetry otel) {
    this.tracer = otel.getTracer("com.example.otel");
    this.jobs = otel.getMeter("com.example.otel")
        .counterBuilder("jobs.processed").build();
  }

  @PostMapping("/work")
  void work() {
    Span span = tracer.spanBuilder("doWork").startSpan();
    try (Scope scope = span.makeCurrent()) {
      jobs.add(1);
      // ...
    } catch (Exception e) {
      span.recordException(e);
      span.setStatus(StatusCode.ERROR);
      throw e;
    } finally {
      span.end();
    }
  }
}
```

Logs are correlated automatically — just use SLF4J:

```java
private static final Logger log = LoggerFactory.getLogger(WorkController.class);
log.info("served request route={}", "/work");
```

## Environment variables

```bash
OTEL_EXPORTER_OTLP_ENDPOINT=http://localhost:4318   # collector / backend (default)
OTEL_EXPORTER_OTLP_PROTOCOL=http/protobuf           # default; or "grpc"
OTEL_EXPORTER_OTLP_HEADERS=api-key=YOUR_KEY         # hosted backend auth
OTEL_TRACES_SAMPLER=parentbased_traceidratio        # sampling strategy
OTEL_TRACES_SAMPLER_ARG=0.1                          # ...sample 10% of traces
OTEL_METRIC_EXPORT_INTERVAL=5000                     # faster local feedback (ms)
OTEL_RESOURCE_ATTRIBUTES=service.version=1.0.0,deployment.environment=dev
OTEL_SDK_DISABLED=true                               # turn telemetry off entirely
```

## Gradle tasks / Make targets

```
make build       ./gradlew build   (compile + format check + test)
make test        ./gradlew test
make lint        ./gradlew spotlessCheck
make format      ./gradlew spotlessApply
make stack       docker compose up -d   (observability stack)
make stack-down  docker compose down
make clean       ./gradlew clean
```
