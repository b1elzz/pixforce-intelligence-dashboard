package com.pixforce.insights_search.scheduler;

import com.pixforce.insights_search.service.PipelineService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

/**
 * Scheduler responsável pela coleta automática de notícias.
 * Executa diariamente às 8h da manhã para coletar notícias frescas.
 */
@Component
@RequiredArgsConstructor
@Slf4j
public class NewsCollectionScheduler {
    
    private final PipelineService pipelineService;
    
    /**
     * Executa coleta automática de notícias diariamente às 8h.
     */
    @Scheduled(cron = "0 0 8 * * *")
    public void collectNewsDaily() {
        log.info("INICIANDO COLETA AUTOMÁTICA DE NOTÍCIAS");
        
        try {
            var result = pipelineService.executePipeline();
            
            if (result.isSuccess()) {
                log.info("COLETA AUTOMÁTICA CONCLUÍDA COM SUCESSO");
                log.info("Notícias coletadas: {}", result.getCollectedNews());
                log.info("Notícias processadas: {}", result.getProcessedNews());
                log.info("Tempo de execução: {}ms", result.getExecutionTimeMs());
            } else {
                log.error("FALHA NA COLETA AUTOMÁTICA: {}", result.getError());
            }
            
        } catch (Exception e) {
            log.error("ERRO CRÍTICO na coleta automática de notícias: {}", e.getMessage(), e);
        }
    }
    
    /**
     * Executa apenas coleta de notícias (sem processamento) a cada 6 horas.
     */
    @Scheduled(cron = "0 0 0/6 * * *")
    public void collectNewsEvery6Hours() {
        log.info("EXECUTANDO COLETA INTERMEDIÁRIA DE NOTÍCIAS");
        
        try {
            int collectedNews = pipelineService.collectNewsOnly();
            log.info("COLETA INTERMEDIÁRIA CONCLUÍDA: {} notícias coletadas", collectedNews);
            
        } catch (Exception e) {
            log.error("ERRO na coleta intermediária: {}", e.getMessage(), e);
        }
    }
    
    /**
     * Executa processamento de notícias pendentes a cada 2 horas.
     */
    @Scheduled(cron = "0 0 0/2 * * *")
    public void processPendingNews() {
        log.info("EXECUTANDO PROCESSAMENTO DE NOTÍCIAS PENDENTES");
        
        try {
            int processedNews = pipelineService.processNewsOnly();
            log.info("PROCESSAMENTO CONCLUÍDO: {} notícias processadas", processedNews);
            
        } catch (Exception e) {
            log.error("ERRO no processamento de notícias pendentes: {}", e.getMessage(), e);
        }
    }
}
