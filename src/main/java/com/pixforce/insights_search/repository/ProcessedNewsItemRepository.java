package com.pixforce.insights_search.repository;

import com.pixforce.insights_search.entity.Category;
import com.pixforce.insights_search.entity.ProcessedNewsItem;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;

/**
 * ===========================================
 * REPOSITÓRIO PROCESSED NEWS ITEM - ACESSO A DADOS
 * ===========================================
 * 
 * Interface de repositório para operações com notícias processadas pela IA.
 * Estende JpaRepository para operações CRUD básicas e adiciona
 * métodos customizados para consultas específicas de insights.
 * 
 * FUNCIONALIDADES:
 * - Operações CRUD básicas (herdadas do JpaRepository)
 * - Consultas por categoria e relevância
 * - Busca por período de processamento
 * - Filtros por score de confiança
 * - Operações de limpeza de dados antigos
 * 
 * @author Equipe PixForce
 * @version 1.0.0
 * @since 2025-01-27
 */
@Repository
public interface ProcessedNewsItemRepository extends JpaRepository<ProcessedNewsItem, Long> {
    
    /**
     * Busca notícias processadas por categoria.
     * 
     * @param category Categoria desejada
     * @param pageable Configuração de paginação
     * @return Página de notícias da categoria especificada
     */
    Page<ProcessedNewsItem> findByCategory(Category category, Pageable pageable);
    
    /**
     * Busca notícias processadas por categoria (sem paginação).
     * 
     * @param category Categoria desejada
     * @return Lista de notícias da categoria especificada
     */
    List<ProcessedNewsItem> findByCategory(Category category);
    
    /**
     * Busca apenas notícias relevantes.
     * 
     * @param pageable Configuração de paginação
     * @return Página de notícias relevantes
     */
    Page<ProcessedNewsItem> findByIsRelevantTrue(Pageable pageable);
    
    /**
     * Busca apenas notícias relevantes (sem paginação).
     * 
     * @return Lista de notícias relevantes
     */
    List<ProcessedNewsItem> findByIsRelevantTrue();
    
    /**
     * Busca notícias relevantes por categoria.
     * 
     * @param category Categoria desejada
     * @param pageable Configuração de paginação
     * @return Página de notícias relevantes da categoria especificada
     */
    Page<ProcessedNewsItem> findByIsRelevantTrueAndCategory(Category category, Pageable pageable);
    
    /**
     * Busca notícias relevantes por categoria (sem paginação).
     * 
     * @param category Categoria desejada
     * @return Lista de notícias relevantes da categoria especificada
     */
    List<ProcessedNewsItem> findByIsRelevantTrueAndCategory(Category category);
    
    /**
     * Busca notícias por score de confiança mínimo.
     * 
     * @param minScore Score mínimo desejado
     * @param pageable Configuração de paginação
     * @return Página de notícias com score >= minScore
     */
    @Query("SELECT p FROM ProcessedNewsItem p WHERE p.confidenceScore >= :minScore ORDER BY p.confidenceScore DESC")
    Page<ProcessedNewsItem> findByConfidenceScoreGreaterThanEqual(@Param("minScore") Double minScore, Pageable pageable);
    
    /**
     * Busca notícias por score de confiança mínimo (sem paginação).
     * 
     * @param minScore Score mínimo desejado
     * @return Lista de notícias com score >= minScore
     */
    @Query("SELECT p FROM ProcessedNewsItem p WHERE p.confidenceScore >= :minScore ORDER BY p.confidenceScore DESC")
    List<ProcessedNewsItem> findByConfidenceScoreGreaterThanEqual(@Param("minScore") Double minScore);
    
    /**
     * Busca notícias processadas em um período específico.
     * 
     * @param startDate Data inicial
     * @param endDate Data final
     * @param pageable Configuração de paginação
     * @return Página de notícias processadas no período
     */
    @Query("SELECT p FROM ProcessedNewsItem p WHERE p.processedAt BETWEEN :startDate AND :endDate ORDER BY p.processedAt DESC")
    Page<ProcessedNewsItem> findByProcessedAtBetween(@Param("startDate") LocalDateTime startDate, 
                                                    @Param("endDate") LocalDateTime endDate, 
                                                    Pageable pageable);
    
    /**
     * Busca notícias processadas em um período específico (sem paginação).
     * 
     * @param startDate Data inicial
     * @param endDate Data final
     * @return Lista de notícias processadas no período
     */
    @Query("SELECT p FROM ProcessedNewsItem p WHERE p.processedAt BETWEEN :startDate AND :endDate ORDER BY p.processedAt DESC")
    List<ProcessedNewsItem> findByProcessedAtBetween(@Param("startDate") LocalDateTime startDate, 
                                                    @Param("endDate") LocalDateTime endDate);
    
