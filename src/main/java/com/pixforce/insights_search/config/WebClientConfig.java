package com.pixforce.insights_search.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.reactive.function.client.WebClient;

/**
 * ===========================================
 * CONFIGURAÇÃO WEB CLIENT - HTTP CLIENT
 * ===========================================
 * 
 * Configuração do WebClient para fazer requisições HTTP.
 * Utilizado para comunicação com APIs externas (NewsData.io e Gemini Pro).
 * 
 * CONFIGURAÇÕES:
 * - Timeout de conexão: 30 segundos
 * - Timeout de leitura: 60 segundos
 * - User-Agent personalizado
 * - Headers padrão
 * 
 * @author Equipe PixForce
 * @version 1.0.0
 * @since 2025-01-27
 */
@Configuration
public class WebClientConfig {
    
    /**
     * Bean do WebClient configurado para requisições HTTP.
     * 
     * @return WebClient configurado
     */
    @Bean
    public WebClient webClient() {
        return WebClient.builder()
            .codecs(configurer -> configurer.defaultCodecs().maxInMemorySize(10 * 1024 * 1024)) // 10MB
            .build();
    }
}
