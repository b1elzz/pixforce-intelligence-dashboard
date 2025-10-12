package com.pixforce.insights_search.config;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * ===========================================
 * CONFIGURAÇÃO OBJECT MAPPER - JSON PROCESSING
 * ===========================================
 * 
 * Configuração do ObjectMapper para processamento de JSON.
 * Utilizado para serialização/deserialização de objetos Java.
 * 
 * CONFIGURAÇÕES:
 * - Suporte a LocalDateTime
 * - Deserialização de enums
 * - Formatação de datas
 * - Tratamento de propriedades desconhecidas
 * 
 * @author Equipe PixForce
 * @version 1.0.0
 * @since 2025-01-27
 */
@Configuration
public class ObjectMapperConfig {
    
    /**
     * Bean do ObjectMapper configurado para o projeto.
     * 
     * @return ObjectMapper configurado
     */
    @Bean
    public ObjectMapper objectMapper() {
        ObjectMapper mapper = new ObjectMapper();
        
        // Configurar módulo de tempo Java 8+
        mapper.registerModule(new JavaTimeModule());
        
        // Desabilitar falha em propriedades desconhecidas
        mapper.configure(com.fasterxml.jackson.databind.DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);
        
        // Configurar serialização de datas
        mapper.configure(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS, false);
        
        // Configurar indentação para logs
        mapper.configure(SerializationFeature.INDENT_OUTPUT, false);
        
        return mapper;
    }
}
