-- ===========================================
-- PIXFORCE INSIGHTS SEARCH - SCHEMA DO BANCO
-- ===========================================
-- 
-- Script SQL para criação do banco de dados e tabelas.
-- Execute este script no PostgreSQL antes de rodar a aplicação.
--
-- COMANDO PARA EXECUTAR:
-- psql -U postgres -f database/schema.sql
--

-- ===========================================
-- CRIAÇÃO DO BANCO DE DADOS
-- ===========================================

-- Criar banco de dados (se não existir)
CREATE DATABASE pixforce_insights;

-- Conectar ao banco criado
\c pixforce_insights;

-- ===========================================
-- CRIAÇÃO DO USUÁRIO E PERMISSÕES
-- ===========================================

-- Criar usuário (se não existir)
DO $$
BEGIN
    IF NOT EXISTS (SELECT FROM pg_catalog.pg_roles WHERE rolname = 'pixforce') THEN
        CREATE USER pixforce WITH PASSWORD 'insights123';
    END IF;
END
$$;

-- Conceder permissões
GRANT ALL PRIVILEGES ON DATABASE pixforce_insights TO pixforce;
GRANT ALL PRIVILEGES ON SCHEMA public TO pixforce;
GRANT ALL PRIVILEGES ON ALL TABLES IN SCHEMA public TO pixforce;
GRANT ALL PRIVILEGES ON ALL SEQUENCES IN SCHEMA public TO pixforce;

-- ===========================================
-- COMENTÁRIOS DAS TABELAS
-- ===========================================

-- As tabelas serão criadas automaticamente pelo Hibernate
-- quando a aplicação Spring Boot for executada pela primeira vez.
-- 
-- TABELAS QUE SERÃO CRIADAS:
-- - news_items: Notícias brutas coletadas da API
-- - processed_news_items: Notícias analisadas pela IA
-- 
-- CONFIGURAÇÕES IMPORTANTES:
-- - spring.jpa.hibernate.ddl-auto=update (cria/atualiza automaticamente)
-- - spring.datasource.url=jdbc:postgresql://localhost:5432/pixforce_insights
-- - spring.datasource.username=pixforce
-- - spring.datasource.password=insights123

-- ===========================================
-- ÍNDICES RECOMENDADOS (OPCIONAL)
-- ===========================================

-- Estes índices podem ser criados manualmente para melhorar performance
-- ou deixar que o Hibernate os crie automaticamente

-- Índices para news_items
-- CREATE INDEX IF NOT EXISTS idx_news_items_status ON news_items(status);
-- CREATE INDEX IF NOT EXISTS idx_news_items_published_at ON news_items(published_at);
-- CREATE INDEX IF NOT EXISTS idx_news_items_created_at ON news_items(created_at);
-- CREATE INDEX IF NOT EXISTS idx_news_items_source ON news_items(source);

-- Índices para processed_news_items
-- CREATE INDEX IF NOT EXISTS idx_processed_news_category ON processed_news_items(category);
-- CREATE INDEX IF NOT EXISTS idx_processed_news_relevant ON processed_news_items(is_relevant);
-- CREATE INDEX IF NOT EXISTS idx_processed_news_confidence ON processed_news_items(confidence_score);
-- CREATE INDEX IF NOT EXISTS idx_processed_news_processed_at ON processed_news_items(processed_at);

-- ===========================================
-- VERIFICAÇÃO FINAL
-- ===========================================

-- Verificar se o banco foi criado corretamente
SELECT 'Banco pixforce_insights criado com sucesso!' as status;

-- Verificar usuário
SELECT 'Usuário pixforce criado com sucesso!' as status;

-- Listar bancos disponíveis
\list

-- ===========================================
-- PRÓXIMOS PASSOS
-- ===========================================
--
-- 1. Execute este script: psql -U postgres -f database/schema.sql
-- 2. Configure as chaves de API no arquivo .env
-- 3. Execute a aplicação: ./mvnw spring-boot:run
-- 4. As tabelas serão criadas automaticamente pelo Hibernate
-- 5. Acesse: http://localhost:8080/api/actuator/health
--
