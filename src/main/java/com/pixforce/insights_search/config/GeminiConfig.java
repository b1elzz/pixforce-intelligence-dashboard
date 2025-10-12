package com.pixforce.insights_search.config;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;

/**
 * ===========================================
 * CONFIGURAÇÃO GEMINI PRO API
 * ===========================================
 * 
 * Classe de configuração para a API Gemini Pro do Google.
 * Mapeia as propriedades do application.properties para um objeto Java.
 * 
 * PROPRIEDADES CONFIGURÁVEIS:
 * - Chave da API (gemini.api.key)
 * - URL base da API (gemini.api.base-url)
 * - Modelo de IA (gemini.api.model)
 * 
 * USO:
 * @Autowired GeminiConfig geminiConfig;
 * String apiKey = geminiConfig.getApi().getKey();
 * 
 * @author Equipe PixForce
 * @version 1.0.0
 * @since 2025-01-27
 */
@Configuration
@ConfigurationProperties(prefix = "gemini.api")
@Data
public class GeminiConfig {
    
    /**
     * Chave da API Gemini Pro.
     * Obtida do arquivo .env ou application.properties.
     */
    private String key;
    
    /**
     * URL base da API Gemini Pro.
     * Padrão: https://generativelanguage.googleapis.com/v1beta
     */
    private String baseUrl = "https://generativelanguage.googleapis.com/v1beta";
    
    /**
     * Modelo de IA a ser utilizado.
     * Padrão: "gemini-1.5-pro"
     */
    private String model = "gemini-1.5-pro";
    
    /**
     * Verifica se a configuração está válida.
     * 
     * @return true se a chave da API está configurada, false caso contrário
     */
    public boolean isValid() {
        return key != null && !key.trim().isEmpty() && !key.equals("your_gemini_key_here");
    }
    
    /**
     * Retorna a URL completa da API para o modelo especificado.
     * 
     * @return URL formatada para requisições
     */
    public String getApiUrl() {
        return baseUrl + "/models/" + model + ":generateContent";
    }
    
    /**
     * Retorna a URL completa da API com chave incluída.
     * 
     * @return URL formatada com chave de autenticação
     */
    public String getApiUrlWithKey() {
        return getApiUrl() + "?key=" + key;
    }
    
    /**
     * Retorna o nome do modelo para logs e auditoria.
     * 
     * @return Nome do modelo de IA
     */
    public String getModelName() {
        return model;
    }
}
