package com.pixforce.insights_search;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableScheduling;

/**
 * ===========================================
 * PIXFORCE INSIGHTS SEARCH - APLICAÇÃO PRINCIPAL
 * ===========================================
 * 
 * Esta é a classe principal da aplicação PixForce Intelligence Dashboard.
 * 
 * FUNCIONALIDADES:
 * - Coleta automática de notícias via NewsData.io API
 * - Análise de IA usando Gemini Pro para classificar relevância
 * - Agendamento automático (coleta diária às 8h, limpeza às 3h)
 * - API REST para consulta de insights
 * - Armazenamento em PostgreSQL
 * 
 * CONFIGURAÇÕES IMPORTANTES:
 * - Porta: 8080 (configurável via application.properties)
 * - Context Path: /api
 * - Banco: PostgreSQL (pixforce_insights)
 * 
 * @author Equipe PixForce
 * @version 1.0.0
 * @since 2025-01-27
 */
@SpringBootApplication
@EnableScheduling // Habilita o agendamento automático de tarefas
public class InsightsSearchApplication {

    /**
     * Método principal que inicia a aplicação Spring Boot.
     * 
     * A aplicação irá:
     * 1. Conectar ao banco PostgreSQL
     * 2. Configurar os schedulers automáticos
     * 3. Inicializar os serviços de coleta e IA
     * 4. Expor as APIs REST
     * 
     * @param args Argumentos da linha de comando
     */
    public static void main(String[] args) {
        SpringApplication.run(InsightsSearchApplication.class, args);
    }

}
