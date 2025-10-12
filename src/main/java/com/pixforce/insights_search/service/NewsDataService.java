package com.pixforce.insights_search.service;

import com.pixforce.insights_search.config.NewsDataConfig;
import com.pixforce.insights_search.dto.NewsDataArticle;
import com.pixforce.insights_search.dto.NewsDataResponse;
import com.pixforce.insights_search.entity.NewsItem;
import com.pixforce.insights_search.entity.ProcessingStatus;
import com.pixforce.insights_search.repository.NewsItemRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;
import org.springframework.web.reactive.function.client.WebClientResponseException;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

/**
 * ===========================================
 * SERVIÇO NEWS DATA - COLETA DE NOTÍCIAS
 * ===========================================
 * 
 * Serviço responsável por coletar notícias da API NewsData.io.
 * Implementa a lógica de busca, filtragem e armazenamento de notícias.
 * 
 * FUNCIONALIDADES:
 * - Busca notícias por palavras-chave
 * - Filtragem de duplicatas
 * - Armazenamento no banco de dados
 * - Tratamento de erros e retry
 * 
 * PALAVRAS-CHAVE MONITORADAS:
 * - "inteligencia artificial"
 * - "visao computacional" 
 * - "machine learning"
 * - "deep learning"
 * - "computer vision"
 * - "IA"
 * - "automação"
 * 
 * @author Equipe PixForce
 * @version 1.0.0
 * @since 2025-01-27
 */
@Service
@RequiredArgsConstructor
@Slf4j
public class NewsDataService {
    
    private final NewsDataConfig newsDataConfig;
    private final NewsItemRepository newsItemRepository;
    private final WebClient webClient;
    
    /**
     * Palavras-chave para monitoramento do mercado de IA e Visão Computacional.
     */
    private static final String[] KEYWORDS = {
        "inteligencia artificial",
        "visao computacional", 
        "machine learning",
        "deep learning",
        "computer vision",
        "IA",
        "automação",
        "pixforce",
        "visão computacional"
    };
    
    /**
     * Coleta notícias de todas as palavras-chave monitoradas.
     * 
     * @return Número de notícias coletadas
     */
    public int collectAllNews() {
        log.info("Iniciando coleta de notícias para {} palavras-chave", KEYWORDS.length);
        
        int totalCollected = 0;
        
        for (String keyword : KEYWORDS) {
            try {
                int collected = collectNewsByKeyword(keyword);
                totalCollected += collected;
                log.info("Coletadas {} notícias para palavra-chave: {}", collected, keyword);
                
                // Pequena pausa entre requisições para respeitar rate limits
                Thread.sleep(1000);
                
            } catch (Exception e) {
                log.error("Erro ao coletar notícias para palavra-chave '{}': {}", keyword, e.getMessage());
            }
        }
        
        log.info("Coleta concluída. Total de notícias coletadas: {}", totalCollected);
        return totalCollected;
    }
    
    /**
     * Coleta notícias para uma palavra-chave específica.
     * 
     * @param keyword Palavra-chave para busca
     * @return Número de notícias coletadas
     */
    public int collectNewsByKeyword(String keyword) {
        log.info("Coletando notícias para palavra-chave: {}", keyword);
        
        try {
            // Verificar se a configuração está válida
            if (!newsDataConfig.isValid()) {
                log.error("Configuração do NewsData.io inválida. Verifique a chave da API.");
                return 0;
            }
            
            // Construir URL da API
            String apiUrl = newsDataConfig.getApiUrlWithQuery(keyword);
            log.debug("Fazendo requisição para: {}", apiUrl);
            
            // Fazer requisição para a API
            NewsDataResponse response = webClient
                .get()
                .uri(apiUrl)
                .retrieve()
                .bodyToMono(NewsDataResponse.class)
                .block();
            
            if (response == null || !response.isSuccess()) {
                log.warn("Resposta inválida da API NewsData.io para palavra-chave: {}", keyword);
                return 0;
            }
            
            if (!response.hasResults()) {
                log.info("Nenhuma notícia encontrada para palavra-chave: {}", keyword);
                return 0;
            }
            
            // Processar e salvar notícias
            List<NewsItem> savedNews = processAndSaveNews(response.getResults(), keyword);
            
            log.info("Salvas {} notícias para palavra-chave: {}", savedNews.size(), keyword);
            return savedNews.size();
            
        } catch (WebClientResponseException e) {
            log.error("Erro HTTP ao coletar notícias para '{}': {} - {}", keyword, e.getStatusCode(), e.getResponseBodyAsString());
            return 0;
        } catch (Exception e) {
            log.error("Erro inesperado ao coletar notícias para '{}': {}", keyword, e.getMessage());
            return 0;
        }
    }
    
