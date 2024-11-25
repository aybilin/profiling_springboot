package com.profiling.profiling_project;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.CorsRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

@Configuration
public class WebConfig implements WebMvcConfigurer {

    @Bean
    public WebMvcConfigurer corsConfigurer() {
        return new WebMvcConfigurer() {
            @Override
            public void addCorsMappings(CorsRegistry registry) {
                registry.addMapping("/api/**") // Autorise toutes les routes sous /api
                        .allowedOrigins("http://localhost:3000") // URL exacte de votre frontend
                        .allowedMethods("GET", "POST", "PUT", "DELETE", "OPTIONS") // Méthodes HTTP autorisées
                        .allowedHeaders("*") // Autorise tous les en-têtes
                        .exposedHeaders("Authorization") // Permet d'exposer certains en-têtes au frontend
                        .allowCredentials(true); // Nécessaire si vous utilisez des cookies ou l'
                System.out.println("CORS configuration applied!");
            }
        };
    }
}
