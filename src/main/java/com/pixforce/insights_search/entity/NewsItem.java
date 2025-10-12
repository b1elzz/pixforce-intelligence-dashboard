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
 * ENTIDADE NEWS ITEM - NOTÍCIAS BRUTAS
 * ===========================================
 * 
 * Representa uma notícia coletada diretamente da API NewsData.io.
 * Esta entidade armazena os dados brutos antes do processamento pela IA.
 * 
 * DADOS ARMAZENADOS:
 * - Informações básicas da notícia (título, descrição, URL)
 * - Metadados da fonte (autor, data de publicação)
 * - Status de processamento
 * - Timestamps de auditoria
 * 
 * RELACIONAMENTOS:
 * - OneToOne com ProcessedNewsItem (quando processada)
 * 
 * @author Equipe PixForce
 * @version 1.0.0
 * @since 2025-01-27
 */
@Entity
@Table(name = "news_items")
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class NewsItem {
    
    /**
     * ID único da notícia (chave primária).
     * Gerado automaticamente pelo banco de dados.
     */
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    
    /**
     * Título da notícia.
     * Campo obrigatório, máximo 500 caracteres.
     */
    @Column(nullable = false, length = 500)
    private String title;
    
    /**
     * Descrição/resumo da notícia.
     * Campo obrigatório, máximo 2000 caracteres.
     */
    @Column(nullable = false, length = 2000)
    private String description;
    
    /**
     * URL original da notícia.
     * Campo obrigatório, máximo 1000 caracteres.
     */
    @Column(nullable = false, length = 1000)
    private String url;
    
    /**
     * URL da imagem da notícia (se disponível).
     * Campo opcional, máximo 1000 caracteres.
     */
    @Column(length = 1000)
    private String imageUrl;
    
    /**
     * Nome da fonte da notícia.
     * Ex: "TecMundo", "Olhar Digital", "CanalTech"
     */
    @Column(length = 100)
    private String source;
    
    /**
     * Nome do autor da notícia (se disponível).
     * Campo opcional, máximo 200 caracteres.
     */
    @Column(length = 200)
    private String author;
    
    /**
     * Data de publicação original da notícia.
     * Importante para ordenação cronológica.
     */
    @Column(name = "published_at")
    private LocalDateTime publishedAt;
    
    /**
     * Status atual do processamento da notícia.
     * Inicia como PENDING e evolui conforme o pipeline.
     */
    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    @Builder.Default
    private ProcessingStatus status = ProcessingStatus.PENDING;
    
    /**
     * Conteúdo completo da notícia (se disponível).
     * Campo opcional, pode ser muito grande.
     */
    @Column(columnDefinition = "TEXT")
    private String content;
    
    /**
     * Palavras-chave que levaram à coleta desta notícia.
     * Ex: "inteligencia artificial", "visao computacional"
     */
    @Column(length = 500)
    private String keywords;
    
    /**
     * Idioma da notícia.
     * Padrão: "pt" (português)
     */
    @Column(length = 5)
    @Builder.Default
    private String language = "pt";
    
    /**
     * País de origem da notícia.
     * Padrão: "br" (Brasil)
     */
    @Column(length = 5)
    @Builder.Default
    private String country = "br";
    
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
     * Relacionamento com a notícia processada.
     * OneToOne: uma notícia bruta pode ter uma versão processada.
     */
    @OneToOne(mappedBy = "newsItem", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    private ProcessedNewsItem processedNewsItem;
    
    /**
     * Verifica se a notícia já foi processada pela IA.
     * 
     * @return true se já foi processada, false caso contrário
     */
    public boolean isProcessed() {
        return processedNewsItem != null;
    }
    
    /**
     * Verifica se a notícia está pronta para processamento.
     * 
     * @return true se status é PENDING, false caso contrário
     */
    public boolean isReadyForProcessing() {
        return status == ProcessingStatus.PENDING;
    }
    
    /**
     * Verifica se houve falha no processamento.
     * 
     * @return true se status é FAILED, false caso contrário
     */
    public boolean hasFailed() {
        return status == ProcessingStatus.FAILED;
    }
}
