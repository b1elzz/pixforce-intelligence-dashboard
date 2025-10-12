# 💡 Solução Proposta - PixForce Intelligence Dashboard (MVP)

## 🎯 Visão Geral
A solução propõe o desenvolvimento de um **MVP (Produto Mínimo Viável)** para um **Painel de Inteligência de Mercado** automatizado, que coleta, analisa e apresenta insights relevantes sobre o mercado de **Inteligência Artificial e Visão Computacional**, principais áreas de atuação da **PixForce**.

A ideia central é criar um sistema que **funcione como um analista de mercado automatizado**, capaz de buscar notícias de diversas fontes, filtrar o que é relevante para a empresa e entregar uma visão estratégica consolidada, de forma **simples, rápida e inteligente.**

---

## 🧩 Arquitetura da Solução

```plaintext
🧭 FRONT-END
  (React / Angular / Vue)
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

---

## 🕒 Scheduler (Agendamento Automático)
O sistema realiza a coleta e análise **automaticamente todos os dias às 08:00 da manhã**, sem necessidade de intervenção humana.

```java
@Scheduled(cron = "0 0 8 * * *")
public void runDailyCollection() {
    pipelineService.executePipeline();
}
```

Esse agendamento garante que o painel esteja sempre atualizado com as notícias mais recentes do dia.

---

## 📰 Coleta de Dados (Data Collector)
O módulo de coleta busca informações de diversas fontes:

- **NewsData.io API:** API principal, capaz de realizar buscas em tempo real por palavras-chave relacionadas ao mercado de IA e visão computacional;
- **RSS Feeds:** fontes adicionais (TecMundo, Olhar Digital, CanalTech, etc.);
- **Web Scraping:** utilizado apenas quando não há API disponível para sites específicos de concorrentes.

Exemplo de query típica:
```
https://newsdata.io/api/1/news?apikey=CHAVE&q=visao%20computacional%20OR%20inteligencia%20artificial&language=pt&country=br
```

---

## 🤖 Processamento de Dados (IA com Gemini Pro)
Após coletar os dados, cada notícia é enviada ao módulo de IA para **análise semântica e contextual**.

O **Gemini Pro** é utilizado para:
- Avaliar se a notícia é **relevante** para a PixForce;
- Explicar o **motivo da relevância**;
- Classificar o conteúdo em **Produto, Parceria ou Estratégia**;
- Sugerir uma **ação recomendada**.

Exemplo de prompt enviado à IA:
```text
Você é um analista da PixForce.
Analise a notícia abaixo e retorne APENAS em JSON:
{
  "relevante": true/false,
  "motivo": "...",
  "categoria": "Produto/Parceria/Estratégia",
  "acao_sugerida": "..."
}
Texto: <conteúdo da notícia>
```

A resposta é processada e armazenada como um objeto `ProcessedNewsItem` no banco de dados.

---

## 💾 Armazenamento e Retenção
Os dados são salvos em um banco **PostgreSQL**, contendo apenas as notícias consideradas relevantes. Para evitar acúmulo, o sistema executa uma **limpeza diária** às 3h da manhã:

```java
@Scheduled(cron = "0 0 3 * * *")
public void deleteOldRecords() {
    repository.deleteByCreatedAtBefore(LocalDateTime.now().minusDays(7));
}
```

Assim, o painel sempre exibe informações **atuais e enxutas**.

---

## 🌐 API REST
O back-end expõe uma API RESTful simples, consumida pelo front-end:

| Endpoint | Descrição |
|-----------|------------|
| `GET /news` | Retorna todas as notícias processadas |
| `GET /news?categoria=Produto` | Filtra por categoria |
| `GET /summary/daily` | Gera o resumo diário de insights |
| `POST /collect` | Dispara coleta manual (modo admin) |

---

## ⚙️ Tecnologias Principais
| Camada | Tecnologia |
|---------|-------------|
| Linguagem | **Java 21** |
| Framework | **Spring Boot 3** |
| API de Notícias | **NewsData.io** |
| IA | **Gemini Pro (Google)** |
| Banco de Dados | **PostgreSQL** |
| ORM | **Spring Data JPA** |
| Scheduler | **Spring Scheduler** |
| Scraping | **Jsoup (opcional)** |
| Front-end | **React / Angular / Vue** |

---

## 🔐 Configuração Base (application.yml)
```yaml
newsdata:
  api:
    key: YOUR_NEWSDATA_API_KEY
gemini:
  api:
    key: YOUR_GEMINI_API_KEY
spring:
  datasource:
    url: jdbc:postgresql://localhost:5432/pixforce
    username: user
    password: pass
```

---

## 📊 Benefícios do MVP
✅ Automatiza a coleta e análise de informações.
✅ Elimina tarefas manuais e repetitivas.
✅ Fornece insights claros e categorizados.
✅ Mantém o foco em relevância, não volume.
✅ Arquitetura escalável e moderna (Java 21 / Spring Boot 3).
✅ Fácil evolução para incluir dashboards, alertas e IA preditiva.

---

## 🧠 Futuras Evoluções (Pós-MVP)
- Integração com APIs de redes sociais (LinkedIn, YouTube);
- Sistema de alertas automáticos via e-mail ou Slack;
- Painel analítico interativo com gráficos e filtros avançados;
- Fine-tuning de modelo interno para classificação mais precisa;
- Suporte multilíngue (inglês, espanhol, português).

---

## 🚀 Conclusão
O **PixForce Intelligence Dashboard (MVP)** demonstra de forma prática como a empresa pode **utilizar IA aplicada para transformar dados dispersos em insights estratégicos e acionáveis**, acelerando a tomada de decisão e fortalecendo sua liderança no mercado de IA e visão computacional.

