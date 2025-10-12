package com.pixforce.insights_search.service;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.pixforce.insights_search.config.GeminiConfig;
import com.pixforce.insights_search.dto.AIAnalysisResult;
import com.pixforce.insights_search.dto.GeminiResponse;
import com.pixforce.insights_search.entity.Category;
import com.pixforce.insights_search.entity.NewsItem;
import com.pixforce.insights_search.entity.ProcessedNewsItem;
import com.pixforce.insights_search.repository.ProcessedNewsItemRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;
import org.springframework.web.reactive.function.client.WebClientResponseException;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.Map;

/**
 * ===========================================
 * SERVIÇO GEMINI - ANÁLISE DE IA
 * ===========================================
 * 
 * Serviço responsável por analisar notícias usando a API Gemini Pro.
 * Implementa a lógica de análise de relevância, categorização e geração de insights.
 * 
 * FUNCIONALIDADES:
 * - Análise de relevância para a PixForce
 * - Categorização automática (Produto, Parceria, Estratégia)
 * - Geração de ações sugeridas
 * - Score de confiança da análise
 * - Resumo executivo
 * 
 * PROMPT UTILIZADO:
 * O serviço envia um prompt estruturado para o Gemini Pro analisar
 * cada notícia e retornar um JSON com as informações processadas.
 * 
 * @author Equipe PixForce
 * @version 1.0.0
 * @since 2025-01-27
 */
@Service
@RequiredArgsConstructor
@Slf4j
public class GeminiService {
    
    private final GeminiConfig geminiConfig;
    private final ProcessedNewsItemRepository processedNewsItemRepository;
    private final WebClient webClient;
    private final ObjectMapper objectMapper;
    
    /**
     * Analisa uma notícia usando o Gemini Pro.
     * 
     * @param newsItem Notícia a ser analisada
     * @return Resultado da análise de IA
     */
    public AIAnalysisResult analyzeNews(NewsItem newsItem) {
        log.info("Analisando notícia: {} - {}", newsItem.getId(), 
            newsItem.getTitle() != null && newsItem.getTitle().length() > 50 ? 
            newsItem.getTitle().substring(0, 50) + "..." : newsItem.getTitle());
        
        try {
            // Verificar se a configuração está válida
            if (!geminiConfig.isValid()) {
                log.error("Configuração do Gemini Pro inválida. Verifique a chave da API.");
                return createErrorResult("Configuração inválida");
            }
            
            // Construir prompt para análise
            String prompt = buildAnalysisPrompt(newsItem);
            log.debug("Prompt construído para análise da notícia {}", newsItem.getId());
            
            // Fazer requisição para o Gemini Pro
            GeminiResponse response = callGeminiAPI(prompt);
            
            if (response == null || !response.isSuccess()) {
                log.warn("Resposta inválida do Gemini Pro para notícia: {}", newsItem.getId());
                return createErrorResult("Resposta inválida da IA");
            }
            
            // Processar resposta da IA
            AIAnalysisResult result = parseAIResponse(response.getResponseText());
            
            if (result.isSuccessful()) {
                log.info("Análise concluída para notícia {} - Relevante: {}, Categoria: {}", 
                    newsItem.getId(), result.isRelevant(), result.getCategoria());
            } else {
                log.warn("Análise incompleta para notícia {} - {}", newsItem.getId(), result.getMotivo());
            }
            
            return result;
            
        } catch (WebClientResponseException e) {
            log.error("Erro HTTP ao analisar notícia {}: {} - {}", newsItem.getId(), e.getStatusCode(), e.getResponseBodyAsString());
            return createErrorResult("Erro HTTP: " + e.getStatusCode());
        } catch (Exception e) {
            log.error("Erro inesperado ao analisar notícia {}: {}", newsItem.getId(), e.getMessage());
            return createErrorResult("Erro inesperado: " + e.getMessage());
        }
    }
    
