#!/bin/bash
set -e

echo "Configurando monitoreo con Prometheus y Grafana para InnoSistemas..."

# Construir la imagen Docker con soporte para Actuator
docker build -t innosistemas:latest .

# Iniciar la pila de monitoreo
docker-compose -f docker-compose-monitoring.yml up -d

# Esperar a que los servicios estén disponibles
echo "Esperando a que los servicios estén disponibles..."
sleep 50

echo "Monitoreo configurado correctamente. Accede a:"
echo "- Prometheus: http://localhost:9090"
echo "- Grafana: http://localhost:3000 (usuario: admin, contraseña: admin)"
echo "- Aplicación Spring Boot: http://localhost:8080"
echo "- Métricas de Spring Boot: http://localhost:8080/actuator/prometheus"