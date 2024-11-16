package com.desafio.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;

/**
 * Configuração de segurança específica para o ambiente de testes.
 *
 * Esta classe de configuração é ativada apenas quando o perfil "test" está
 * ativo.
 * Ela ajusta as configurações de segurança para permitir testes sem exigir
 * autenticação
 * em todas as rotas, e desabilita a proteção CSRF, que não é necessária durante
 * os testes.
 *
 * @author Seu Nome
 */
// @Configuration
// @Profile("test")
// public class TestSecurityConfig {

//     /**
//      * Configura as permissões de acesso para o ambiente de testes.
//      *
//      * @param http o objeto HttpSecurity que permite configurar a segurança de
//      *             requisições HTTP.
//      * @return o objeto SecurityFilterChain configurado.
//      * @throws Exception em caso de erro durante a configuração da segurança HTTP.
//      */
//     @Bean
//     public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
//         http
//                 .csrf().disable()
//                 .authorizeRequests()
//                 .anyRequest().permitAll();
//         return http.build();
//     }

//     @Bean
//     public AuthenticationManager authenticationManager(HttpSecurity http) throws Exception {
//         return http.getSharedObject(AuthenticationManagerBuilder.class).build();
//     }

//     @Bean
//     public PasswordEncoder passwordEncoder() {
//         return new BCryptPasswordEncoder();
//     }
// }
