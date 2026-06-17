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

---

## O que é a LaLouise?

A LaLouise nasceu de um problema real: restaurantes controlando validade de alimentos com caneta e papel, sem rastreabilidade, sem histórico e sem alertas de vencimento.
É uma plataforma SaaS multi-tenant que automatiza o ciclo de vida das etiquetas de validade — da impressão ao descarte — garantindo rastreabilidade completa por lote, conformidade com normas de segurança alimentar e redução de desperdícios. Cada restaurante opera em ambiente isolado, com controle granular por setor e perfis de acesso por função.

---

## Para quem é?

- **Restaurantes de pequeno e médio** porte que ainda controlam validade com caneta, papel ou planilha
- **Gestores e supervisores** que precisam saber o que está próximo do vencimento em cada setor de forma automática
- **Equipes operacionais** que precisam imprimir e realocar etiquetas rapidamente entre setores sem perder o lote, mantendo o fluxo do alimento internamente
- **Estabelecimentos que recebem visitas da vigilância sanitária** e precisam comprovar conformidade com histórico rastreável

---

## Principais Funcionalidades

### 🏷️ Gestão de Etiquetas (Core)
- Emissão de etiquetas com produto, responsável, setor, lote, fabricação e validade
- Rastreabilidade completa do fluxo: entrada na câmara fria → realocação para cozinha → bancada → descarte
- Reimpressão por setor sem perda de lote — etiqueta anterior descartada automaticamente e gerada nova com mesmo lote mas validade atualizada conforme novo setor/armazenamento
- Ciclo de vida: **Ativa → Expirando → Expirada → Descartada**
- Histórico mantido por 90 dias após vencimento ou descarte para comprovação e auditoria

### 📦 Gestão de Recursos Base
- Produtos por categoria e restaurante (Categoria influencia validade padrão)
- Setores com armazenamentos internos (Tambem influenciam validade e alertas)
- Usuarios com diferentes perfis e contextos (Admin, Manager, Staff) vinculados automaticamente ao seu restaurante e setor
- Restaurantes, cada um com seu próprio ambiente isolado (multi-tenancy)

### 🖨️ Impressão Automática
- Agente de impressão instalado localmente em cada unidade
- Impressão imediata ao emitir ou reimprimir uma etiqueta
- Comunicação assíncrona — funciona mesmo com instabilidade de rede ou falhas temporárias de recursos locais das unidades

### 🔔 Alertas e Monitoramento
- Job de varredura noturna atualiza status de todas as etiquetas
- Notificações automáticas por e-mail toda madrugada após varredura
- Alertas de produtos próximos ao vencimento reduzem perdas e retrabalho

### 🔐 Controle de Acesso
- Três perfis: **Admin**, **Manager** e **Staff**
- Manager vinculado automaticamente ao seu restaurante — sem configuração manual
- Staff vinculado automaticamente ao seu setor — sem configuração manual
- Acesso controlado de forma diferente dependendo do perfil, garantindo segurança e usabilidade
- Bloqueio automático por tentativas suspeitas de acesso

## Arquitetura do Sistema

```
┌─────────────────────────────────────────────────────────────┐
│                        CLOUD (VPS Linux)                    │
│                                                             │
│  ┌──────────────┐    HTTPS/SSL    ┌─────────────────────┐   │
│  │  Next.js UI  │ ◄──── Nginx ──► │  Spring Boot API    │   │
│  │  (Vercel)    │                 │  (Docker · :8080)   │   │
│  └──────────────┘                 └──────────┬──────────┘   │
│                                              │              │
│                        ┌─────────────────────┤              │
│                        │                     │              │
│              ┌─────────▼──────┐  ┌───────────▼─────────┐    │
│              │  PostgreSQL 16 │  │  Redis (Rate Limit) │    │
│              │  (Docker)      │  │  (Docker)           │    │
│              └────────────────┘  └─────────────────────┘    │
│                                                             │
│              ┌──────────────────────────────────────────┐   │
│              │           RabbitMQ (AMQP)                │   │
│              │   exchange: label.exchange               │   │
│              │   routing: print.{restaurantId}          │   │
│              └──────────────────┬───────────────────────┘   │
└─────────────────────────────────┼───────────────────────────┘
                                  │ AMQP
              ┌───────────────────▼───────────────────┐
              │   Print Agent (Windows Service)       │
              │   Spring Boot · ZPL → Zebra Printer   │
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

A LaLouise adota uma estratégia de segurança em camadas — do transporte até o domínio:

**Autenticação & Autorização**
- **JWT** — Autenticação stateless com tokens assinados
- **Spring Security** — Controle de acesso por roles com `@PreAuthorize`
- **Brute Force Protection** — Bloqueio automático após tentativas suspeitas de login

**Infraestrutura**
- **Nginx + SSL/HTTPS** — Terminação TLS e cabeçalhos de segurança
- **Redis Rate Limiting** — Limitação de requisições por endpoint

**Validação de Dados**
- **Bean Validation** — Validação de entrada em todos os DTOs
- **Value Objects** — Validação de domínio encapsulada (`ProductName`, `Lot`)
- **Defesas em camada** — Validações no frontend, controller, serviço e domínio

# ️ LaLouise — Controle de Qualidade e Validade para Restaurantes

> Plataforma multi-tenant de rastreamento de validade de etiquetas, controle de qualidade e impressão automática para redes de alimentação.

**🏢 Projeto proprietário da LaLouise**  
**👨‍💻 Desenvolvido por: Matheus Fraga**

> Este repositório é disponibilizado publicamente para fins de portfólio técnico. Para mais informações, veja [`LICENSE.md`](./LICENSE.md).

---

<div align="center">
  <sub>LaLouise — Qualidade e Validade sob controle.</sub>
</div>
