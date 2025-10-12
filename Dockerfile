# ===========================================
# DOCKERFILE - PIXFORCE INSIGHTS SEARCH
# ===========================================
# 
# Dockerfile simples para o MVP.
# Multi-stage build para otimizar o tamanho da imagem.
#

# ===========================================
# STAGE 1: BUILD
# ===========================================
FROM openjdk:21-jdk-slim AS builder

# Instalar Maven
RUN apt-get update && \
    apt-get install -y maven && \
    rm -rf /var/lib/apt/lists/*

# Definir diretório de trabalho
WORKDIR /app

# Copiar arquivos de configuração do Maven
COPY pom.xml .
COPY mvnw .
COPY .mvn .mvn

# Baixar dependências (cache layer)
RUN mvn dependency:go-offline -B

# Copiar código fonte
COPY src src

# Compilar a aplicação
RUN mvn clean package -DskipTests

# ===========================================
# STAGE 2: RUNTIME
# ===========================================
FROM openjdk:21-jre-slim

# Instalar curl para health checks
RUN apt-get update && \
    apt-get install -y curl && \
    rm -rf /var/lib/apt/lists/*

# Criar usuário não-root
RUN groupadd -r pixforce && useradd -r -g pixforce pixforce

# Definir diretório de trabalho
WORKDIR /app

# Copiar JAR da aplicação
COPY --from=builder /app/target/insights-search-*.jar app.jar

# Alterar propriedade para o usuário pixforce
RUN chown -R pixforce:pixforce /app

# Mudar para usuário não-root
USER pixforce

# Expor porta
EXPOSE 8080

# Health check
HEALTHCHECK --interval=30s --timeout=3s --start-period=60s --retries=3 \
    CMD curl -f http://localhost:8080/api/actuator/health || exit 1

# Comando para executar a aplicação
ENTRYPOINT ["java", "-jar", "app.jar"]
