# 🧠 PixForce Intelligence Dashboard

## 📋 Visão Geral

Este é o **MVP (Produto Mínimo Viável)** do PixForce Intelligence Dashboard - um sistema automatizado de inteligência de mercado que coleta, analisa e apresenta insights relevantes sobre o mercado de **Inteligência Artificial e Visão Computacional**.

## 🎯 Objetivo

Transformar o grande volume de dados do mercado em **informações realmente úteis e estratégicas** para a PixForce, automatizando o monitoramento de concorrentes e tendências do mercado.

## 🏗️ Arquitetura

```
🧭 FRONT-END (Futuro)
       ↓
🌐 BACK-END (Spring Boot 3 / Java 21)
 ├── Scheduler (executa 1x por dia às 08:00)
 │     ↓
 │  Data Collector
 │   ├── NewsData.io API (principal fonte)
 │   ├── RSS (opcional)
 │   └── Web Scraping seletivo (sites concorrentes)
 │
 ├── AI Processor
 │   ├── Pré-filtro local (palavras-chave)
 │   ├── Gemini Pro API (relevância + categoria + ação sugerida)
 │
 ├── Database (PostgreSQL)
 │   ├── Armazena apenas notícias relevantes
 │   ├── Auto-limpeza de dados antigos (Scheduler)
 │
 ├── API REST
 │   ├── /news → lista notícias processadas
 │   ├── /news?categoria=Produto → filtro por categoria
 │   ├── /summary/daily → resumo diário de insights
 │   └── /collect → coleta manual opcional
 │
 └── Logs + Monitoramento
       (Spring Actuator / Logback)
```

## 🚀 Como Executar

### Pré-requisitos

- **Java 21** ou superior
- **PostgreSQL** 13+ instalado e rodando
- **Maven** 3.6+ (ou use o wrapper incluído: `./mvnw`)

### 1. Configuração do Banco de Dados

```sql
-- Criar o banco de dados
CREATE DATABASE pixforce_insights;
CREATE USER pixforce WITH PASSWORD 'insights123';
GRANT ALL PRIVILEGES ON DATABASE pixforce_insights TO pixforce;
```

### 2. Configuração das APIs

Copie o arquivo `env.example` para `.env` e preencha as chaves:

```bash
cp env.example .env
```

Preencha as chaves no arquivo `.env`:
- **NewsData.io**: https://newsdata.io/register (200 requests/dia grátis)
- **Gemini Pro**: https://makersuite.google.com/app/apikey (15 requests/min grátis)

### 3. Executar a Aplicação

```bash
# Usando Maven wrapper (recomendado)
./mvnw spring-boot:run

# Ou usando Maven instalado
mvn spring-boot:run
```

A aplicação estará disponível em: `http://localhost:8080/api`

## 📊 Funcionalidades

### ✅ Implementado
- [x] Configuração base do projeto (Spring Boot 3 + Java 21)
- [x] Configuração do banco PostgreSQL
- [x] Configuração das APIs externas (NewsData.io + Gemini Pro)
- [x] Scheduler habilitado para automação
- [x] Estrutura de logging e monitoramento

### 🚧 Em Desenvolvimento
- [ ] Entidades JPA (NewsItem, ProcessedNewsItem)
- [ ] Serviços de coleta de dados
- [ ] Integração com Gemini Pro para análise de IA
- [ ] API REST endpoints
- [ ] Schedulers automáticos

### 🔮 Futuras Evoluções
- [ ] Front-end React/Angular/Vue
- [ ] Sistema de alertas automáticos
- [ ] Dashboard analítico interativo
- [ ] Integração com redes sociais
- [ ] Suporte multilíngue

## 🛠️ Tecnologias Utilizadas

| Camada | Tecnologia | Versão |
|--------|------------|--------|
| Linguagem | **Java** | 21 |
| Framework | **Spring Boot** | 3.5.6 |
| Banco de Dados | **PostgreSQL** | 13+ |
| ORM | **Spring Data JPA** | - |
| API de Notícias | **NewsData.io** | - |
| IA | **Gemini Pro** | - |
| Web Scraping | **Jsoup** | 1.17.2 |
| HTTP Client | **WebFlux** | - |
| Testes | **Testcontainers** | - |

## 📁 Estrutura do Projeto

```
src/
├── main/
│   ├── java/com/pixforce/insights_search/
│   │   ├── InsightsSearchApplication.java    # Classe principal
│   │   ├── config/                           # Configurações
│   │   ├── entity/                           # Entidades JPA
│   │   ├── repository/                       # Repositórios
│   │   ├── service/                          # Serviços de negócio
│   │   ├── controller/                      # Controllers REST
│   │   └── scheduler/                       # Tarefas agendadas
│   └── resources/
│       ├── application.properties           # Configurações
│       └── static/                         # Arquivos estáticos
└── test/                                   # Testes unitários
```

## 🔧 Configurações Importantes

### application.properties
- **Banco**: `pixforce_insights` (PostgreSQL)
- **Porta**: `8080`
- **Context Path**: `/api`
- **Scheduler**: Coleta às 8h, limpeza às 3h

### Variáveis de Ambiente
- `NEWSDATA_API_KEY`: Chave da API NewsData.io
- `GEMINI_API_KEY`: Chave da API Gemini Pro
- `DB_USERNAME`: Usuário do banco (padrão: pixforce)
- `DB_PASSWORD`: Senha do banco (padrão: insights123)

## 📝 Próximos Passos

1. **Criar entidades JPA** para armazenar notícias
2. **Implementar serviços** de coleta de dados
3. **Integrar Gemini Pro** para análise de IA
4. **Criar endpoints REST** para consulta
5. **Implementar schedulers** automáticos

## 🤝 Contribuição

Este é um projeto em dupla. Para contribuir:

1. Faça um commit por classe implementada
2. Documente bem o código
3. Siga as boas práticas de desenvolvimento
4. Teste antes de commitar

## 📞 Suporte

Para dúvidas sobre o projeto, consulte:
- Este README
- Comentários no código
- Documentação das APIs utilizadas

---

**Desenvolvido com ❤️ pela equipe PixForce - Geração Caldeira 2025**
