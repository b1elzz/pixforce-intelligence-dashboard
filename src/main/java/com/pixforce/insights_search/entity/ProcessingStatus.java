package com.pixforce.insights_search.entity;

/**
 * ===========================================
 * STATUS DE PROCESSAMENTO - ENUM
 * ===========================================
 * 
 * Define os possíveis status de uma notícia durante o pipeline de processamento.
 * Utilizado para rastrear o progresso da análise e identificar possíveis problemas.
 * 
 * FLUXO DE STATUS:
 * PENDING → PROCESSING → COMPLETED
 *    ↓           ↓
 * FAILED ← RETRYING
 * 
 * @author Equipe PixForce
 * @version 1.0.0
 * @since 2025-01-27
 */
public enum ProcessingStatus {
    
    /**
     * ⏳ PENDING
     * 
     * Notícia coletada mas ainda não processada pela IA.
     * Estado inicial após coleta da API NewsData.io.
     */
    PENDING("⏳ Pendente", "Aguardando processamento pela IA"),
    
    /**
     * 🔄 PROCESSING
     * 
     * Notícia sendo analisada pelo Gemini Pro.
     * Estado temporário durante a análise de relevância.
     */
    PROCESSING("🔄 Processando", "Sendo analisada pela IA"),
    
    /**
     * ✅ COMPLETED
     * 
     * Notícia processada com sucesso.
     * Análise de relevância concluída e categoria definida.
     */
    COMPLETED("✅ Concluída", "Processamento finalizado com sucesso"),
    
    /**
     * ❌ FAILED
     * 
     * Falha no processamento da notícia.
     * Pode ser devido a erro na API do Gemini ou timeout.
     */
    FAILED("❌ Falhou", "Erro durante o processamento"),
    
    /**
     * 🔄 RETRYING
     * 
     * Tentativa de reprocessamento após falha.
     * Estado temporário para notícias que falharam anteriormente.
     */
    RETRYING("🔄 Tentando novamente", "Reprocessando após falha");
    
    private final String displayName;
    private final String description;
    
    /**
     * Construtor do enum ProcessingStatus.
     * 
     * @param displayName Nome amigável para exibição
     * @param description Descrição do status
     */
    ProcessingStatus(String displayName, String description) {
        this.displayName = displayName;
        this.description = description;
    }
    
    /**
     * Retorna o nome amigável do status para exibição.
     * 
     * @return Nome formatado com emoji
     */
    public String getDisplayName() {
        return displayName;
    }
    
    /**
     * Retorna a descrição do status.
     * 
     * @return Descrição explicativa
     */
    public String getDescription() {
        return description;
    }
    
    /**
     * Verifica se o status indica que o processamento foi concluído.
     * 
     * @return true se COMPLETED, false caso contrário
     */
    public boolean isCompleted() {
        return this == COMPLETED;
    }
    
    /**
     * Verifica se o status indica que houve falha.
     * 
     * @return true se FAILED, false caso contrário
     */
    public boolean isFailed() {
        return this == FAILED;
    }
    
    /**
     * Verifica se o status indica processamento em andamento.
     * 
     * @return true se PROCESSING ou RETRYING, false caso contrário
     */
    public boolean isProcessing() {
        return this == PROCESSING || this == RETRYING;
    }
}
