# 🌐 LaLouise — Frontend

Interface Web do sistema LaLouise · Next.js 16 · React 19 · TypeScript · Tailwind CSS · TanStack Query

---

## 📚 Sumário Técnico

- [Visão Geral](#visão-geral)
- [Stack Tecnológica](#stack-tecnológica)
- [Funcionalidades & Módulos](#funcionalidades--módulos)
- [Autenticação & Controle de Acesso](#autenticação--controle-de-acesso)
- [Estrutura de Pastas](#estrutura-de-pastas)
- [Variáveis de Ambiente](#variáveis-de-ambiente)
- [Como Executar](#como-executar)

---

## Visão Geral

O frontend da LaLouise é uma aplicação web construída com **Next.js 16 App Router** e **React 19**, fornecendo uma interface intuitiva para gestão de etiquetas de validade, produtos, setores e usuários em contexto multi-tenant.

---

## 🛠️ Stack Tecnológica

| Categoria | Tecnologia | Uso |
|---|---|---|
| Framework | Next.js 16.2.4 (App Router) | Roteamento, SSR, otimização |
| UI Library | React 19.2.4 | Componentes e estado |
| Linguagem | TypeScript 5 | Tipagem estática |
| Estilo | Tailwind CSS 4 | Estilização utilitária |
| Componentes | shadcn/ui + Radix UI | Componentes acessíveis |
| Ícones | React Icons | Ícones vetoriais |
| Data Fetching | TanStack Query v4 | Requisições, cache, sincronização |
| Formulários | React Hook Form v7 | Gerenciamento de formulários |
| HTTP Client | Axios 1.x | Requisições HTTP com interceptors |
| Estado Global | Zustand 5 | Store do usuário autenticado |
| Notificações | Sonner | Toasts e notificações |
| Build | pnpm + Next.js | Gerenciador de pacotes e compilação |
| Deploy | Vercel | Hospedagem e CI/CD |

---

## 📋 Funcionalidades & Módulos

O projeto segue uma organização por **feature modules** em `src/features/`:

| Módulo | Rota | Acesso | Descrição |
|---|---|---|---|
| `login` | `/auth` | Público | Tela de autenticação com JWT |
| `dashboard` | `/dashboard` | Todos | Visão geral do status de etiquetas |
| `home` | `/` | Todos | Página inicial / landing |
| `label` | `/dashboard/labels` | STAFF+ | Emissão, consulta e reimpressão de etiquetas |
| `product` | `/dashboard/products` | MANAGER+ | Gestão de produtos e categorias |
| `sector` | `/dashboard/sectors` | MANAGER+ | Gestão de setores do restaurante |
| `restaurant` | `/dashboard/restaurants` | ADMIN | Gestão de tenants/restaurantes |
| `staff` | `/dashboard/staff` | MANAGER+ | Gestão de usuários operacionais |
| `manager` | `/dashboard/manager` | ADMIN | Gestão de gerentes |
| `admin` | `/dashboard/admin` | ADMIN | Painel de administração global |
| `profile` | `/dashboard/profile` | Todos | Perfil do usuário autenticado |
| `forbidden` | `/forbidden` | — | Página de acesso não autorizado |

---

### Roles e Permissões

| Role | Capacidades |
|---|---|
| `ADMIN` | Acesso total — gestão de tenants, usuários, relatórios globais |
| `MANAGER` | Gestão do restaurante — produtos, setores, equipe |
| `STAFF` | Operacional — emissão e consulta de etiquetas |

---

## 📁 Estrutura de Pastas

```
ui/
├── src/
│   ├── app/                          # Next.js App Router
│   │   ├── layout.tsx                # Layout raiz (providers, fontes)
│   │   ├── page.tsx                  # Página inicial
│   │   ├── providers.tsx             # React Query, Theme providers
│   │   ├── globals.css               # Estilos globais + variáveis CSS
│   │   ├── auth/                     # Rota de autenticação
│   │   ├── dashboard/                # Área protegida autenticada
│   │   └── forbidden/                # Página de acesso negado
│   │
│   ├── features/                     # Módulos de funcionalidade
│   │   ├── admin/                    # Painel admin
│   │   ├── dashboard/                # Dashboard com métricas
│   │   ├── home/                     # Página home
│   │   ├── label/                    # Gestão de etiquetas
│   │   ├── login/                    # Formulário de login
│   │   ├── manager/                  # Gestão de gerentes
│   │   ├── product/                  # Gestão de produtos
│   │   ├── profile/                  # Perfil do usuário
│   │   ├── restaurant/               # Gestão de restaurantes
│   │   ├── sector/                   # Gestão de setores
│   │   └── staff/                    # Gestão de staff
│   │
│   ├── shared/
│   │   ├── components/               # Componentes reutilizáveis (shadcn/ui)
│   │   ├── assets/                   # Imagens, ícones estáticos
│   │   ├── config/                   # Configurações (axios instance, etc.)
│   │   ├── stores/                   # Stores Zustand compartilhadas
│   │   └── type/                     # Types TypeScript compartilhados
│   │
│   ├── store/
│   │   └── user-store.ts             # Store do usuário autenticado (Zustand)
│   │
│   └── lib/
│       └── utils.ts                  # Utilitários (cn, formatters, etc.)
│
├── next.config.ts                    # Configuração do Next.js
├── tailwind.config.ts                # Configuração do Tailwind CSS
├── tsconfig.json                     # Configuração TypeScript
├── components.json                   # Configuração shadcn/ui
├── package.json
└── pnpm-lock.yaml
```

---

## 🚀 Como Executar

### Pré-requisitos

- Node.js 20+
- pnpm (`npm install`)

### Desenvolvimento

```bash
# Instalar dependências
pnpm install

# Configurar api para requisição do localhost
http://localhost:8080/

# Iniciar servidor de desenvolvimento
pnpm run dev
```

A aplicação estará disponível em `http://localhost:3000`.

> Para autenticação funcionar, o backend [`lalouise`](../lalouise/README.md) precisa estar rodando em `http://localhost:8080`.

## 🔗 Backend Vinculado

Este frontend depende da API do LaLouise:
👉 [`lalouise` — Documentação Técnica do Backend](../lalouise/README.md)

---

<div align="center">
  <sub>LaLouise Frontend · Next.js 16.2.4 · React 19 · Vercel</sub>
</div>