    /**
     * Busca notícias por modelo de IA utilizado.
     * 
     * @param aiModel Nome do modelo de IA
     * @param pageable Configuração de paginação
     * @return Página de notícias processadas pelo modelo especificado
     */
    Page<ProcessedNewsItem> findByAiModel(String aiModel, Pageable pageable);
    
    /**
     * Busca notícias por modelo de IA utilizado (sem paginação).
     * 
     * @param aiModel Nome do modelo de IA
     * @return Lista de notícias processadas pelo modelo especificado
     */
    List<ProcessedNewsItem> findByAiModel(String aiModel);
    
    /**
     * Conta notícias por categoria.
     * 
     * @param category Categoria desejada
     * @return Número de notícias da categoria especificada
     */
    long countByCategory(Category category);
    
    /**
     * Conta notícias relevantes.
     * 
     * @return Número de notícias relevantes
     */
    long countByIsRelevantTrue();
    
    /**
     * Conta notícias relevantes por categoria.
     * 
     * @param category Categoria desejada
     * @return Número de notícias relevantes da categoria especificada
     */
    long countByIsRelevantTrueAndCategory(Category category);
    
    /**
     * Conta notícias por modelo de IA.
     * 
     * @param aiModel Nome do modelo de IA
     * @return Número de notícias processadas pelo modelo especificado
     */
    long countByAiModel(String aiModel);
    
    /**
     * Busca notícias com alta confiança (score >= 0.8).
     * 
     * @param pageable Configuração de paginação
     * @return Página de notícias com alta confiança
     */
    @Query("SELECT p FROM ProcessedNewsItem p WHERE p.confidenceScore >= 0.8 ORDER BY p.confidenceScore DESC")
    Page<ProcessedNewsItem> findHighConfidenceNews(Pageable pageable);
    
    /**
     * Busca notícias com alta confiança (sem paginação).
     * 
     * @return Lista de notícias com alta confiança
     */
    @Query("SELECT p FROM ProcessedNewsItem p WHERE p.confidenceScore >= 0.8 ORDER BY p.confidenceScore DESC")
    List<ProcessedNewsItem> findHighConfidenceNews();
    
    /**
     * Busca notícias com baixa confiança (score < 0.5).
     * 
     * @param pageable Configuração de paginação
     * @return Página de notícias com baixa confiança
     */
    @Query("SELECT p FROM ProcessedNewsItem p WHERE p.confidenceScore < 0.5 ORDER BY p.confidenceScore ASC")
    Page<ProcessedNewsItem> findLowConfidenceNews(Pageable pageable);
    
    /**
     * Busca notícias com baixa confiança (sem paginação).
     * 
     * @return Lista de notícias com baixa confiança
     */
    @Query("SELECT p FROM ProcessedNewsItem p WHERE p.confidenceScore < 0.5 ORDER BY p.confidenceScore ASC")
    List<ProcessedNewsItem> findLowConfidenceNews();
    
    /**
     * Remove notícias processadas antigas (para limpeza automática).
     * 
     * @param cutoffDate Data limite para remoção
     * @return Número de registros removidos
     */
    @Modifying
    @Query("DELETE FROM ProcessedNewsItem p WHERE p.createdAt < :cutoffDate")
    int deleteByCreatedAtBefore(@Param("cutoffDate") LocalDateTime cutoffDate);
    
    /**
     * Busca estatísticas de processamento por categoria.
     * 
     * @return Lista de arrays contendo [categoria, count]
     */
    @Query("SELECT p.category, COUNT(p) FROM ProcessedNewsItem p GROUP BY p.category")
    List<Object[]> getCategoryStatistics();
    
    /**
     * Busca estatísticas de processamento por modelo de IA.
     * 
     * @return Lista de arrays contendo [modelo, count]
     */
    @Query("SELECT p.aiModel, COUNT(p) FROM ProcessedNewsItem p GROUP BY p.aiModel")
    List<Object[]> getAiModelStatistics();
    
    /**
     * Busca notícias por categoria que não são relevantes.
     * 
     * @param category Categoria desejada
     * @param pageable Configuração de paginação
     * @return Página de notícias não relevantes da categoria
     */
    @Query("SELECT p FROM ProcessedNewsItem p WHERE p.category = :category AND p.isRelevant = false ORDER BY p.processedAt DESC")
    Page<ProcessedNewsItem> findByCategoryAndIsRelevantFalse(@Param("category") Category category, Pageable pageable);
    
    /**
     * Busca notícias que não são relevantes.
     * 
     * @param pageable Configuração de paginação
     * @return Página de notícias não relevantes
     */
    @Query("SELECT p FROM ProcessedNewsItem p WHERE p.isRelevant = false ORDER BY p.processedAt DESC")
    Page<ProcessedNewsItem> findByIsRelevantFalse(Pageable pageable);
}
