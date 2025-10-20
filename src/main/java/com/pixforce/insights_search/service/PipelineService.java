package com.pixforce.insights_search.service;

import com.pixforce.insights_search.entity.Category;
import com.pixforce.insights_search.entity.NewsItem;
import com.pixforce.insights_search.entity.ProcessedNewsItem;
import com.pixforce.insights_search.entity.ProcessingStatus;
import com.pixforce.insights_search.repository.NewsItemRepository;
import com.pixforce.insights_search.repository.ProcessedNewsItemRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * ===========================================
 * SERVIÇO PIPELINE - ORQUESTRAÇÃO DO PROCESSO
 * ===========================================
 * 
 * Serviço principal que orquestra todo o pipeline de coleta e análise.
 * Coordena a execução dos serviços de coleta (NewsData) e análise (Gemini).
 * 
 * FLUXO DO PIPELINE:
 * 1. Coleta de notícias (NewsDataService)
 * 2. Análise de relevância (GeminiService)
 * 3. Armazenamento de resultados
 * 4. Limpeza de dados antigos
 * 
 * FUNCIONALIDADES:
 * - Execução completa do pipeline
 * - Processamento de notícias pendentes
 * - Retry de notícias que falharam
 * - Limpeza automática de dados antigos
 * - Estatísticas de execução
 * 
 * @author Equipe PixForce
 * @version 1.0.0
 * @since 2025-01-27
 */
@Service
@RequiredArgsConstructor
@Slf4j
public class PipelineService {
    
    private final NewsDataService newsDataService;
    private final GeminiService geminiService;
    private final NewsItemRepository newsItemRepository;
    private final ProcessedNewsItemRepository processedNewsItemRepository;
    
    /**
     * Executa o pipeline completo de coleta e análise.
     * 
     * @return Estatísticas da execução
     */
    public PipelineExecutionResult executePipeline() {
        log.info("=== INICIANDO PIPELINE DE COLETA E ANÁLISE ===");
        
        long startTime = System.currentTimeMillis();
        PipelineExecutionResult result = new PipelineExecutionResult();
        
        try {
            // 1. Coleta de notícias
            log.info("Fase 1: Coletando notícias...");
            int collectedNews = newsDataService.collectAllNews();
            result.setCollectedNews(collectedNews);
            log.info("Coletadas {} notícias", collectedNews);
            
            // 2. Processamento de notícias pendentes
            log.info("Fase 2: Processando notícias pendentes...");
            int processedNews = processPendingNews();
            result.setProcessedNews(processedNews);
            log.info("Processadas {} notícias", processedNews);
            
            // 3. Retry de notícias que falharam
            log.info("Fase 3: Tentando reprocessar notícias que falharam...");
            int retriedNews = retryFailedNews();
            result.setRetriedNews(retriedNews);
            log.info("Reprocessadas {} notícias", retriedNews);
            
            // 4. Limpeza de dados antigos
            log.info("Fase 4: Limpando dados antigos...");
            int cleanedNews = newsDataService.cleanupOldNews(7); // 7 dias
            result.setCleanedNews(cleanedNews);
            log.info("Removidas {} notícias antigas", cleanedNews);
            
            // Calcular tempo de execução
            long executionTime = System.currentTimeMillis() - startTime;
            result.setExecutionTimeMs(executionTime);
            
            log.info("=== PIPELINE CONCLUÍDO EM {}ms ===", executionTime);
            log.info("Resultado: {} coletadas, {} processadas, {} reprocessadas, {} removidas", 
                collectedNews, processedNews, retriedNews, cleanedNews);
            
            return result;
            
        } catch (Exception e) {
            log.error("Erro durante execução do pipeline: {}", e.getMessage(), e);
            result.setError(e.getMessage());
            return result;
        }
    }
    
