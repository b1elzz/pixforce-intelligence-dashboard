package com.pixforce.insights_search.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import java.time.LocalDateTime;

/**
 * ===========================================
 * ENTIDADE PROCESSED NEWS ITEM - NOTÍCIAS ANALISADAS
 * ===========================================
 * 
 * Representa uma notícia após processamento pela IA (Gemini Pro).
 * Esta entidade armazena os insights gerados e as classificações automáticas.
 * 
 * DADOS ARMAZENADOS:
 * - Análise de relevância (relevante ou não)
 * - Categoria automática (Produto, Parceria, Estratégia)
 * - Motivo da relevância (explicação da IA)
 * - Ação sugerida para a PixForce
 * - Score de confiança da análise
 * 
 * RELACIONAMENTOS:
 * - OneToOne com NewsItem (notícia original)
 * 
 * @author Equipe PixForce
 * @version 1.0.0
 * @since 2025-01-27
 */
@Entity
@Table(name = "processed_news_items")
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ProcessedNewsItem {
    
    /**
     * ID único da notícia processada (chave primária).
     * Gerado automaticamente pelo banco de dados.
     */
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    
    /**
     * Referência para a notícia original.
     * Relacionamento OneToOne com NewsItem.
     */
    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "news_item_id", nullable = false, unique = true)
    private NewsItem newsItem;
    
    /**
     * Indica se a notícia é relevante para a PixForce.
     * Resultado da análise de IA (true/false).
     */
    @Column(nullable = false)
    private Boolean isRelevant;
    
    /**
     * Categoria automática atribuída pela IA.
     * Valores: PRODUTO, PARCERIA, ESTRATEGIA
     */
    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private Category category;
    
    /**
     * Explicação do motivo da relevância.
     * Texto gerado pela IA explicando por que a notícia é relevante.
     */
    @Column(columnDefinition = "TEXT")
    private String relevanceReason;
    
    /**
     * Ação sugerida para a PixForce.
     * Recomendação específica baseada na análise da notícia.
     * Ex: "Avaliar oportunidade de parceria", "Monitorar lançamento"
     */
    @Column(columnDefinition = "TEXT")
    private String suggestedAction;
    
    /**
     * Score de confiança da análise (0.0 a 1.0).
     * Indica o nível de certeza da IA na classificação.
     */
    @Column
    private Double confidenceScore;
    
    /**
     * Resumo executivo da notícia.
     * Versão condensada gerada pela IA para leitura rápida.
     */
    @Column(columnDefinition = "TEXT")
    private String executiveSummary;
    
    /**
     * Palavras-chave extraídas pela IA.
     * Termos relevantes identificados automaticamente.
     */
    @Column(length = 1000)
    private String extractedKeywords;
    
    /**
     * Nome do modelo de IA utilizado.
     * Ex: "gemini-1.5-pro", "gemini-1.5-flash"
     */
    @Column(length = 50)
    private String aiModel;
    
    /**
     * Tempo de processamento em milissegundos.
     * Útil para monitoramento de performance.
     */
    @Column(name = "processing_time_ms")
    private Long processingTimeMs;
    
    /**
     * Data de processamento pela IA.
     * Timestamp exato da análise.
     */
    @Column(name = "processed_at", nullable = false)
    private LocalDateTime processedAt;
    
    /**
     * Data de criação do registro no banco.
     * Preenchida automaticamente pelo Hibernate.
     */
    @CreationTimestamp
    @Column(name = "created_at", nullable = false, updatable = false)
    private LocalDateTime createdAt;
    
    /**
     * Data da última atualização do registro.
     * Atualizada automaticamente pelo Hibernate.
     */
    @UpdateTimestamp
    @Column(name = "updated_at", nullable = false)
    private LocalDateTime updatedAt;
    
    /**
     * Verifica se a notícia é relevante e foi categorizada.
     * 
     * @return true se relevante e tem categoria, false caso contrário
     */
    public boolean isRelevantAndCategorized() {
        return Boolean.TRUE.equals(isRelevant) && category != null;
    }
    
    /**
     * Verifica se tem score de confiança alto (>= 0.8).
     * 
     * @return true se confiança alta, false caso contrário
     */
    public boolean hasHighConfidence() {
        return confidenceScore != null && confidenceScore >= 0.8;
    }
    
    /**
     * Verifica se tem score de confiança médio (0.5 a 0.8).
     * 
     * @return true se confiança média, false caso contrário
     */
    public boolean hasMediumConfidence() {
        return confidenceScore != null && confidenceScore >= 0.5 && confidenceScore < 0.8;
    }
    
    /**
     * Verifica se tem score de confiança baixo (< 0.5).
     * 
     * @return true se confiança baixa, false caso contrário
     */
    public boolean hasLowConfidence() {
        return confidenceScore != null && confidenceScore < 0.5;
    }
    
    /**
     * Retorna o nível de confiança como string.
     * 
     * @return "Alta", "Média" ou "Baixa"
     */
    public String getConfidenceLevel() {
        if (confidenceScore == null) return "N/A";
        if (confidenceScore >= 0.8) return "Alta";
        if (confidenceScore >= 0.5) return "Média";
        return "Baixa";
    }
}