# Imagen base con JDK 17
FROM eclipse-temurin:17-jdk-alpine as build

# Directorio de trabajo
WORKDIR /workspace/app

# Instalar herramientas para corregir codificación
RUN apk add --no-cache dos2unix

# Copiar archivos Maven
COPY mvnw .
COPY .mvn .mvn
COPY pom.xml .
COPY src src

# Dar permisos de ejecución al script mvnw
RUN chmod +x ./mvnw

# Convertir archivos de propiedades a UTF-8
RUN find ./src -name "*.properties" -type f -exec dos2unix {} \;

# Construir la aplicación sin tests y con codificación explícita
RUN ./mvnw package -DskipTests -Dproject.build.sourceEncoding=UTF-8 -Dproject.reporting.outputEncoding=UTF-8

# Imagen final más ligera
FROM eclipse-temurin:17-jre-alpine

# Crear un usuario no root
RUN addgroup -S appgroup && adduser -S appuser -G appgroup

# Directorio donde se ejecutará la aplicación
WORKDIR /app

# Copiar el JAR de la etapa de construcción
COPY --from=build /workspace/app/target/*.jar app.jar

# Cambiar propietario de los archivos
RUN chown -R appuser:appgroup /app

# Cambiar a usuario no privilegiado
USER appuser

# Configurar para recibir el puerto de Render a través de variable de entorno
ENV PORT=8080

# Exponer el puerto
EXPOSE ${PORT}

# Healthcheck para Docker y Prometheus
HEALTHCHECK --interval=30s --timeout=3s --retries=3 CMD wget -q --spider http://localhost:${PORT}/actuator/health || exit 1

# Comando para ejecutar la aplicación
CMD ["sh", "-c", "java -Dserver.port=${PORT} -jar app.jar"]