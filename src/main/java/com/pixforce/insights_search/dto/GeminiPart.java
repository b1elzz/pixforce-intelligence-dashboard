package com.pixforce.insights_search.dto;

import lombok.Data;

/**
 * ===========================================
 * DTO GEMINI PART - PARTE DO CONTEÚDO
 * ===========================================
 * 
 * Classe DTO para mapear uma parte do conteúdo da resposta do Gemini Pro.
 * Representa uma parte específica da resposta da IA.
 * 
 * @author Equipe PixForce
 * @version 1.0.0
 * @since 2025-01-27
 */
@Data
public class GeminiPart {
    
    /**
     * Texto da parte da resposta.
     */
    private String text;
}
