package com.profiling.profiling_project;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.CorsRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

@Configuration
public class WebConfig {

    @Bean
    public WebMvcConfigurer corsConfigurer() {
        return new WebMvcConfigurer() {
            @Override
            public void addCorsMappings(CorsRegistry registry) {
                // Autoriser les requêtes venant de votre frontend React
                registry.addMapping("/api/**") // Toutes les routes sous /auth
                        .allowedOrigins("http://localhost:3000/") // L'URL de votre frontend React
                        .allowedMethods("GET", "POST", "PUT", "DELETE") // Les méthodes HTTP autorisées
                        .allowedHeaders("*") // Autoriser tous les en-têtes
                        .allowCredentials(true); // Permet l'envoi des cookies, par exemple pour l'authentification

            }
        };
    }
}
