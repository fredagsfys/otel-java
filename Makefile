COMPOSE := docker compose -f observability/docker-compose.yaml
GRADLE  := ./gradlew

# Dev-only OTEL_* settings: an explicit http:// endpoint for the local plaintext
# collector and a faster 5s metric interval (the OTEL default is 60s). Resource
# attributes add service version and environment.
OTEL_DEV_ENV := OTEL_EXPORTER_OTLP_ENDPOINT=http://localhost:4318 \
                OTEL_METRIC_EXPORT_INTERVAL=5000 \
                OTEL_RESOURCE_ATTRIBUTES=service.version=1.0.0,deployment.environment=development

.PHONY: help build run server stack stack-down test lint format clean

help: ## Show this help
	@grep -E '^[a-zA-Z_-]+:.*?## .*$$' $(MAKEFILE_LIST) | \
		awk 'BEGIN {FS = ":.*?## "}; {printf "  \033[36m%-12s\033[0m %s\n", $$1, $$2}'

build: ## Build the application (compile, format check, test)
	$(GRADLE) build

run: stack server ## Start the observability stack and run the app

server: ## Run the app (expects the stack to be up)
	$(OTEL_DEV_ENV) $(GRADLE) bootRun

stack: ## Start the supporting observability stack (collector, Tempo, Loki, Prometheus, Grafana)
	$(COMPOSE) up -d

stack-down: ## Stop the observability stack
	$(COMPOSE) down

test: ## Run tests
	$(GRADLE) test

lint: ## Check formatting (Spotless / google-java-format)
	$(GRADLE) spotlessCheck

format: ## Apply formatting
	$(GRADLE) spotlessApply

clean: ## Remove build artifacts
	$(GRADLE) clean
