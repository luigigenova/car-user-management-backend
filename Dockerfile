# Usar imagem base do OpenJDK
FROM openjdk:11-jdk-slim

# Diretório de trabalho
WORKDIR /app

# Copiar o JAR do build para o container
COPY target/car-user-management-backend-*.jar app.jar

# Expor a porta usada pelo Spring Boot
EXPOSE 8080

# Comando para rodar a aplicação
ENTRYPOINT ["java", "-jar", "app.jar"]
