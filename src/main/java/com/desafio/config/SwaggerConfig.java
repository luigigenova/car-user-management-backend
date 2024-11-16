package com.desafio.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springdoc.core.models.GroupedOpenApi;

/**
 * Classe de configuração para habilitar e configurar o Swagger.
 * O Swagger é utilizado para documentar e testar a API REST.
 */
@Configuration
public class SwaggerConfig {

    /**
     * Cria o bean GroupedOpenApi para configuração do Swagger.
     * Define quais pacotes e caminhos devem ser documentados pela interface Swagger.
     *
     * @return um objeto GroupedOpenApi configurado para o Swagger
     */
    @Bean
    public GroupedOpenApi api() {
        return GroupedOpenApi.builder()
                .group("com.desafio")
                .packagesToScan("com.desafio.controller")
                .pathsToMatch("/**")
                .build();
    }
}
