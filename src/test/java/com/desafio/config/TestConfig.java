package com.desafio.config;

import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.context.annotation.Bean;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.password.NoOpPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.provisioning.InMemoryUserDetailsManager;

// @TestConfiguration
// public class TestConfig {

//      @Bean
//     public UserDetailsService userDetailsService() {
//         return new InMemoryUserDetailsManager(
//             User.withUsername("testuser")
//                 .password("password")
//                 .roles("USER")
//                 .build()
//         );
//     }

//     @Bean
//     public PasswordEncoder passwordEncoder() {
//         return NoOpPasswordEncoder.getInstance();
//     }
// }

