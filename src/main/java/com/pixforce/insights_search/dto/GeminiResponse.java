package com.pixforce.insights_search.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;

import java.util.List;

/**
 * ===========================================
 * DTO GEMINI RESPONSE - RESPOSTA DA IA
 * ===========================================
 * 
 * Classe DTO para mapear a resposta da API Gemini Pro.
 * Utilizada para deserializar o JSON retornado pela IA.
 * 
 * ESTRUTURA DA RESPOSTA:
 * - candidates: Lista de candidatos de resposta
 * - usageMetadata: Metadados de uso da API
 * 
 * @author Equipe PixForce
 * @version 1.0.0
 * @since 2025-01-27
 */
@Data
public class GeminiResponse {
    
    /**
     * Lista de candidatos de resposta da IA.
     */
    private List<GeminiCandidate> candidates;
    
    /**
     * Metadados de uso da API.
     */
    @JsonProperty("usageMetadata")
    private GeminiUsageMetadata usageMetadata;
    
    /**
     * Verifica se a resposta foi bem-sucedida.
     * 
     * @return true se há candidatos válidos, false caso contrário
     */
    public boolean isSuccess() {
        return candidates != null && !candidates.isEmpty() && 
               candidates.get(0) != null && candidates.get(0).getContent() != null;
    }
    
    /**
     * Retorna o primeiro candidato de resposta.
     * 
     * @return Primeiro candidato ou null se não houver
     */
    public GeminiCandidate getFirstCandidate() {
        return candidates != null && !candidates.isEmpty() ? candidates.get(0) : null;
    }
    
    /**
     * Retorna o texto da resposta da IA.
     * 
     * @return Texto da resposta ou null se não houver
     */
    public String getResponseText() {
        GeminiCandidate candidate = getFirstCandidate();
        return candidate != null ? candidate.getResponseText() : null;
    }
}
