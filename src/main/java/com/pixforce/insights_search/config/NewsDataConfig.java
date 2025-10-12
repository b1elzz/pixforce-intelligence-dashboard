package com.pixforce.insights_search.config;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;

/**
 * ===========================================
 * CONFIGURAÇÃO NEWS DATA API
 * ===========================================
 * 
 * Classe de configuração para a API NewsData.io.
 * Mapeia as propriedades do application.properties para um objeto Java.
 * 
 * PROPRIEDADES CONFIGURÁVEIS:
 * - Chave da API (newsdata.api.key)
 * - URL base da API (newsdata.api.base-url)
 * - Idioma padrão (newsdata.api.language)
 * - País padrão (newsdata.api.country)
 * 
 * USO:
 * @Autowired NewsDataConfig newsDataConfig;
 * String apiKey = newsDataConfig.getApi().getKey();
 * 
 * @author Equipe PixForce
 * @version 1.0.0
 * @since 2025-01-27
 */
@Configuration
@ConfigurationProperties(prefix = "newsdata.api")
@Data
public class NewsDataConfig {
    
    /**
     * Chave da API NewsData.io.
     * Obtida do arquivo .env ou application.properties.
     */
    private String key;
    
    /**
     * URL base da API NewsData.io.
     * Padrão: https://newsdata.io/api/1/news
     */
    private String baseUrl = "https://newsdata.io/api/1/news";
    
    /**
     * Idioma padrão para as notícias.
     * Padrão: "pt" (português)
     */
    private String language = "pt";
    
    /**
     * País padrão para as notícias.
     * Padrão: "br" (Brasil)
     */
    private String country = "br";
    
    /**
     * Verifica se a configuração está válida.
     * 
     * @return true se a chave da API está configurada, false caso contrário
     */
    public boolean isValid() {
        return key != null && !key.trim().isEmpty() && !key.equals("your_newsdata_key_here");
    }
    
    /**
     * Retorna a URL completa da API com parâmetros básicos.
     * 
     * @return URL formatada para requisições
     */
    public String getApiUrl() {
        return baseUrl + "?apikey=" + key + "&language=" + language + "&country=" + country;
    }
    
    /**
     * Retorna a URL da API com query personalizada.
     * 
     * @param query Query de busca personalizada
     * @return URL formatada com query
     */
    public String getApiUrlWithQuery(String query) {
        return getApiUrl() + "&q=" + query;
    }
}
