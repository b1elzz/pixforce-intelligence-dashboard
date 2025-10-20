package com.pixforce.insights_search.controller;

import com.pixforce.insights_search.entity.Category;
import com.pixforce.insights_search.entity.ProcessedNewsItem;
import com.pixforce.insights_search.service.PipelineService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.util.List;

/**
 * ===========================================
 * CONTROLLER INSIGHTS - API REST PRINCIPAL
 * ===========================================
 * 
 * Controller principal para consulta de insights processados pela IA.
 * Expõe endpoints REST para o frontend consumir os dados de inteligência de mercado.
 * 
 * ENDPOINTS DISPONÍVEIS:
 * - GET /api/insights - Listar insights com paginação e filtros
 * - GET /api/insights/relevantes - Apenas insights relevantes
 * - GET /api/insights/categoria/{categoria} - Filtrar por categoria
 * - GET /api/insights/summary/daily - Resumo diário de insights
 * - POST /api/insights/collect - Executar coleta manual
 * - GET /api/insights/stats - Estatísticas do sistema
 * 
 * FILTROS SUPORTADOS:
 * - categoria: PRODUTO, PARCERIA, ESTRATEGIA
 * - relevante: true/false
 * - page: número da página (padrão: 0)
 * - size: tamanho da página (padrão: 20)
 * - sort: campo para ordenação (padrão: processedAt,desc)
 * 
 * @author Equipe PixForce
 * @version 1.0.0
 * @since 2025-01-27
 */
@RestController
@RequestMapping("/api/insights")
@RequiredArgsConstructor
@Slf4j
public class InsightsController {
    
    private final PipelineService pipelineService;
    
