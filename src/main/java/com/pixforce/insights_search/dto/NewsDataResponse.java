package com.pixforce.insights_search.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;

import java.util.List;

/**
 * ===========================================
 * DTO NEWS DATA RESPONSE - RESPOSTA DA API
 * ===========================================
 * 
 * Classe DTO para mapear a resposta da API NewsData.io.
 * Utilizada para deserializar o JSON retornado pela API.
 * 
 * ESTRUTURA DA RESPOSTA:
 * - status: Status da requisição
 * - totalResults: Total de resultados encontrados
 * - results: Lista de artigos/notícias
 * 
 * @author Equipe PixForce
 * @version 1.0.0
 * @since 2025-01-27
 */
@Data
public class NewsDataResponse {
    
    /**
     * Status da requisição.
     * Valores possíveis: "success", "error"
     */
    private String status;
    
    /**
     * Total de resultados encontrados.
     */
    @JsonProperty("totalResults")
    private Integer totalResults;
    
    /**
     * Lista de artigos/notícias retornados.
     */
    private List<NewsDataArticle> results;
    
    /**
     * Código de erro (se houver).
     */
    private String code;
    
    /**
     * Mensagem de erro (se houver).
     */
    private String message;
    
    /**
     * Verifica se a resposta foi bem-sucedida.
     * 
     * @return true se status é "success", false caso contrário
     */
    public boolean isSuccess() {
        return "success".equals(status);
    }
    
    /**
     * Verifica se há resultados disponíveis.
     * 
     * @return true se há resultados, false caso contrário
     */
    public boolean hasResults() {
        return results != null && !results.isEmpty();
    }
    
    /**
     * Retorna o número de resultados disponíveis.
     * 
     * @return Número de resultados ou 0 se não houver
     */
    public int getResultCount() {
        return results != null ? results.size() : 0;
    }
}
