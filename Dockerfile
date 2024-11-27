# Usar a imagem base do OpenJDK para Java 17
FROM openjdk:17-jdk-slim

# Diretório de trabalho dentro do container
WORKDIR /app

# Copiar o JAR gerado pelo Maven para o container
COPY target/car-user-management-backend-0.0.1-SNAPSHOT.jar app.jar

# Expor a porta usada pelo Spring Boot
EXPOSE 8080

# Configurar variáveis de ambiente necessárias
ENV SPRING_PROFILES_ACTIVE=prod
ENV SPRING_DATASOURCE_URL=jdbc:h2:mem:testdb;DB_CLOSE_DELAY=-1
ENV SPRING_DATASOURCE_USERNAME=sa
ENV SPRING_DATASOURCE_PASSWORD=password
ENV SPRING_H2_CONSOLE_ENABLED=true
ENV SPRING_H2_CONSOLE_PATH=/h2-console
ENV JWT_SECRET=Y2ZQeSpcGcGxLp4QxL9vQnLz1BKzX2YFJ3T6WQ8MnP5

# Comando para iniciar a aplicação Spring Boot
ENTRYPOINT ["java", "-jar", "app.jar"]
