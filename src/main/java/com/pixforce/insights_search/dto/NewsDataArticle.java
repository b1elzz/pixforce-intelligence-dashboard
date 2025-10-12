package com.pixforce.insights_search.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;

import java.time.LocalDateTime;
import java.util.List;

/**
 * ===========================================
 * DTO NEWS DATA ARTICLE - ARTIGO INDIVIDUAL
 * ===========================================
 * 
 * Classe DTO para mapear um artigo individual da API NewsData.io.
 * Representa uma notícia específica com todos os seus metadados.
 * 
 * CAMPOS PRINCIPAIS:
 * - title: Título da notícia
 * - description: Descrição/resumo
 * - content: Conteúdo completo (se disponível)
 * - url: URL original da notícia
 * - image: URL da imagem
 * - source: Fonte da notícia
 * - author: Autor da notícia
 * - publishedAt: Data de publicação
 * 
 * @author Equipe PixForce
 * @version 1.0.0
 * @since 2025-01-27
 */
@Data
public class NewsDataArticle {
    
    /**
     * Título da notícia.
     */
    private String title;
    
    /**
     * Descrição/resumo da notícia.
     */
    private String description;
    
    /**
     * Conteúdo completo da notícia (se disponível).
     */
    private String content;
    
    /**
     * URL original da notícia.
     */
    private String url;
    
    /**
     * URL da imagem da notícia.
     */
    private String image;
    
    /**
     * Nome da fonte da notícia.
     */
    private String source;
    
    /**
     * Nome do autor da notícia.
     */
    private String author;
    
    /**
     * Data de publicação da notícia.
     */
    @JsonProperty("pubDate")
    private LocalDateTime publishedAt;
    
    /**
     * Idioma da notícia.
     */
    private String language;
    
    /**
     * País de origem da notícia.
     */
    private String country;
    
    /**
     * Categoria da notícia.
     */
    private String category;
    
    /**
     * Lista de palavras-chave associadas.
     */
    private List<String> keywords;
    
    /**
     * Score de relevância (se disponível).
     */
    private Double relevanceScore;
    
    /**
     * Verifica se a notícia tem conteúdo válido.
     * 
     * @return true se tem título e descrição, false caso contrário
     */
    public boolean isValid() {
        return title != null && !title.trim().isEmpty() && 
               description != null && !description.trim().isEmpty() &&
               url != null && !url.trim().isEmpty();
    }
    
    /**
     * Verifica se a notícia tem imagem.
     * 
     * @return true se tem URL de imagem, false caso contrário
     */
    public boolean hasImage() {
        return image != null && !image.trim().isEmpty();
    }
    
    /**
     * Verifica se a notícia tem autor.
     * 
     * @return true se tem autor, false caso contrário
     */
    public boolean hasAuthor() {
        return author != null && !author.trim().isEmpty();
    }
    
    /**
     * Retorna o título truncado para exibição.
     * 
     * @param maxLength Comprimento máximo
     * @return Título truncado
     */
    public String getTruncatedTitle(int maxLength) {
        if (title == null) return "";
        return title.length() > maxLength ? title.substring(0, maxLength) + "..." : title;
    }
    
    /**
     * Retorna a descrição truncada para exibição.
     * 
     * @param maxLength Comprimento máximo
     * @return Descrição truncada
     */
    public String getTruncatedDescription(int maxLength) {
        if (description == null) return "";
        return description.length() > maxLength ? description.substring(0, maxLength) + "..." : description;
    }
}
