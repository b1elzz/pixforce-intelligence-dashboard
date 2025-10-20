package com.pixforce.insights_search.scheduler;

import com.pixforce.insights_search.service.NewsDataService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

/**
 * Scheduler responsável pela limpeza automática de dados antigos.
 * Executa diariamente às 3h da manhã para manter o banco otimizado.
 */
@Component
@RequiredArgsConstructor
@Slf4j
public class DataCleanupScheduler {
    
    private final NewsDataService newsDataService;
    
    /**
     * Executa limpeza automática de dados antigos diariamente às 3h.
     */
    @Scheduled(cron = "0 0 3 * * *")
    public void cleanupOldData() {
        log.info("INICIANDO LIMPEZA AUTOMÁTICA DE DADOS");
        
        try {
            int removedOldNews = newsDataService.cleanupOldNews(30);
            log.info("Notícias antigas removidas (30+ dias): {}", removedOldNews);
            
            int removedFailedNews = newsDataService.cleanupFailedNews(7);
            log.info("Notícias com falha removidas (7+ dias): {}", removedFailedNews);
            
            int removedProcessedNews = newsDataService.cleanupOldProcessedNews(60);
            log.info("Notícias processadas antigas removidas (60+ dias): {}", removedProcessedNews);
            
            int totalRemoved = removedOldNews + removedFailedNews + removedProcessedNews;
            log.info("LIMPEZA AUTOMÁTICA CONCLUÍDA: {} registros removidos", totalRemoved);
            
        } catch (Exception e) {
            log.error("ERRO CRÍTICO na limpeza automática de dados: {}", e.getMessage(), e);
        }
    }
    
    /**
     * Executa limpeza leve de dados a cada 12 horas.
     */
    @Scheduled(cron = "0 0 0/12 * * *")
    public void cleanupFailedNews() {
        log.info("EXECUTANDO LIMPEZA LEVE DE DADOS FALHADOS");
        
        try {
            int removedFailedNews = newsDataService.cleanupFailedNews(3);
            
            if (removedFailedNews > 0) {
                log.info("LIMPEZA LEVE CONCLUÍDA: {} notícias com falha removidas", removedFailedNews);
            }
            
        } catch (Exception e) {
            log.error("ERRO na limpeza leve de dados: {}", e.getMessage(), e);
        }
    }
    
    /**
     * Executa verificação de saúde do sistema a cada hora.
     */
    @Scheduled(cron = "0 0 * * * *")
    public void healthCheck() {
        try {
            String stats = newsDataService.getCollectionStats();
            
            if (stats.contains("Falharam:") && !stats.contains("Falharam: 0")) {
                log.warn("ALERTA: Detectadas notícias com falha no processamento");
                log.warn("Status atual: {}", stats);
            }
            
        } catch (Exception e) {
            log.error("ERRO na verificação de saúde: {}", e.getMessage(), e);
        }
    }
}
