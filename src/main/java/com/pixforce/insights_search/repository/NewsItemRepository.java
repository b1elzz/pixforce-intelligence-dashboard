package com.pixforce.insights_search.repository;

import com.pixforce.insights_search.entity.NewsItem;
import com.pixforce.insights_search.entity.ProcessingStatus;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

/**
 * ===========================================
 * REPOSITÓRIO NEWS ITEM - ACESSO A DADOS
 * ===========================================
 * 
 * Interface de repositório para operações com notícias brutas.
 * Estende JpaRepository para operações CRUD básicas e adiciona
 * métodos customizados para consultas específicas do negócio.
 * 
 * FUNCIONALIDADES:
 * - Operações CRUD básicas (herdadas do JpaRepository)
 * - Consultas por status de processamento
 * - Busca por período de publicação
 * - Filtros por fonte e palavras-chave
 * - Operações de limpeza de dados antigos
 * 
 * @author Equipe PixForce
 * @version 1.0.0
 * @since 2025-01-27
 */
@Repository
public interface NewsItemRepository extends JpaRepository<NewsItem, Long> {
    
    /**
     * Busca notícias por status de processamento.
     * 
     * @param status Status desejado
     * @param pageable Configuração de paginação
     * @return Página de notícias com o status especificado
     */
    Page<NewsItem> findByStatus(ProcessingStatus status, Pageable pageable);
    
    /**
     * Busca notícias por status de processamento (sem paginação).
     * 
     * @param status Status desejado
     * @return Lista de notícias com o status especificado
     */
    List<NewsItem> findByStatus(ProcessingStatus status);
    
    /**
     * Busca notícias pendentes de processamento.
     * 
     * @return Lista de notícias com status PENDING
     */
    @Query("SELECT n FROM NewsItem n WHERE n.status = 'PENDING' ORDER BY n.publishedAt DESC")
    List<NewsItem> findPendingForProcessing();
    
    /**
     * Busca notícias que falharam no processamento.
     * 
     * @return Lista de notícias com status FAILED
     */
    @Query("SELECT n FROM NewsItem n WHERE n.status = 'FAILED' ORDER BY n.updatedAt DESC")
    List<NewsItem> findFailedForRetry();
    
    /**
     * Busca notícias por fonte.
     * 
     * @param source Nome da fonte
     * @param pageable Configuração de paginação
     * @return Página de notícias da fonte especificada
     */
    Page<NewsItem> findBySource(String source, Pageable pageable);
    
    /**
     * Busca notícias por palavras-chave (busca parcial).
     * 
     * @param keywords Palavras-chave para busca
     * @param pageable Configuração de paginação
     * @return Página de notícias que contenham as palavras-chave
     */
    @Query("SELECT n FROM NewsItem n WHERE n.keywords LIKE %:keywords% OR n.title LIKE %:keywords% OR n.description LIKE %:keywords%")
    Page<NewsItem> findByKeywordsContaining(@Param("keywords") String keywords, Pageable pageable);
    
    /**
     * Busca notícias publicadas em um período específico.
     * 
     * @param startDate Data inicial
     * @param endDate Data final
     * @param pageable Configuração de paginação
     * @return Página de notícias publicadas no período
     */
    @Query("SELECT n FROM NewsItem n WHERE n.publishedAt BETWEEN :startDate AND :endDate ORDER BY n.publishedAt DESC")
    Page<NewsItem> findByPublishedAtBetween(@Param("startDate") LocalDateTime startDate, 
                                           @Param("endDate") LocalDateTime endDate, 
                                           Pageable pageable);
    
    /**
     * Busca notícias criadas em um período específico.
     * 
     * @param startDate Data inicial
     * @param endDate Data final
     * @return Lista de notícias criadas no período
     */
    @Query("SELECT n FROM NewsItem n WHERE n.createdAt BETWEEN :startDate AND :endDate ORDER BY n.createdAt DESC")
    List<NewsItem> findByCreatedAtBetween(@Param("startDate") LocalDateTime startDate, 
                                        @Param("endDate") LocalDateTime endDate);
    
    /**
     * Conta notícias por status.
     * 
     * @param status Status desejado
     * @return Número de notícias com o status especificado
     */
    long countByStatus(ProcessingStatus status);
    
    /**
     * Conta notícias por fonte.
     * 
     * @param source Nome da fonte
     * @return Número de notícias da fonte especificada
     */
    long countBySource(String source);
    
    /**
     * Verifica se já existe uma notícia com a mesma URL.
     * 
     * @param url URL da notícia
     * @return true se já existe, false caso contrário
     */
    boolean existsByUrl(String url);
    
    /**
     * Busca notícia por URL.
     * 
     * @param url URL da notícia
     * @return Optional contendo a notícia se encontrada
     */
    Optional<NewsItem> findByUrl(String url);
    
    /**
     * Remove notícias antigas (para limpeza automática).
     * 
     * @param cutoffDate Data limite para remoção
     * @return Número de registros removidos
     */
    @Modifying
    @Query("DELETE FROM NewsItem n WHERE n.createdAt < :cutoffDate")
    int deleteByCreatedAtBefore(@Param("cutoffDate") LocalDateTime cutoffDate);
    
    /**
     * Remove notícias antigas que não foram processadas.
     * 
     * @param cutoffDate Data limite para remoção
     * @return Número de registros removidos
     */
    @Modifying
    @Query("DELETE FROM NewsItem n WHERE n.createdAt < :cutoffDate AND n.status = 'PENDING'")
    int deleteOldPendingNews(@Param("cutoffDate") LocalDateTime cutoffDate);
    
    /**
     * Remove notícias antigas baseado na data de criação e status.
     * 
     * @param cutoffDate Data limite para remoção
     * @param status Status das notícias a serem removidas
     * @return Número de notícias removidas
     */
    @Modifying
    @Query("DELETE FROM NewsItem n WHERE n.createdAt < :cutoffDate AND n.status = :status")
    int deleteByCreatedAtBeforeAndStatus(@Param("cutoffDate") LocalDateTime cutoffDate, @Param("status") ProcessingStatus status);
    
    /**
     * Atualiza status de notícias em lote.
     * 
     * @param oldStatus Status atual
     * @param newStatus Novo status
     * @return Número de registros atualizados
     */
    @Modifying
    @Query("UPDATE NewsItem n SET n.status = :newStatus WHERE n.status = :oldStatus")
    int updateStatusByStatus(@Param("oldStatus") ProcessingStatus oldStatus, 
                           @Param("newStatus") ProcessingStatus newStatus);
}