    /**
     * Processa notícias pendentes de análise.
     * 
     * @return Número de notícias processadas
     */
    public int processPendingNews() {
        log.info("Buscando notícias pendentes de processamento...");
        
        List<NewsItem> pendingNews = newsDataService.getPendingNews();
        log.info("Encontradas {} notícias pendentes", pendingNews.size());
        
        int processedCount = 0;
        
        for (NewsItem newsItem : pendingNews) {
            try {
                // Atualizar status para PROCESSING
                newsDataService.updateNewsStatus(newsItem, ProcessingStatus.PROCESSING);
                
                // Analisar com IA
                var analysisResult = geminiService.analyzeNews(newsItem);
                
                if (analysisResult.isSuccessful()) {
                    // Salvar resultado da análise
                    geminiService.saveAnalysisResult(newsItem, analysisResult);
                    
                    // Atualizar status para COMPLETED
                    newsDataService.updateNewsStatus(newsItem, ProcessingStatus.COMPLETED);
                    
                    processedCount++;
                    log.debug("Notícia {} processada com sucesso - Relevante: {}, Categoria: {}", 
                        newsItem.getId(), analysisResult.isRelevant(), analysisResult.getCategoria());
                    
                } else {
                    // Marcar como falha
                    newsDataService.updateNewsStatus(newsItem, ProcessingStatus.FAILED);
                    log.warn("Falha ao processar notícia {}: {}", newsItem.getId(), analysisResult.getMotivo());
                }
                
                // Pequena pausa entre processamentos para respeitar rate limits
                Thread.sleep(2000);
                
            } catch (Exception e) {
                log.error("Erro ao processar notícia {}: {}", newsItem.getId(), e.getMessage());
                newsDataService.updateNewsStatus(newsItem, ProcessingStatus.FAILED);
            }
        }
        
        log.info("Processamento concluído: {} notícias processadas", processedCount);
        return processedCount;
    }
    
    /**
     * Tenta reprocessar notícias que falharam anteriormente.
     * 
     * @return Número de notícias reprocessadas
     */
    public int retryFailedNews() {
        log.info("Buscando notícias que falharam para reprocessamento...");
        
        List<NewsItem> failedNews = newsDataService.getFailedNews();
        log.info("Encontradas {} notícias que falharam", failedNews.size());
        
        int retriedCount = 0;
        
        for (NewsItem newsItem : failedNews) {
            try {
                // Atualizar status para RETRYING
                newsDataService.updateNewsStatus(newsItem, ProcessingStatus.RETRYING);
                
                // Analisar com IA
                var analysisResult = geminiService.analyzeNews(newsItem);
                
                if (analysisResult.isSuccessful()) {
                    // Salvar resultado da análise
                    geminiService.saveAnalysisResult(newsItem, analysisResult);
                    
                    // Atualizar status para COMPLETED
                    newsDataService.updateNewsStatus(newsItem, ProcessingStatus.COMPLETED);
                    
                    retriedCount++;
                    log.info("Notícia {} reprocessada com sucesso - Relevante: {}, Categoria: {}", 
                        newsItem.getId(), analysisResult.isRelevant(), analysisResult.getCategoria());
                    
                } else {
                    // Manter como falha
                    newsDataService.updateNewsStatus(newsItem, ProcessingStatus.FAILED);
                    log.warn("Falha ao reprocessar notícia {}: {}", newsItem.getId(), analysisResult.getMotivo());
                }
                
                // Pequena pausa entre processamentos
                Thread.sleep(2000);
                
            } catch (Exception e) {
                log.error("Erro ao reprocessar notícia {}: {}", newsItem.getId(), e.getMessage());
                newsDataService.updateNewsStatus(newsItem, ProcessingStatus.FAILED);
            }
        }
        
        log.info("Reprocessamento concluído: {} notícias reprocessadas", retriedCount);
        return retriedCount;
    }
    
    /**
     * Executa apenas a coleta de notícias (sem análise).
     * 
     * @return Número de notícias coletadas
     */
    public int collectNewsOnly() {
        log.info("Executando apenas coleta de notícias...");
        return newsDataService.collectAllNews();
    }
    
    /**
     * Executa apenas o processamento de notícias pendentes.
     * 
     * @return Número de notícias processadas
     */
    public int processNewsOnly() {
        log.info("Executando apenas processamento de notícias...");
        return processPendingNews();
    }
    