    /**
     * Salva o resultado da análise no banco de dados.
     * 
     * @param newsItem Notícia original
     * @param analysisResult Resultado da análise
     * @return Notícia processada salva
     */
    public ProcessedNewsItem saveAnalysisResult(NewsItem newsItem, AIAnalysisResult analysisResult) {
        log.info("Salvando resultado da análise para notícia: {}", newsItem.getId());
        
        try {
            // Criar entidade ProcessedNewsItem
            ProcessedNewsItem processedNews = ProcessedNewsItem.builder()
                .newsItem(newsItem)
                .isRelevant(analysisResult.getRelevante())
                .category(analysisResult.getCategoria())
                .relevanceReason(analysisResult.getMotivo())
                .suggestedAction(analysisResult.getAcaoSugerida())
                .confidenceScore(analysisResult.getScoreConfianca())
                .executiveSummary(analysisResult.getResumoExecutivo())
                .extractedKeywords(analysisResult.getPalavrasChave())
                .aiModel(geminiConfig.getModelName())
                .processedAt(LocalDateTime.now())
                .build();
            
            // Salvar no banco
            ProcessedNewsItem saved = processedNewsItemRepository.save(processedNews);
            
            log.info("Resultado da análise salvo: {} - Relevante: {}, Categoria: {}", 
                saved.getId(), saved.getIsRelevant(), saved.getCategory());
            
            return saved;
            
        } catch (Exception e) {
            log.error("Erro ao salvar resultado da análise para notícia {}: {}", newsItem.getId(), e.getMessage());
            throw new RuntimeException("Erro ao salvar análise", e);
        }
    }
    
    /**
     * Constrói o prompt para análise da notícia.
     * 
     * @param newsItem Notícia a ser analisada
     * @return Prompt formatado para o Gemini Pro
     */
    private String buildAnalysisPrompt(NewsItem newsItem) {
        return String.format("""
            Você é um analista de mercado especializado em Inteligência Artificial e Visão Computacional.
            Analise a notícia abaixo e retorne APENAS um JSON com as seguintes informações:
            
            {
              "relevante": true/false,
              "motivo": "explicação do motivo da relevância",
              "categoria": "PRODUTO/PARCERIA/ESTRATEGIA",
              "acaoSugerida": "ação recomendada para a PixForce",
              "scoreConfianca": 0.0-1.0,
              "resumoExecutivo": "resumo em 2-3 linhas",
              "palavrasChave": "palavras-chave extraídas, separadas por vírgula"
            }
            
            CRITÉRIOS DE RELEVÂNCIA:
            - Relacionada a IA, Visão Computacional, Machine Learning, Deep Learning
            - Novos produtos, tecnologias ou inovações
            - Oportunidades de parceria ou colaboração
            - Movimentos estratégicos do mercado
            - Concorrentes ou empresas do setor
            
            CATEGORIAS:
            - PRODUTO: Novos produtos, tecnologias, inovações
            - PARCERIA: Oportunidades de parceria, colaborações
            - ESTRATEGIA: Movimentos estratégicos, tendências, mudanças no mercado
            
            TÍTULO: %s
            DESCRIÇÃO: %s
            FONTE: %s
            URL: %s
            """, 
            newsItem.getTitle(),
            newsItem.getDescription(),
            newsItem.getSource(),
            newsItem.getUrl()
        );
    }
    
    /**
     * Faz a requisição para a API do Gemini Pro.
     * 
     * @param prompt Prompt para análise
     * @return Resposta do Gemini Pro
     */
    private GeminiResponse callGeminiAPI(String prompt) {
        String apiUrl = geminiConfig.getApiUrlWithKey();
        
        // Construir payload da requisição
        Map<String, Object> requestBody = new HashMap<>();
        Map<String, Object> content = new HashMap<>();
        Map<String, Object> part = new HashMap<>();
        
        part.put("text", prompt);
        content.put("parts", new Object[]{part});
        requestBody.put("contents", new Object[]{content});
        
        return webClient
            .post()
            .uri(apiUrl)
            .bodyValue(requestBody)
            .retrieve()
            .bodyToMono(GeminiResponse.class)
            .block();
    }
    
