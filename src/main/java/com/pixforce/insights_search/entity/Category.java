package com.pixforce.insights_search.entity;

/**
 * ===========================================
 * CATEGORIAS DE NOTÍCIAS - ENUM
 * ===========================================
 * 
 * Define as categorias possíveis para classificar notícias processadas pela IA.
 * Cada categoria representa um tipo de insight estratégico para a PixForce.
 * 
 * CATEGORIAS DISPONÍVEIS:
 * - PRODUTO: Novos produtos, tecnologias, inovações no mercado
 * - PARCERIA: Oportunidades de parceria, colaborações, alianças
 * - ESTRATEGIA: Movimentos estratégicos, tendências, mudanças no mercado
 * 
 * @author Equipe PixForce
 * @version 1.0.0
 * @since 2025-01-27
 */
public enum Category {
    
    /**
     * 🧩 PRODUTO
     * 
     * Notícias relacionadas a:
     * - Novos produtos ou tecnologias
     * - Lançamentos de soluções de IA/Visão Computacional
     * - Inovações técnicas
     * - Updates de produtos existentes
     */
    PRODUTO("🧩 Produto", "Novos produtos, tecnologias e inovações no mercado"),
    
    /**
     * 🤝 PARCERIA
     * 
     * Notícias relacionadas a:
     * - Oportunidades de parceria
     * - Colaborações entre empresas
     * - Alianças estratégicas
     * - Joint ventures
     */
    PARCERIA("🤝 Parceria", "Oportunidades de parceria e colaborações"),
    
    /**
     * 📈 ESTRATEGIA
     * 
     * Notícias relacionadas a:
     * - Movimentos estratégicos de mercado
     * - Tendências da indústria
     * - Mudanças regulatórias
     * - Análises de mercado
     */
    ESTRATEGIA("📈 Estratégia", "Movimentos estratégicos e tendências do mercado");
    
    private final String displayName;
    private final String description;
    
    /**
     * Construtor do enum Category.
     * 
     * @param displayName Nome amigável para exibição
     * @param description Descrição detalhada da categoria
     */
    Category(String displayName, String description) {
        this.displayName = displayName;
        this.description = description;
    }
    
    /**
     * Retorna o nome amigável da categoria para exibição.
     * 
     * @return Nome formatado com emoji
     */
    public String getDisplayName() {
        return displayName;
    }
    
    /**
     * Retorna a descrição detalhada da categoria.
     * 
     * @return Descrição explicativa
     */
    public String getDescription() {
        return description;
    }
    
    /**
     * Retorna a categoria baseada no nome (case-insensitive).
     * 
     * @param name Nome da categoria
     * @return Category correspondente ou null se não encontrada
     */
    public static Category fromName(String name) {
        if (name == null) return null;
        
        for (Category category : values()) {
            if (category.name().equalsIgnoreCase(name)) {
                return category;
            }
        }
        return null;
    }
}
