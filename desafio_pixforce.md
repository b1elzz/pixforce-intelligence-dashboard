# 💼 Desafio PixForce - Geração Caldeira 2025

## 🧩 Contexto
A **PixForce** atua em setores altamente dinâmicos — como **indústria, energia e óleo e gás** — desenvolvendo soluções baseadas em **Inteligência Artificial e Visão Computacional**. Esses mercados evoluem rapidamente, com novas tecnologias, empresas e oportunidades surgindo constantemente. Nesse cenário, a **informação é estratégica**: quem identifica tendências primeiro tem vantagem competitiva.

Entretanto, a PixForce enfrenta um desafio crescente para **acompanhar o ritmo acelerado do mercado e da concorrência**. Atualmente, o monitoramento é feito de forma **manual e fragmentada**, exigindo que colaboradores acessem múltiplas fontes: sites de inovação, páginas de concorrentes, portais especializados, redes sociais, editais e muito mais.

Essa abordagem consome tempo, energia e nem sempre resulta em decisões rápidas e embasadas.

---

## 🚧 Problema Central
A empresa tem **um excesso de dados e informações**, mas **dificuldade em identificar o que realmente importa**. O desafio, portanto, é **transformar essa enxurrada de dados em insights relevantes** para orientar decisões estratégicas.

> "O grande problema não é só o volume gigante de informações que surge todos os dias, mas sim a dificuldade de entender o que realmente importa para a PixForce." — Documento da empresa

Como resultado:
- Decisões importantes são tomadas **com base em informações incompletas**;
- A **reação às mudanças do mercado** é lenta;
- Cada área da empresa tem **uma visão limitada** da realidade;
- O monitoramento exige **muito esforço humano**.

---

## 🎯 Objetivo do Desafio
Desenvolver um **protótipo de agente de inteligência de mercado** capaz de **automatizar o acompanhamento de concorrentes e do mercado**, coletando e analisando informações relevantes para gerar **insights claros e acionáveis**.

O sistema deve permitir que equipes de **marketing, vendas e negócios** tomem decisões mais rápidas e informadas.

---

## ⚙️ Requisitos do Projeto
### Aplicação Web
Uma aplicação web que exiba um **painel de inteligência de mercado** com os principais insights do dia.

### Coleta de Dados
- Buscar informações de **um site concorrente** e de **uma fonte pública** (ex: Google News, YouTube, RSS ou APIs de notícias);
- Utilizar **web scraping** (ex: Jsoup) caso não haja API disponível.

### Processamento de Dados
- Armazenar as informações coletadas;
- **Remover duplicatas** e **gerar resumos automáticos**;
- Classificar cada item em categorias como:
  - 🧩 Produto
  - 🤝 Parceria
  - 📈 Estratégia
- Fornecer para cada notícia:
  - **O que houve?** (resumo curto)
  - **Por que importa?** (classificação)
  - **Ação sugerida** (ex: “Avaliar oportunidade de parceria”).

---

## 🧠 Requisitos Específicos por Trilha
### Marketing
- Identificar novas oportunidades de conteúdo, cases e parcerias;
- Definir ações sugeridas (ex: criar post, entrar em contato com concorrente).

### Gestão Comercial
- Auxiliar o time de vendas a entender o cenário competitivo;
- Melhorar o discurso comercial e a tomada de decisão;
- Definir KPIs, como número de insights gerados e tempo economizado.

### IA e Dados
- Selecionar fontes de dados e queries a serem monitoradas;
- Implementar **NLP** para resumir textos;
- Desenvolver modelo simples de classificação (regras ou ML leve).

### Programação Java
- Desenvolver o **back-end em Spring Boot**;
- Implementar scraping com **Jsoup**, APIs ou RSS;
- Expor informações via **API REST** para o front-end (React, Angular ou Vue).

---

## 📊 Critérios de Sucesso
- Redução do tempo gasto em pesquisa manual;
- Geração automática de insights acionáveis;
- Clareza e organização das informações no painel web;
- Capacidade de atualização periódica e autônoma.

---

## 💬 Desafio Final
> Como transformar o grande volume de dados do mercado de IA e visão computacional em **informações realmente úteis e estratégicas** para a PixForce?

O desafio está lançado: usar **tecnologia, dados e IA** para criar uma ferramenta que **pense como um analista de mercado**, mas aja com a **velocidade de uma máquina.**