    /**
     * Retorna estatísticas completas do sistema.
     * 
     * @return String com estatísticas
     */
    public String getSystemStats() {
        StringBuilder stats = new StringBuilder();
        stats.append("=== ESTATÍSTICAS DO SISTEMA ===\n\n");
        
        // Estatísticas de coleta
        stats.append(newsDataService.getCollectionStats()).append("\n\n");
        
        // Estatísticas de análise
        stats.append(geminiService.getAnalysisStats()).append("\n\n");
        
        // Estatísticas gerais
        long totalNews = newsItemRepository.count();
        long pendingNews = newsItemRepository.countByStatus(ProcessingStatus.PENDING);
        long processingNews = newsItemRepository.countByStatus(ProcessingStatus.PROCESSING);
        long completedNews = newsItemRepository.countByStatus(ProcessingStatus.COMPLETED);
        long failedNews = newsItemRepository.countByStatus(ProcessingStatus.FAILED);
        
        stats.append("=== RESUMO GERAL ===\n");
        stats.append(String.format("Total de notícias: %d\n", totalNews));
        stats.append(String.format("Pendentes: %d\n", pendingNews));
        stats.append(String.format("Processando: %d\n", processingNews));
        stats.append(String.format("Concluídas: %d\n", completedNews));
        stats.append(String.format("Falharam: %d\n", failedNews));
        
        return stats.toString();
    }
    
    /**
     * Busca todos os insights processados com paginação.
     * 
     * @param pageable Configuração de paginação
     * @return Página de insights processados
     */
    public Page<ProcessedNewsItem> getAllInsights(Pageable pageable) {
        log.debug("Buscando todos os insights processados");
        return processedNewsItemRepository.findAll(pageable);
    }
    
    /**
     * Busca insights por categoria.
     * 
     * @param category Categoria desejada
     * @param pageable Configuração de paginação
     * @return Página de insights da categoria
     */
    public Page<ProcessedNewsItem> getInsightsByCategory(Category category, Pageable pageable) {
        log.debug("Buscando insights da categoria: {}", category);
        return processedNewsItemRepository.findByCategory(category, pageable);
    }
    
    /**
     * Busca insights por relevância.
     * 
     * @param isRelevant Se deve buscar apenas relevantes
     * @param pageable Configuração de paginação
     * @return Página de insights filtrados por relevância
     */
    public Page<ProcessedNewsItem> getInsightsByRelevance(Boolean isRelevant, Pageable pageable) {
        log.debug("Buscando insights por relevância: {}", isRelevant);
        
        if (isRelevant) {
            return processedNewsItemRepository.findByIsRelevantTrue(pageable);
        } else {
            return processedNewsItemRepository.findByIsRelevantFalse(pageable);
        }
    }
    
    /**
     * Busca insights por categoria e relevância.
     * 
     * @param category Categoria desejada
     * @param isRelevant Se deve buscar apenas relevantes
     * @param pageable Configuração de paginação
     * @return Página de insights filtrados
     */
    public Page<ProcessedNewsItem> getInsightsByCategoryAndRelevance(Category category, Boolean isRelevant, Pageable pageable) {
        log.debug("Buscando insights da categoria {} e relevância {}", category, isRelevant);
        
        if (isRelevant) {
            return processedNewsItemRepository.findByIsRelevantTrueAndCategory(category, pageable);
        } else {
            // Para não relevantes, precisamos fazer uma consulta customizada
            return processedNewsItemRepository.findByCategoryAndIsRelevantFalse(category, pageable);
        }
    }
    
