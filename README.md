# 🏷️ LaLouise — Controle de Qualidade e Validade para Restaurantes

Plataforma multi-tenant de rastreamento de validade de etiquetas, controle de qualidade e impressão automática para redes de alimentação.

---

## 📚 Documentação Técnica

Se você é desenvolvedor ou precisa entender a arquitetura técnica:

| Componente      | Link                                                          | Responsabilidade                                               |
| --------------- | ------------------------------------------------------------- | -------------------------------------------------------------- |
| **Backend**     | [📄 Documentação Técnica do Backend](./lalouise/README.md)    | Spring Boot 4 · Java 21 · PostgreSQL · Redis · RabbitMQ        |
| **Frontend**    | [📄 Documentação Técnica do Frontend](./ui/README.md)         | Next.js 16 · React 19 · TypeScript · Tailwind · TanStack Query |
| **Print Agent** | [📄 Documentação Técnica do Print Agent](./printer/README.md) | Spring Boot 3 · AMQP · ZPL / Zebra · Windows Service           |

---

## 🎯 Visão Geral do Negócio

### O Problema

Redes de restaurantes enfrentam desafios críticos no controle de validade de alimentos:

- **Falta de rastreamento** — unidades nao tinham como rastrear em caso de uma visita da vigilancia local
- **Erros manuais** — etiquetas escritas à mão, datas ilegíveis, perda de lotes
- **Perda de rastreabilidade** — impossível auditar qual produto foi descartado e quando
- **Conformidade regulatória** — dificuldade em comprovar conformidade com normas de segurança alimentar
- **Desperdício elevado** — produtos descartados sem registro, impossível analisar padrões e acompanhar validade dos mesmos

### A Solução

A **LaLouise** automatiza o ciclo de vida completo das etiquetas de validade — da emissão ao descarte —, garantindo:

✅ **Padronização** — todas as unidades usam o mesmo sistema
✅ **Rastreabilidade completa** — cada etiqueta, lote e descarte é registrado
✅ **Conformidade** — pronto para auditorias e certificações
✅ **Inteligência de dados** — visibilidade em tempo real sobre status de produtos
✅ **Eficiência operacional** — impressão automática, sem erros manuais

---

## ✅ O que a Plataforma Oferece

| Funcionalidade           | Descrição                                                            |
| ------------------------ | -------------------------------------------------------------------- |
| **Gestão de Etiquetas**  | Emissão, reimpressão, rastreamento de lotes e ciclo de vida completo |
| **Gestão de Produtos**   | Cadastro de produtos por categoria com validades padrão              |
| **Impressão Automática** | Envio direto para impressoras Zebra via agente local                 |
| **Controle por Setor**   | Isolamento de dados por setor (cozinha, confeitaria, açougue, etc.)  |
| **Dashboard Gerencial**  | Visão consolidada do status de validade por unidade e setor          |
| **Controle de Acesso**   | Três perfis (Admin, Manager, Staff) com permissões granulares        |
| **Alertas Automáticos**  | Notificações de produtos próximos ao vencimento                      |
| **Multi-tenancy**        | Suporte nativo para múltiplas unidades com isolamento completo       |

---

## 📦 Arquitetura

```
┌─────────────────────────────────────────────────────────────┐
│                        CLOUD (VPS Linux)                    │
│                                                             │
│  ┌──────────────┐    HTTPS/SSL    ┌─────────────────────┐   │
│  │  Next.js UI  │ ◄── Nginx ──►   │  Spring Boot API    │   │
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

| Camada      | Tecnologia                                        | Local                 |
| ----------- | ------------------------------------------------- | --------------------- |
| Frontend    | Next.js 16 + React 19 + Tailwind + TanStack Query | Vercel                |
| API Backend | Spring Boot 4 + Java 21 + PostgreSQL + Redis      | VPS Linux (Docker)    |
| Mensageria  | RabbitMQ (AMQP)                                   | Nuvem                 |
| Impressão   | Spring Boot 3 + ZPL / Zebra                       | Windows (por unidade) |
| Proxy / SSL | Nginx + HTTPS                                     | VPS Linux             |
| CI/CD       | GitHub Actions                                    | GitHub                |

---

## 🔐 Segurança em Camadas

- 🔐 **JWT** — Autenticação stateless com tokens assinados
- 🛡️ **Spring Security** — Controle de acesso declarativo por roles (Admin, Manager, Staff)
- ⏱️ **Redis Rate Limiting** — Proteção contra abuso por endpoint
- 🔒 **Brute Force Protection** — Bloqueio automático de conta após tentativas suspeitas em endpoints de autenticacao
- 🌐 **Nginx + SSL/HTTPS** — Terminação TLS com certificado válido
- ✅ **Validação em Camadas** — Controller (Bean Validation), Domain (Value Objects)
- 🏗️ **Multi-tenancy** — Isolamento garantido de dados por `restaurant_id`

---

## 🚀 Como Rodar Localmente

### Backend

```bash
cd lalouise
cp .env.example .env  # Configure variáveis
./gradlew clean bootJar
docker compose up -d
```

### Frontend

```bash
cd ui
pnpm install
pnpm dev
```

Acesse em `http://localhost:3000`.

### Print Agent (por unidade Windows)

```bash
cd printer
./gradlew clean bootJar
# Siga o guia em printer/README.md para instalar como Windows Service
```

---

## 🌍 Acesse a Plataforma

Em desenvolvimento ou produção, consulte o README técnico correspondente.

---

## 📞 Links Úteis

- **Backend**: [lalouise/README.md](./lalouise/README.md) — API, endpoints, segurança
- **Frontend**: [ui/README.md](./ui/README.md) — Interface, componentes, deploy Vercel
- **Print Agent**: [printer/README.md](./printer/README.md) — Impressão, Windows Service, configuração
- **Releases**: [RELEASE.md](./RELEASE.md) — Versionamento e deploy

---

<div align="center">
  <sub>LaLouise — Qualidade e Validade sob controle.</sub>
</div>
