package com.pixforce.insights_search.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;

/**
 * ===========================================
 * DTO GEMINI CANDIDATE - CANDIDATO DE RESPOSTA
 * ===========================================
 * 
 * Classe DTO para mapear um candidato de resposta do Gemini Pro.
 * Representa uma possível resposta da IA com seu conteúdo e metadados.
 * 
 * @author Equipe PixForce
 * @version 1.0.0
 * @since 2025-01-27
 */
@Data
public class GeminiCandidate {
    
    /**
     * Conteúdo da resposta.
     */
    private GeminiContent content;
    
    /**
     * Motivo de finalização da resposta.
     */
    @JsonProperty("finishReason")
    private String finishReason;
    
    /**
     * Índice do candidato.
     */
    private Integer index;
    
    /**
     * Verifica se a resposta foi finalizada com sucesso.
     * 
     * @return true se finishReason é "STOP", false caso contrário
     */
    public boolean isFinished() {
        return "STOP".equals(finishReason);
    }
    
    /**
     * Retorna o texto da resposta.
     * 
     * @return Texto da resposta ou null se não houver
     */
    public String getResponseText() {
        return content != null ? content.getText() : null;
    }
}
