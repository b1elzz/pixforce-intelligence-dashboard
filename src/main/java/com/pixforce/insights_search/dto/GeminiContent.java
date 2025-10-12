package com.pixforce.insights_search.dto;

import lombok.Data;

import java.util.List;

/**
 * ===========================================
 * DTO GEMINI CONTENT - CONTEÚDO DA RESPOSTA
 * ===========================================
 * 
 * Classe DTO para mapear o conteúdo da resposta do Gemini Pro.
 * Contém as partes da resposta da IA.
 * 
 * @author Equipe PixForce
 * @version 1.0.0
 * @since 2025-01-27
 */
@Data
public class GeminiContent {
    
    /**
     * Lista de partes do conteúdo.
     */
    private List<GeminiPart> parts;
    
    /**
     * Retorna o texto da resposta.
     * 
     * @return Texto da resposta ou null se não houver
     */
    public String getText() {
        if (parts != null && !parts.isEmpty() && parts.get(0) != null) {
            return parts.get(0).getText();
        }
        return null;
    }
}