    /**
     * Lista insights processados com paginação e filtros opcionais.
     * 
     * @param categoria Filtro por categoria (opcional)
     * @param relevante Filtro por relevância (opcional)
     * @param page Número da página (padrão: 0)
     * @param size Tamanho da página (padrão: 20)
     * @param sort Campo para ordenação (padrão: processedAt,desc)
     * @return Página de insights processados
     */
    @GetMapping
    public ResponseEntity<?> getInsights(
            @RequestParam(required = false) Category categoria,
            @RequestParam(required = false) Boolean relevante,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "20") int size,
            @RequestParam(defaultValue = "processedAt,desc") String sort) {
        
        log.info("Consultando insights - Categoria: {}, Relevante: {}, Página: {}, Tamanho: {}", 
            categoria, relevante, page, size);
        
        try {
            // Criar objeto de paginação
            Sort sortObj = Sort.by(Sort.Direction.DESC, "processedAt");
            if (sort != null && !sort.isEmpty()) {
                String[] sortParts = sort.split(",");
                if (sortParts.length == 2) {
                    Sort.Direction direction = "desc".equalsIgnoreCase(sortParts[1]) ? 
                        Sort.Direction.DESC : Sort.Direction.ASC;
                    sortObj = Sort.by(direction, sortParts[0]);
                }
            }
            
            Pageable pageable = PageRequest.of(page, size, sortObj);
            
            // Buscar insights baseado nos filtros
            Page<ProcessedNewsItem> insights;
            
            if (categoria != null && relevante != null) {
                // Filtro por categoria E relevância
                insights = pipelineService.getInsightsByCategoryAndRelevance(categoria, relevante, pageable);
            } else if (categoria != null) {
                // Filtro apenas por categoria
                insights = pipelineService.getInsightsByCategory(categoria, pageable);
            } else if (relevante != null) {
                // Filtro apenas por relevância
                insights = pipelineService.getInsightsByRelevance(relevante, pageable);
            } else {
                // Sem filtros
                insights = pipelineService.getAllInsights(pageable);
            }
            
            log.info("Retornando {} insights da página {} de {}", 
                insights.getContent().size(), page, insights.getTotalPages());
            
            return ResponseEntity.ok(insights);
            
        } catch (Exception e) {
            log.error("Erro ao consultar insights: {}", e.getMessage(), e);
            return ResponseEntity.internalServerError()
                .body("Erro interno do servidor: " + e.getMessage());
        }
    }
    
    /**
     * Lista apenas insights relevantes (relevante = true).
     * 
     * @param page Número da página
     * @param size Tamanho da página
     * @return Página de insights relevantes
     */
    @GetMapping("/relevantes")
    public ResponseEntity<?> getRelevantInsights(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "20") int size) {
        
        log.info("Consultando insights relevantes - Página: {}, Tamanho: {}", page, size);
        
        try {
            Pageable pageable = PageRequest.of(page, size, Sort.by(Sort.Direction.DESC, "processedAt"));
            Page<ProcessedNewsItem> insights = pipelineService.getInsightsByRelevance(true, pageable);
            
            log.info("Retornando {} insights relevantes", insights.getContent().size());
            return ResponseEntity.ok(insights);
            
        } catch (Exception e) {
            log.error("Erro ao consultar insights relevantes: {}", e.getMessage(), e);
            return ResponseEntity.internalServerError()
                .body("Erro interno do servidor: " + e.getMessage());
        }
    }
    
    /**
     * Lista insights por categoria específica.
     * 
     * @param categoria Categoria desejada
     * @param page Número da página
     * @param size Tamanho da página
     * @return Página de insights da categoria
     */
    @GetMapping("/categoria/{categoria}")
    public ResponseEntity<?> getInsightsByCategory(
            @PathVariable Category categoria,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "20") int size) {
        
        log.info("Consultando insights da categoria: {} - Página: {}, Tamanho: {}", 
            categoria, page, size);
        
        try {
            Pageable pageable = PageRequest.of(page, size, Sort.by(Sort.Direction.DESC, "processedAt"));
            Page<ProcessedNewsItem> insights = pipelineService.getInsightsByCategory(categoria, pageable);
            
            log.info("Retornando {} insights da categoria {}", insights.getContent().size(), categoria);
            return ResponseEntity.ok(insights);
            
        } catch (Exception e) {
            log.error("Erro ao consultar insights da categoria {}: {}", categoria, e.getMessage(), e);
            return ResponseEntity.internalServerError()
                .body("Erro interno do servidor: " + e.getMessage());
        }
    }
    
    /**
     * Retorna resumo diário de insights processados hoje.
     * 
     * @return Resumo diário com estatísticas
     */
    @GetMapping("/summary/daily")
    public ResponseEntity<?> getDailySummary() {
        log.info("Gerando resumo diário de insights");
        
        try {
            LocalDateTime startOfDay = LocalDateTime.now().withHour(0).withMinute(0).withSecond(0);
            LocalDateTime endOfDay = LocalDateTime.now().withHour(23).withMinute(59).withSecond(59);
            
            var summary = pipelineService.getDailySummary(startOfDay, endOfDay);
            
            log.info("Resumo diário gerado com sucesso");
            return ResponseEntity.ok(summary);
            
        } catch (Exception e) {
            log.error("Erro ao gerar resumo diário: {}", e.getMessage(), e);
            return ResponseEntity.internalServerError()
                .body("Erro interno do servidor: " + e.getMessage());
        }
    }
    
    /**
     * Executa coleta manual de notícias.
     * 
     * @return Resultado da coleta
     */
    @PostMapping("/collect")
    public ResponseEntity<?> collectNews() {
        log.info("Iniciando coleta manual de notícias");
        
        try {
            int collectedNews = pipelineService.collectNewsOnly();
            
            log.info("Coleta manual concluída: {} notícias coletadas", collectedNews);
            return ResponseEntity.ok("Coleta concluída: " + collectedNews + " notícias coletadas");
            
        } catch (Exception e) {
            log.error("Erro na coleta manual: {}", e.getMessage(), e);
            return ResponseEntity.internalServerError()
                .body("Erro na coleta: " + e.getMessage());
        }
    }
    
    /**
     * Executa pipeline completo (coleta + processamento).
     * 
     * @return Resultado da execução do pipeline
     */
    @PostMapping("/pipeline")
    public ResponseEntity<?> executePipeline() {
        log.info("Iniciando execução completa do pipeline");
        
        try {
            var result = pipelineService.executePipeline();
            
            log.info("Pipeline executado: {}", result);
            return ResponseEntity.ok(result);
            
        } catch (Exception e) {
            log.error("Erro na execução do pipeline: {}", e.getMessage(), e);
            return ResponseEntity.internalServerError()
                .body("Erro no pipeline: " + e.getMessage());
        }
    }
    
    /**
     * Retorna estatísticas gerais do sistema.
     * 
     * @return Estatísticas do sistema
     */
    @GetMapping("/stats")
    public ResponseEntity<?> getSystemStats() {
        log.info("Consultando estatísticas do sistema");
        
        try {
            String stats = pipelineService.getSystemStats();
            
            log.info("Estatísticas consultadas com sucesso");
            return ResponseEntity.ok(stats);
            
        } catch (Exception e) {
            log.error("Erro ao consultar estatísticas: {}", e.getMessage(), e);
            return ResponseEntity.internalServerError()
                .body("Erro interno do servidor: " + e.getMessage());
        }
    }
}
