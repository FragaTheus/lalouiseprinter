# ️ LaLouise — Controle de Qualidade e Validade para Restaurantes

> Plataforma multi-tenant de rastreamento de validade de etiquetas, controle de qualidade e impressão automática para redes de alimentação.

---

##  Sumário

- [ Documentação Técnica](#-documentação-técnica)
- [O que é a LaLouise?](#o-que-é-a-lalouise)
- [Para quem é?](#para-quem-é)
- [Principais Funcionalidades](#principais-funcionalidades)
- [Arquitetura do Sistema](#arquitetura-do-sistema)
- [Multi-tenancy & Domínio](#multi-tenancy--domínio)
- [Hospedagem & Infraestrutura](#hospedagem--infraestrutura)
- [Segurança](#segurança)
- [Como Executar o Projeto Completo](#como-executar-o-projeto-completo)
- [Estrutura de Pastas](#estrutura-de-pastas)

---

##  Documentação Técnica

> Links diretos para a stack que você precisa:

| Componente | Descrição | Documento |
|---|---|---|
| ️ **Backend** | API REST · Spring Boot 4 · Java 21 · PostgreSQL · Redis · RabbitMQ | [`lalouise/README.md`](./lalouise/README.md) |
|  **Frontend** | Interface Web · Next.js 16 · React 19 · TypeScript · Tailwind | [`ui/README.md`](./ui/README.md) |
| ️ **Print Agent** | Microsserviço de impressão · Spring Boot 3 · AMQP · ZPL / Zebra | [`printer/README.md`](./printer/README.md) |
|  **Releases** | Versionamento, tags e deploy | [`RELEASE.md`](./RELEASE.md) |

---

## O que é a LaLouise?

A **LaLouise** é uma solução SaaS de controle de qualidade e validade de alimentos desenvolvida para redes de restaurantes e estabelecimentos de alimentação. O sistema automatiza o ciclo de vida das etiquetas de validade — da impressão ao descarte —, garantindo conformidade com as normas de segurança alimentar e reduzindo desperdícios.

Cada restaurante opera em seu próprio ambiente isolado (**multi-tenant**), com controle granular por setor (cozinha, confeitaria, açougue, etc.) e rastreabilidade completa por lote.

---

## Para quem é?

- **Redes de restaurantes** que precisam padronizar o controle de validade em múltiplas unidades
- **Gestores e supervisores** que precisam de visibilidade em tempo real sobre o status dos produtos em estoque
- **Equipes operacionais** que precisam imprimir e gerenciar etiquetas de forma rápida e confiável
- **Auditores e responsáveis técnicos** que precisam de rastreabilidade de lotes e histórico de descartes

---

## Principais Funcionalidades

### ️ Gestão de Etiquetas
- Emissão de etiquetas de validade com data de fabricação, vencimento e lote
- Reimpressão de etiquetas sem perda de rastreabilidade
- Ciclo de vida completo: **Ativa → Expirando → Expirada → Descartada**
- Alertas automáticos de produtos próximos ao vencimento

### ️ Gestão de Produtos
- Cadastro de produtos por categoria e restaurante
- Controle por setor (cozinha, linha fria, confeitaria, etc.)
- Atribuição de validades padrão por produto

### ️ Impressão Automática
- Envio imediato para impressoras Zebra via protocolo ZPL
- Agente de impressão instalado localmente em cada unidade
- Comunicação assíncrona via mensageria (RabbitMQ)

###  Controle de Acesso
- Três perfis de acesso: **Admin**, **Manager** e **Staff**
- Cada usuário tem acesso apenas ao seu restaurante e setor
- Bloqueio automático de conta por tentativas de acesso suspeitas

###  Dashboard Gerencial
- Visão geral do status de validade por unidade e setor
- Histórico de impressões e descartes

---

## Arquitetura do Sistema

```
┌─────────────────────────────────────────────────────────────┐
│                        CLOUD (VPS Linux)                     │
│                                                             │
│  ┌──────────────┐    HTTPS/SSL    ┌─────────────────────┐  │
│  │  Next.js UI  │ ◄──── Nginx ────► │  Spring Boot API   │  │
│  │  (Vercel)    │                 │  (Docker · :8080)   │  │
│  └──────────────┘                 └──────────┬──────────┘  │
│                                              │              │
│                        ┌─────────────────────┤              │
│                        │                     │              │
│              ┌─────────▼──────┐  ┌───────────▼─────────┐  │
│              │  PostgreSQL 16  │  │  Redis (Rate Limit)  │  │
│              │  (Docker)       │  │  (Docker)            │  │
│              └────────────────┘  └────────────────────── ┘  │
│                                                             │
│              ┌──────────────────────────────────────────┐  │
│              │           RabbitMQ (AMQP)                  │  │
│              │   exchange: label.exchange                  │  │
│              │   routing: print.{restaurantId}             │  │
│              └──────────────────┬───────────────────────┘  │
└─────────────────────────────────┼───────────────────────────┘
                                  │ AMQP
              ┌───────────────────▼───────────────────┐
              │   Print Agent (Windows Service)        │
              │   Spring Boot · ZPL → Zebra Printer    │
              └───────────────────────────────────────┘
```

---

## Multi-tenancy & Domínio

A LaLouise foi construída para atender **múltiplos restaurantes (tenants)** em uma única instância do sistema. Cada tenant possui:

- Seus próprios **usuários, setores e produtos**
- **Isolamento de dados** garantido em nível de banco de dados via `restaurant_id`
- Contexto de autenticação que injeta automaticamente o tenant em cada operação
- **Agente de impressão** dedicado por unidade, vinculado ao `restaurantId`

A resolução do contexto de tenant é feita automaticamente no backend a partir do token JWT do usuário autenticado, sem necessidade de configuração adicional na UI.

---

## Hospedagem & Infraestrutura

| Camada | Tecnologia | Local |
|---|---|---|
| Frontend | Next.js + Vercel | Nuvem (Vercel) |
| API Backend | Spring Boot + Docker | VPS Linux |
| Banco de dados | PostgreSQL 16 + Docker | VPS Linux |
| Cache / Rate Limit | Redis + Docker | VPS Linux |
| Mensageria | RabbitMQ (AMQP) | Nuvem |
| Proxy / SSL | Nginx + HTTPS | VPS Linux |
| Print Agent | Spring Boot JAR | Windows local (por unidade) |
| CI/CD | GitHub Actions | GitHub |

---

## Segurança

A LaLouise adota uma **estratégia de segurança em camadas**:

-  **JWT** — Autenticação stateless com tokens assinados
- ️ **Spring Security** — Controle de acesso por roles com `@PreAuthorize`
- ⏱️ **Redis Rate Limiting** — Limitação de requisições por endpoint
-  **Brute Force Protection** — Algoritmo de bloqueio automático de conta após tentativas suspeitas de login
-  **Nginx + SSL/HTTPS** — Terminação TLS e cabeçalhos de segurança no servidor
- ✅ **Bean Validation** — Validação de entrada em todos os DTOs
- ️ **Value Objects** — Validação de domínio encapsulada (ex: `ProductName`, `Lot`)
- ️ **Defesas em camada** — Validações no frontend, controller, serviço e domínio

---

## Como Executar o Projeto Completo

### Pré-requisitos

- Docker e Docker Compose
- Java 21+
- Node.js 20+ e pnpm
- RabbitMQ acessível (nuvem ou local)
- Redis acessível

### 1. Backend

```bash
cd lalouise
cp .env.example .env  # configure suas variáveis
./gradlew clean bootJar
docker compose up -d
```

### 2. Frontend

```bash
cd ui
pnpm install
pnpm dev
```

### 3. Print Agent (por unidade Windows)

```bash
cd printer
./gradlew clean bootJar
# Siga o guia em printer/README.md para instalar como Windows Service
```

> Consulte cada README técnico para detalhes completos de configuração.

---

## Estrutura de Pastas

```
lalouiseprinter/
├── lalouise/          # Backend API — Spring Boot 4 + Java 21
│   ├── src/
│   │   ├── main/java/ # Código-fonte (DDD: domain, application, infra)
│   │   └── resources/ # Configs, migrations Flyway
│   ├── docker-compose.yml
│   └── build.gradle
│
├── ui/                # Frontend — Next.js 16 + React 19 + TypeScript
│   ├── src/
│   │   ├── app/       # Rotas Next.js (App Router)
│   │   ├── features/  # Módulos de funcionalidade
│   │   └── shared/    # Componentes, stores, tipos compartilhados
│   └── package.json
│
├── printer/           # Print Agent — Spring Boot 3 + AMQP + ZPL
│   ├── src/
│   └── build.gradle
│
├── README.md          # Este arquivo
└── RELEASE.md         # Guia de releases e versionamento
```

---

# ️ LaLouise — Controle de Qualidade e Validade para Restaurantes

> Plataforma multi-tenant de rastreamento de validade de etiquetas, controle de qualidade e impressão automática para redes de alimentação.

**🏢 Projeto proprietário da LaLouise**  
**👨‍💻 Desenvolvido por: Matheus Fraga**

> Este repositório é disponibilizado publicamente para fins de portfólio técnico. Para mais informações, veja [`LICENSE.md`](./LICENSE.md).

---

<div align="center">
  <sub>LaLouise — Qualidade e Validade sob controle.</sub>
</div>
