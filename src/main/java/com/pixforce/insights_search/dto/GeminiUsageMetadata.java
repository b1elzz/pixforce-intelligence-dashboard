package com.pixforce.insights_search.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;

/**
 * ===========================================
 * DTO GEMINI USAGE METADATA - METADADOS DE USO
 * ===========================================
 * 
 * Classe DTO para mapear os metadados de uso da API Gemini Pro.
 * Contém informações sobre tokens utilizados e custos.
 * 
 * @author Equipe PixForce
 * @version 1.0.0
 * @since 2025-01-27
 */
@Data
public class GeminiUsageMetadata {
    
    /**
     * Número de tokens do prompt.
     */
    @JsonProperty("promptTokenCount")
    private Integer promptTokenCount;
    
    /**
     * Número de tokens da resposta.
     */
    @JsonProperty("candidatesTokenCount")
    private Integer candidatesTokenCount;
    
    /**
     * Número total de tokens utilizados.
     */
    @JsonProperty("totalTokenCount")
    private Integer totalTokenCount;
    
    /**
     * Retorna o total de tokens utilizados.
     * 
     * @return Total de tokens ou 0 se não disponível
     */
    public int getTotalTokens() {
        return totalTokenCount != null ? totalTokenCount : 0;
    }
    
    /**
     * Verifica se há informações de uso disponíveis.
     * 
     * @return true se há metadados, false caso contrário
     */
    public boolean hasUsageInfo() {
        return totalTokenCount != null && totalTokenCount > 0;
    }
}
