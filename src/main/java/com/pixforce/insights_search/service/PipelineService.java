package com.pixforce.insights_search.service;

import com.pixforce.insights_search.entity.NewsItem;
import com.pixforce.insights_search.entity.ProcessingStatus;
import com.pixforce.insights_search.repository.NewsItemRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.List;

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