    /**
     * Processa a resposta JSON do Gemini Pro.
     * 
     * @param responseText Texto da resposta da IA
     * @return Resultado da análise processado
     */
    private AIAnalysisResult parseAIResponse(String responseText) {
        try {
            // Tentar extrair JSON da resposta
            String jsonText = extractJsonFromResponse(responseText);
            
            // Parsear JSON
            JsonNode jsonNode = objectMapper.readTree(jsonText);
            
            // Extrair informações
            Boolean relevante = jsonNode.has("relevante") ? jsonNode.get("relevante").asBoolean() : null;
            String motivo = jsonNode.has("motivo") ? jsonNode.get("motivo").asText() : null;
            String categoriaStr = jsonNode.has("categoria") ? jsonNode.get("categoria").asText() : null;
            String acaoSugerida = jsonNode.has("acaoSugerida") ? jsonNode.get("acaoSugerida").asText() : null;
            Double scoreConfianca = jsonNode.has("scoreConfianca") ? jsonNode.get("scoreConfianca").asDouble() : null;
            String resumoExecutivo = jsonNode.has("resumoExecutivo") ? jsonNode.get("resumoExecutivo").asText() : null;
            String palavrasChave = jsonNode.has("palavrasChave") ? jsonNode.get("palavrasChave").asText() : null;
            
            // Converter categoria
            Category categoria = null;
            if (categoriaStr != null) {
                try {
                    categoria = Category.valueOf(categoriaStr.toUpperCase());
                } catch (IllegalArgumentException e) {
                    log.warn("Categoria inválida recebida da IA: {}", categoriaStr);
                }
            }
            
            return AIAnalysisResult.builder()
                .relevante(relevante)
                .motivo(motivo)
                .categoria(categoria)
                .acaoSugerida(acaoSugerida)
                .scoreConfianca(scoreConfianca)
                .resumoExecutivo(resumoExecutivo)
                .palavrasChave(palavrasChave)
                .build();
                
        } catch (JsonProcessingException e) {
            log.error("Erro ao processar resposta JSON da IA: {}", e.getMessage());
            return createErrorResult("Erro ao processar resposta da IA");
        } catch (Exception e) {
            log.error("Erro inesperado ao processar resposta da IA: {}", e.getMessage());
            return createErrorResult("Erro inesperado ao processar resposta");
        }
    }
    
    /**
     * Extrai JSON da resposta da IA.
     * 
     * @param responseText Texto completo da resposta
     * @return JSON extraído
     */
    private String extractJsonFromResponse(String responseText) {
        if (responseText == null || responseText.trim().isEmpty()) {
            throw new IllegalArgumentException("Resposta vazia da IA");
        }
        
        // Procurar por JSON na resposta
        int jsonStart = responseText.indexOf("{");
        int jsonEnd = responseText.lastIndexOf("}");
        
        if (jsonStart == -1 || jsonEnd == -1 || jsonStart >= jsonEnd) {
            throw new IllegalArgumentException("JSON não encontrado na resposta da IA");
        }
        
        return responseText.substring(jsonStart, jsonEnd + 1);
    }
    
    /**
     * Cria um resultado de erro.
     * 
     * @param errorMessage Mensagem de erro
     * @return Resultado de erro
     */
    private AIAnalysisResult createErrorResult(String errorMessage) {
        return AIAnalysisResult.builder()
            .relevante(false)
            .motivo("Erro na análise: " + errorMessage)
            .categoria(null)
            .acaoSugerida("Verificar configuração da IA")
            .scoreConfianca(0.0)
            .resumoExecutivo("Erro na análise")
            .palavrasChave("")
            .build();
    }
    
    /**
     * Retorna estatísticas de análise.
     * 
     * @return String com estatísticas
     */
    public String getAnalysisStats() {
        long total = processedNewsItemRepository.count();
        long relevant = processedNewsItemRepository.countByIsRelevantTrue();
        long produto = processedNewsItemRepository.countByCategory(Category.PRODUTO);
        long parceria = processedNewsItemRepository.countByCategory(Category.PARCERIA);
        long estrategia = processedNewsItemRepository.countByCategory(Category.ESTRATEGIA);
        
        return String.format(
            "Estatísticas de Análise:\n" +
            "- Total analisadas: %d\n" +
            "- Relevantes: %d (%.1f%%)\n" +
            "- Produto: %d\n" +
            "- Parceria: %d\n" +
            "- Estratégia: %d",
            total, relevant, total > 0 ? (relevant * 100.0 / total) : 0, produto, parceria, estrategia
        );
    }
}