    /**
     * Gera resumo diário de insights processados.
     * 
     * @param startDate Data inicial
     * @param endDate Data final
     * @return Resumo diário
     */
    public Map<String, Object> getDailySummary(LocalDateTime startDate, LocalDateTime endDate) {
        log.info("Gerando resumo diário de {} até {}", startDate, endDate);
        
        Map<String, Object> summary = new HashMap<>();
        
        try {
            // Buscar insights do período
            List<ProcessedNewsItem> dailyInsights = processedNewsItemRepository
                .findByProcessedAtBetween(startDate, endDate);
            
            // Estatísticas gerais
            long totalInsights = dailyInsights.size();
            long relevantInsights = dailyInsights.stream()
                .mapToLong(item -> Boolean.TRUE.equals(item.getIsRelevant()) ? 1 : 0)
                .sum();
            
            // Estatísticas por categoria
            Map<String, Long> categoryStats = new HashMap<>();
            for (Category category : Category.values()) {
                long count = dailyInsights.stream()
                    .mapToLong(item -> item.getCategory() == category ? 1 : 0)
                    .sum();
                categoryStats.put(category.getDisplayName(), count);
            }
            
            // Insights com alta confiança
            long highConfidenceInsights = dailyInsights.stream()
                .mapToLong(item -> item.hasHighConfidence() ? 1 : 0)
                .sum();
            
            // Montar resumo
            summary.put("periodo", Map.of(
                "inicio", startDate.toString(),
                "fim", endDate.toString()
            ));
            summary.put("estatisticas", Map.of(
                "totalInsights", totalInsights,
                "insightsRelevantes", relevantInsights,
                "percentualRelevantes", totalInsights > 0 ? (relevantInsights * 100.0 / totalInsights) : 0,
                "insightsAltaConfianca", highConfidenceInsights
            ));
            summary.put("categorias", categoryStats);
            summary.put("insights", dailyInsights.stream()
                .filter(item -> Boolean.TRUE.equals(item.getIsRelevant()))
                .limit(10) // Top 10 insights relevantes
                .map(this::createInsightSummary)
                .toList());
            
            log.info("Resumo diário gerado: {} insights, {} relevantes", totalInsights, relevantInsights);
            return summary;
            
        } catch (Exception e) {
            log.error("Erro ao gerar resumo diário: {}", e.getMessage(), e);
            summary.put("erro", "Erro ao gerar resumo: " + e.getMessage());
            return summary;
        }
    }
    
    /**
     * Cria um resumo de um insight para o relatório diário.
     * 
     * @param insight Insight processado
     * @return Resumo do insight
     */
    private Map<String, Object> createInsightSummary(ProcessedNewsItem insight) {
        Map<String, Object> summary = new HashMap<>();
        
        summary.put("id", insight.getId());
        summary.put("categoria", insight.getCategory().getDisplayName());
        summary.put("confianca", insight.getConfidenceLevel());
        summary.put("resumoExecutivo", insight.getExecutiveSummary());
        summary.put("acaoSugerida", insight.getSuggestedAction());
        summary.put("processadoEm", insight.getProcessedAt().toString());
        
        // Informações da notícia original
        if (insight.getNewsItem() != null) {
            summary.put("titulo", insight.getNewsItem().getTitle());
            summary.put("fonte", insight.getNewsItem().getSource());
            summary.put("url", insight.getNewsItem().getUrl());
        }
        
        return summary;
    }
    
    /**
     * Classe para representar o resultado da execução do pipeline.
     */
    public static class PipelineExecutionResult {
        private int collectedNews = 0;
        private int processedNews = 0;
        private int retriedNews = 0;
        private int cleanedNews = 0;
        private long executionTimeMs = 0;
        private String error = null;
        
        // Getters e Setters
        public int getCollectedNews() { return collectedNews; }
        public void setCollectedNews(int collectedNews) { this.collectedNews = collectedNews; }
        
        public int getProcessedNews() { return processedNews; }
        public void setProcessedNews(int processedNews) { this.processedNews = processedNews; }
        
        public int getRetriedNews() { return retriedNews; }
        public void setRetriedNews(int retriedNews) { this.retriedNews = retriedNews; }
        
        public int getCleanedNews() { return cleanedNews; }
        public void setCleanedNews(int cleanedNews) { this.cleanedNews = cleanedNews; }
        
        public long getExecutionTimeMs() { return executionTimeMs; }
        public void setExecutionTimeMs(long executionTimeMs) { this.executionTimeMs = executionTimeMs; }
        
        public String getError() { return error; }
        public void setError(String error) { this.error = error; }
        
        public boolean isSuccess() { return error == null; }
        
        @Override
        public String toString() {
            return String.format(
                "PipelineExecutionResult{collected=%d, processed=%d, retried=%d, cleaned=%d, time=%dms, success=%s}",
                collectedNews, processedNews, retriedNews, cleanedNews, executionTimeMs, isSuccess()
            );
        }
    }
}