    /**
     * Processa e salva notícias no banco de dados.
     * 
     * @param articles Lista de artigos da API
     * @param keyword Palavra-chave que gerou a busca
     * @return Lista de notícias salvas
     */
    private List<NewsItem> processAndSaveNews(List<NewsDataArticle> articles, String keyword) {
        List<NewsItem> savedNews = new ArrayList<>();
        
        for (NewsDataArticle article : articles) {
            try {
                // Verificar se a notícia é válida
                if (!article.isValid()) {
                    log.debug("Notícia inválida ignorada: {}", article.getTruncatedTitle(50));
                    continue;
                }
                
                // Verificar se já existe (evitar duplicatas)
                if (newsItemRepository.existsByUrl(article.getUrl())) {
                    log.debug("Notícia duplicada ignorada: {}", article.getTruncatedTitle(50));
                    continue;
                }
                
                // Criar entidade NewsItem
                NewsItem newsItem = NewsItem.builder()
                    .title(article.getTitle())
                    .description(article.getDescription())
                    .url(article.getUrl())
                    .imageUrl(article.getImage())
                    .source(article.getSource())
                    .author(article.getAuthor())
                    .publishedAt(article.getPublishedAt())
                    .content(article.getContent())
                    .keywords(keyword)
                    .language(article.getLanguage())
                    .country(article.getCountry())
                    .status(ProcessingStatus.PENDING)
                    .build();
                
                // Salvar no banco
                NewsItem saved = newsItemRepository.save(newsItem);
                savedNews.add(saved);
                
                log.debug("Notícia salva: {} - {}", saved.getId(), article.getTruncatedTitle(50));
                
            } catch (Exception e) {
                log.error("Erro ao processar notícia '{}': {}", article.getTruncatedTitle(50), e.getMessage());
            }
        }
        
        return savedNews;
    }
    
    /**
     * Busca notícias pendentes de processamento.
     * 
     * @return Lista de notícias pendentes
     */
    public List<NewsItem> getPendingNews() {
        return newsItemRepository.findPendingForProcessing();
    }
    
    /**
     * Busca notícias que falharam no processamento.
     * 
     * @return Lista de notícias que falharam
     */
    public List<NewsItem> getFailedNews() {
        return newsItemRepository.findFailedForRetry();
    }
    
    /**
     * Atualiza status de uma notícia.
     * 
     * @param newsItem Notícia a ser atualizada
     * @param status Novo status
     */
    public void updateNewsStatus(NewsItem newsItem, ProcessingStatus status) {
        newsItem.setStatus(status);
        newsItemRepository.save(newsItem);
        log.debug("Status da notícia {} atualizado para: {}", newsItem.getId(), status);
    }
    
    /**
     * Remove notícias antigas (limpeza automática).
     * 
     * @param daysAgo Número de dias para considerar como antigas
     * @return Número de notícias removidas
     */
    public int cleanupOldNews(int daysAgo) {
        LocalDateTime cutoffDate = LocalDateTime.now().minusDays(daysAgo);
        int removed = newsItemRepository.deleteByCreatedAtBefore(cutoffDate);
        log.info("Removidas {} notícias antigas (mais de {} dias)", removed, daysAgo);
        return removed;
    }
    
    /**
     * Retorna estatísticas de coleta.
     * 
     * @return String com estatísticas
     */
    public String getCollectionStats() {
        long total = newsItemRepository.count();
        long pending = newsItemRepository.countByStatus(ProcessingStatus.PENDING);
        long processing = newsItemRepository.countByStatus(ProcessingStatus.PROCESSING);
        long completed = newsItemRepository.countByStatus(ProcessingStatus.COMPLETED);
        long failed = newsItemRepository.countByStatus(ProcessingStatus.FAILED);
        
        return String.format(
            "Estatísticas de Coleta:\n" +
            "- Total: %d\n" +
            "- Pendentes: %d\n" +
            "- Processando: %d\n" +
            "- Concluídas: %d\n" +
            "- Falharam: %d",
            total, pending, processing, completed, failed
        );
    }
}
