# otel-java

A clean, idiomatic **Spring Boot 3 + OpenTelemetry** template — traces, metrics, and
logs exported over vendor-neutral OTLP. The
[`opentelemetry-spring-boot-starter`](https://opentelemetry.io/docs/zero-code/java/spring-boot-starter/)
auto-instruments HTTP, metrics, and logs **in-process** (no manual SDK wiring, no
bytecode agent), configured entirely through standard `OTEL_*` environment
variables — so you can point it at any OTLP backend (Tempo, Datadog, Honeycomb,
New Relic, …) with **no code changes**.

The example service in [`src/main/java`](src/main/java) shows the recommended setup
with **zero hand-written instrumentation**. Everything under
[`observability/`](observability/) is a local stack used only to *see* the
telemetry while you develop — supporting infrastructure, not part of what you ship.

This is the Java sibling of [otel-golang](https://github.com/fredagsfys/otel-golang);
same idea, idiomatic to each language and to each language's OpenTelemetry story.

## What's inside

| Path | Role |
| --- | --- |
| [`build.gradle.kts`](build.gradle.kts) | Build + the single OTel dependency (`opentelemetry-spring-boot-starter`). |
| [`GreetingApplication.java`](src/main/java/com/example/otel/GreetingApplication.java) | Spring Boot entry point — no telemetry code. |
| [`api/GreetingController.java`](src/main/java/com/example/otel/api/GreetingController.java) | Example endpoints — auto-instrumented by the starter. |
| [`application.yaml`](src/main/resources/application.yaml) | Service identity; everything else is `OTEL_*` config. |
| [`observability/`](observability/) | Supporting stack to test against: OTel Collector, Tempo, Loki, Prometheus, Grafana. |

> The example is organized by transport (`api`) because it's a demo. As your service
> grows real domains, slice by domain instead — e.g. `com.example.otel.orders`
> holding that domain's controller, service, and repository together — rather than
> by technical layer.

## Quick start

```bash
# 1. Start the supporting observability stack (collector + Tempo/Loki/Prometheus/Grafana)
make stack

# 2. Run the app (HTTP :8080)
make server

# 3. Generate some telemetry
curl "http://localhost:8080/hello?name=World"
curl "http://localhost:8080/health"
```

`make run` does steps 1 and 2 together. The native interface is Gradle
(`./gradlew bootRun`, `./gradlew test`); the Makefile is a thin convenience wrapper.
Run `make help` for all targets.

### See the telemetry

Open Grafana at <http://localhost:3000> (login `admin` / `admin`) → **Explore**:

- **Traces** — datasource `Tempo`, run TraceQL `{ resource.service.name = "greeting-service" }`
- **Metrics** — datasource `Prometheus`, search for `http_server_request_duration_seconds_count`
- **Logs** — datasource `Loki`, query `{service_name="greeting-service"}`

To watch what the collector receives:

```bash
docker compose -f observability/docker-compose.yaml logs -f otel-collector
```

## How OpenTelemetry is wired

There is no setup code. Adding the starter to [`build.gradle.kts`](build.gradle.kts)

```kotlin
implementation(platform("io.opentelemetry.instrumentation:opentelemetry-instrumentation-bom:2.28.1"))
implementation("io.opentelemetry.instrumentation:opentelemetry-spring-boot-starter")
```

auto-configures the OpenTelemetry SDK and instruments Spring MVC (server spans +
`http.server.*` metrics), SLF4J/Logback logs (bridged and trace-correlated), and
common libraries. The service name comes from `spring.application.name`; transport,
endpoint, sampling, and resource attributes come from the environment.

## Configuration

Configure through the standard `OTEL_*` environment variables (or the matching
`otel.*` properties in `application.yaml`):

| Environment variable | Purpose | Default |
| --- | --- | --- |
| `OTEL_EXPORTER_OTLP_ENDPOINT` | Collector / backend address | `http://localhost:4318` |
| `OTEL_EXPORTER_OTLP_PROTOCOL` | `http/protobuf` or `grpc` | `http/protobuf` |
| `OTEL_EXPORTER_OTLP_HEADERS` | Auth headers for hosted backends | _(none)_ |
| `OTEL_TRACES_SAMPLER` / `_ARG` | Sampling strategy / ratio | `parentbased_always_on` |
| `OTEL_METRIC_EXPORT_INTERVAL` | Metric export interval (ms) | `60000` |
| `OTEL_RESOURCE_ATTRIBUTES` | Extra resource attributes, e.g. `service.version=1.0.0,deployment.environment=prod` | _(none)_ |
| `OTEL_SDK_DISABLED` | Disable all telemetry (used by tests) | `false` |

> Unlike some SDKs, the Java OTLP exporter's default endpoint already includes the
> `http://` scheme (plaintext), so it talks to the local collector with no extra
> config. `make server` additionally sets a faster 5s metric interval and resource
> attributes for local development.

### Switching backends

```bash
# Honeycomb (OTLP/HTTP)
export OTEL_EXPORTER_OTLP_ENDPOINT=https://api.honeycomb.io
export OTEL_EXPORTER_OTLP_HEADERS=x-honeycomb-team=YOUR_KEY

# Grafana Cloud / Tempo over gRPC
export OTEL_EXPORTER_OTLP_PROTOCOL=grpc
export OTEL_EXPORTER_OTLP_ENDPOINT=https://tempo.example.com:4317
```

> **Prefer truly zero-code?** The
> [OpenTelemetry Java agent](https://opentelemetry.io/docs/zero-code/java/agent/)
> (`-javaagent:opentelemetry-javaagent.jar`) instruments a running JVM with no
> dependencies or source changes — the mature, idiomatic Java analog of eBPF for
> Go. This template uses the in-process starter instead: it travels with the build,
> is easy to extend with custom spans, and needs no agent attachment.

## Make targets

```
make help        Show all targets
make stack       Start the supporting observability stack
make server      Run the app
make run         Start the stack and run the app
make build       Build (compile + format check + test)
make test        Run tests
make lint        Check formatting (Spotless / google-java-format)
make format      Apply formatting
make stack-down  Stop the observability stack
make clean       Remove build artifacts
```

## License

MIT
