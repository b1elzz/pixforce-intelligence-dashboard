// Localização: src/main/java/com/pixforce/insights_search/controller/GeminiTestController.java
package com.pixforce.insights_search.controller;

import lombok.Data;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.reactive.function.client.WebClient;
import org.springframework.web.util.UriComponentsBuilder;

import java.util.Collections;
import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/test-gemini")
public class GeminiTestController {

    private static final Logger log = LoggerFactory.getLogger(GeminiTestController.class);

    private final WebClient webClient;
    private final String newsApiBaseUrl;
    private final String newsApiKey;
    private final String geminiApiBaseUrl;
    private final String geminiApiKey;
    private final String geminiApiVersion;
    private final String geminiModel;

    @Autowired
    public GeminiTestController(
            WebClient webClient,
            @Value("${newsdata.api.base-url}") String newsApiBaseUrl,
            @Value("${newsdata.api.key}") String newsApiKey,
            @Value("${gemini.api.base-url}") String geminiApiBaseUrl,
            @Value("${gemini.api.key}") String geminiApiKey,
            @Value("${gemini.api.version}") String geminiApiVersion,
            @Value("${gemini.api.model}") String geminiModel) {
        this.webClient = webClient;
        this.newsApiBaseUrl = newsApiBaseUrl;
        this.newsApiKey = newsApiKey;
        this.geminiApiBaseUrl = geminiApiBaseUrl;
        this.geminiApiKey = geminiApiKey;
        this.geminiApiVersion = geminiApiVersion;
        this.geminiModel = geminiModel;
    }

    @GetMapping("/process-news")
    public ResponseEntity<?> processNews(@RequestParam(defaultValue = "brasil") String topic) {
        log.info("Pipeline completo iniciado! Buscando notícias sobre '{}'.", topic);
        try {
            String newsApiUrl = UriComponentsBuilder.fromUriString(newsApiBaseUrl)
                    .queryParam("apikey", newsApiKey)
                    .queryParam("q", topic)
                    .queryParam("language", "pt")
                    .toUriString();

            NewsDataResponse newsResponse = webClient.get().uri(newsApiUrl).retrieve().bodyToMono(NewsDataResponse.class).block();
            List<NewsDataArticle> allArticles = Optional.ofNullable(newsResponse)
                    .map(NewsDataResponse::getResults)
                    .orElse(Collections.emptyList());

            if (allArticles.isEmpty()) {
                return ResponseEntity.status(404).body("Nenhuma notícia encontrada para o tópico: " + topic);
            }
            log.info("{} notícias recebidas no total. Iniciando processamento.", allArticles.size());

            List<ProcessedArticleResponse> summarizedArticles = allArticles.stream()
                    .map(this::summarizeArticle)
                    .collect(Collectors.toList());

            log.info("Processamento concluído. Retornando {} resumos.", summarizedArticles.size());
            return ResponseEntity.ok(summarizedArticles);
        } catch (Exception e) {
            log.error("Falha geral no pipeline: {}", e.getMessage(), e);
            return ResponseEntity.internalServerError().body("Ocorreu um erro no processo: " + e.getMessage());
        }
    }

    private ProcessedArticleResponse summarizeArticle(NewsDataArticle article) {
        try {
            log.info("Criando resumo para: '{}'", article.getTitle());
            String prompt = String.format("Aja como um redator de notícias criativo. Sua tarefa é criar uma chamada fictícia e impactante de uma única frase para uma notícia com o seguinte título: '%s'. IMPORTANTE: Sua resposta deve conter APENAS o texto da chamada, sem introduções, aspas ou qualquer outra palavra.", article.getTitle());
            GeminiRequest geminiRequest = new GeminiRequest(Collections.singletonList(new GeminiContent(Collections.singletonList(new GeminiPart(prompt)))));
            String geminiUrl = UriComponentsBuilder.fromUriString(geminiApiBaseUrl).pathSegment(geminiApiVersion, "models", geminiModel + ":generateContent").queryParam("key", geminiApiKey).toUriString();
            GeminiResponse geminiResponse = webClient.post().uri(geminiUrl).contentType(MediaType.APPLICATION_JSON).bodyValue(geminiRequest).retrieve().bodyToMono(GeminiResponse.class).block();
            String summary = Optional.ofNullable(geminiResponse).map(GeminiResponse::getCandidates).flatMap(candidates -> candidates.stream().findFirst()).map(GeminiCandidate::getContent).map(GeminiContent::getParts).flatMap(parts -> parts.stream().findFirst()).map(GeminiPart::getText).orElse("Não foi possível gerar um resumo.");
            return new ProcessedArticleResponse(article.getTitle(), summary.trim());
        } catch (Exception e) {
            log.error("Falha ao resumir o artigo '{}': {}", article.getTitle(), e.getMessage());
            return new ProcessedArticleResponse(article.getTitle(), "Falha ao gerar resumo para este artigo.");
        }
    }

    private record ProcessedArticleResponse(String tituloOriginal, String resumoGemini) {}
    @Data private static class NewsDataResponse { private List<NewsDataArticle> results; }
    @Data private static class NewsDataArticle { private String title; private String description; private String content; private String source_id; }
    @Data private static class GeminiRequest { private final List<GeminiContent> contents; }
    @Data private static class GeminiContent { private final List<GeminiPart> parts; }
    @Data private static class GeminiPart { private final String text; }
    @Data private static class GeminiResponse { private List<GeminiCandidate> candidates; }
    @Data private static class GeminiCandidate { private GeminiContent content; }
}