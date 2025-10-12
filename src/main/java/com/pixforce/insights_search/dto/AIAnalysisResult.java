package com.pixforce.insights_search.dto;

import com.pixforce.insights_search.entity.Category;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * ===========================================
 * DTO AI ANALYSIS RESULT - RESULTADO DA ANÁLISE
 * ===========================================
 * 
 * Classe DTO para representar o resultado da análise de IA.
 * Contém todas as informações processadas pelo Gemini Pro.
 * 
 * CAMPOS PRINCIPAIS:
 * - relevante: Se a notícia é relevante para a PixForce
 * - motivo: Explicação do motivo da relevância
 * - categoria: Categoria atribuída (Produto, Parceria, Estratégia)
 * - acaoSugerida: Ação recomendada para a PixForce
 * - scoreConfianca: Score de confiança da análise (0.0 a 1.0)
 * 
 * @author Equipe PixForce
 * @version 1.0.0
 * @since 2025-01-27
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class AIAnalysisResult {
    
    /**
     * Indica se a notícia é relevante para a PixForce.
     */
    private Boolean relevante;
    
    /**
     * Explicação do motivo da relevância.
     */
    private String motivo;
    
    /**
     * Categoria atribuída pela IA.
     */
    private Category categoria;
    
    /**
     * Ação sugerida para a PixForce.
     */
    private String acaoSugerida;
    
    /**
     * Score de confiança da análise (0.0 a 1.0).
     */
    private Double scoreConfianca;
    
    /**
     * Resumo executivo da notícia.
     */
    private String resumoExecutivo;
    
    /**
     * Palavras-chave extraídas pela IA.
     */
    private String palavrasChave;
    
    /**
     * Verifica se a análise foi bem-sucedida.
     * 
     * @return true se tem categoria e relevância definidas, false caso contrário
     */
    public boolean isSuccessful() {
        return relevante != null && categoria != null;
    }
    
    /**
     * Verifica se a notícia é relevante.
     * 
     * @return true se relevante, false caso contrário
     */
    public boolean isRelevant() {
        return Boolean.TRUE.equals(relevante);
    }
    
    /**
     * Verifica se tem score de confiança alto (>= 0.8).
     * 
     * @return true se confiança alta, false caso contrário
     */
    public boolean hasHighConfidence() {
        return scoreConfianca != null && scoreConfianca >= 0.8;
    }
    
    /**
     * Verifica se tem score de confiança médio (0.5 a 0.8).
     * 
     * @return true se confiança média, false caso contrário
     */
    public boolean hasMediumConfidence() {
        return scoreConfianca != null && scoreConfianca >= 0.5 && scoreConfianca < 0.8;
    }
    
    /**
     * Verifica se tem score de confiança baixo (< 0.5).
     * 
     * @return true se confiança baixa, false caso contrário
     */
    public boolean hasLowConfidence() {
        return scoreConfianca != null && scoreConfianca < 0.5;
    }
    
    /**
     * Retorna o nível de confiança como string.
     * 
     * @return "Alta", "Média", "Baixa" ou "N/A"
     */
    public String getNivelConfianca() {
        if (scoreConfianca == null) return "N/A";
        if (scoreConfianca >= 0.8) return "Alta";
        if (scoreConfianca >= 0.5) return "Média";
        return "Baixa";
    }
}
