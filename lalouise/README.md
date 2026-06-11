# 🖥️ LaLouise — Backend API

API REST do sistema LaLouise · Spring Boot 4.0.6 · Java 21 · PostgreSQL · Redis · RabbitMQ

---

## 📚 Sumário Técnico

- [Visão Geral](#visão-geral)
- [Stack Tecnológica](#stack-tecnológica)
- [Segurança em Camadas](#segurança-em-camadas)
- [Arquitetura & DDD](#arquitetura--ddd)
- [Domínio](#domínio)
- [API Endpoints](#api-endpoints)
- [Multi-tenancy](#multi-tenancy)
- [Banco de Dados & Migrações](#banco-de-dados--migrações)
- [Mensageria AMQP](#mensageria-amqp)
- [Variáveis de Ambiente](#variáveis-de-ambiente)
- [Estrutura de Pastas](#estrutura-de-pastas)
- [Como Executar](#como-executar)
- [Testes](#testes)

---

## Visão Geral

O backend da LaLouise é uma API REST construída com **Spring Boot 4.0.6** e **Java 21**, responsável por toda a lógica de negócio: autenticação, gerenciamento de usuários, produtos, setores, restaurantes, emissão de etiquetas de validade e publicação de jobs de impressão via RabbitMQ.

---

## 🛠️ Stack Tecnológica

| Camada             | Tecnologia                          |
| ------------------ | ----------------------------------- |
| Runtime            | Java 21                             |
| Framework          | Spring Boot 4.0.6                   |
| Web                | Spring MVC (WebMVC)                 |
| Persistência       | Spring Data JPA + Hibernate         |
| Banco de dados     | PostgreSQL 16                       |
| Migrações          | Flyway                              |
| Segurança          | Spring Security + JWT (jjwt 0.12.6) |
| Cache / Rate Limit | Redis (Jedis)                       |
| Mensageria         | Spring AMQP (RabbitMQ)              |
| E-mail             | Spring Mail (SMTP/STARTTLS)         |
| Validação          | Bean Validation (Jakarta)           |
| Boilerplate        | Lombok                              |
| Build              | Gradle 8+                           |
| Containerização    | Docker + Docker Compose             |

---

## 🔐 Segurança em Camadas

### 🔐 Autenticação JWT

- Tokens assinados com segredo via `jwt.secret`
- Biblioteca `jjwt 0.12.6` para geração e validação
- Tokens stateless — sem sessão no servidor

### 🛡️ Spring Security

- Controle de acesso declarativo via `@PreAuthorize`
- Roles: `ADMIN`, `MANAGER`, `STAFF`
- Cada role tem permissões distintas por endpoint

### ⏱️ Redis Rate Limiting

- Limitação de requisições **por endpoint e por usuário**
- Implementado com Redis (Jedis pool configurado)
- Protege contra abuso de API e DDoS de camada de aplicação

### 🔒 Proteção Contra Força Bruta

- Algoritmo de detecção de tentativas repetidas de login
- Após N tentativas falhas, a conta é bloqueada temporariamente
- Campo `locked_until` na tabela de credenciais (migração V7)

### 🌐 Nginx + SSL/HTTPS

- Terminação TLS no servidor com certificado
- Backend expõe porta 8080 internamente; Nginx faz proxy reverso com HTTPS

### ✅ Validação em Camadas

- **DTOs**: Bean Validation (`@NotBlank`, `@NotNull`, `@Valid`, etc.)
- **Value Objects de Domínio**: `ProductName`, `Lot` — validação encapsulada
- **Camada de Serviço**: regras de negócio adicionais
- **Banco de dados**: constraints `NOT NULL`, `CHECK`, `UNIQUE`

---

## 📐 Arquitetura & DDD

O projeto segue **Domain-Driven Design (DDD)** com separação clara de camadas:

```
br.com.matheusfragadev.lalouise/
├── domain/          # Entidades, Value Objects, Enums, Repositórios (interfaces)
├── application/     # Casos de uso, Serviços de aplicação
└── infra/
    ├── controller/  # Controllers REST
    ├── security/    # Filtros JWT, configuração Spring Security
    ├── config/      # Configurações (AMQP, Redis, etc.)
    ├── context/     # Resolução de contexto multi-tenant
    └── entrypoint/  # Handlers de exceção globais
```

### Value Objects

- **`ProductName`** — Encapsula e valida o nome do produto
- **`Lot`** — Encapsula e valida a identificação de lote

---

## 🗂️ Domínio

### Entidades Principais

| Entidade     | Descrição                                                          |
| ------------ | ------------------------------------------------------------------ |
| `Credential` | Usuário do sistema com role, senha hash e controle de bloqueio     |
| `Restaurant` | Tenant — unidade de negócio (CNPJ, nome)                           |
| `Sector`     | Setor dentro de um restaurante (cozinha, confeitaria, etc.)        |
| `Product`    | Produto cadastrado por restaurante com categoria e validade padrão |
| `Label`      | Etiqueta emitida com lote, datas de fabricação/vencimento e status |

### Ciclo de Vida de uma Etiqueta

```
ACTIVE → EXPIRING → EXPIRED → DISCARDED
           ↑
    (próximo ao vencimento)
```

### Roles de Usuário

| Role      | Capacidades                                            |
| --------- | ------------------------------------------------------ |
| `ADMIN`   | Acesso total — gestão de tenants, usuários, relatórios |
| `MANAGER` | Gestão do restaurante: produtos, setores, usuários     |
| `STAFF`   | Operacional: emissão e consulta de etiquetas no setor  |

---

## 🔌 API Endpoints

### Autenticação

| Método | Endpoint             | Descrição                      | Acesso  |
| ------ | -------------------- | ------------------------------ | ------- |
| `POST` | `/api/v1/auth/login` | Autenticação e obtenção do JWT | Público |

### Etiquetas

| Método | Endpoint                           | Descrição                                | Acesso   |
| ------ | ---------------------------------- | ---------------------------------------- | -------- |
| `POST` | `/api/v1/labels`                   | Emitir nova etiqueta (dispara impressão) | STAFF+   |
| `GET`  | `/api/v1/labels`                   | Listar etiquetas do restaurante          | MANAGER+ |
| `GET`  | `/api/v1/labels/sector/{sectorId}` | Etiquetas por setor                      | STAFF+   |
| `GET`  | `/api/v1/labels/lot/{lot}`         | Busca por lote                           | MANAGER+ |
| `POST` | `/api/v1/labels/{id}/reprint`      | Reimprimir etiqueta                      | STAFF+   |

### Produtos

| Método   | Endpoint                | Descrição                      | Acesso   |
| -------- | ----------------------- | ------------------------------ | -------- |
| `POST`   | `/api/v1/products`      | Criar produto                  | MANAGER+ |
| `GET`    | `/api/v1/products`      | Listar produtos do restaurante | STAFF+   |
| `PUT`    | `/api/v1/products/{id}` | Atualizar produto              | MANAGER+ |
| `DELETE` | `/api/v1/products/{id}` | Remover produto                | MANAGER+ |

### Restaurantes

| Método | Endpoint              | Descrição                  | Acesso |
| ------ | --------------------- | -------------------------- | ------ |
| `POST` | `/api/v1/restaurants` | Criar restaurante (tenant) | ADMIN  |
| `GET`  | `/api/v1/restaurants` | Listar restaurantes        | ADMIN  |

### Setores

| Método | Endpoint          | Descrição                     | Acesso   |
| ------ | ----------------- | ----------------------------- | -------- |
| `POST` | `/api/v1/sectors` | Criar setor                   | MANAGER+ |
| `GET`  | `/api/v1/sectors` | Listar setores do restaurante | STAFF+   |

### Usuários

| Método | Endpoint        | Descrição                      | Acesso   |
| ------ | --------------- | ------------------------------ | -------- |
| `POST` | `/api/v1/users` | Criar usuário                  | MANAGER+ |
| `GET`  | `/api/v1/users` | Listar usuários do restaurante | MANAGER+ |

---

## 👥 Multi-tenancy

O sistema usa **isolamento por `restaurant_id`** em todas as entidades de negócio. O contexto do tenant é resolvido automaticamente:

1. Usuário se autentica via `/api/v1/auth/login`
2. JWT gerado contém o `restaurantId` do usuário
3. Filtro de segurança extrai o contexto e injeta o `RestaurantContext`
4. Todos os queries são automaticamente filtrados pelo `restaurant_id` do tenant

**Não há necessidade de passar `restaurantId` explicitamente** — o contexto é resolvido por quem está autenticado.

---

## 💾 Banco de Dados & Migrações

Migrações gerenciadas com **Flyway** em `src/main/resources/db/migration/`:

| Versão | Arquivo                           | Descrição                                               |
| ------ | --------------------------------- | ------------------------------------------------------- |
| V1     | `CREATE_CREDENTIALS_TABLE`        | Tabela de usuários com role (`ADMIN`/`MANAGER`/`STAFF`) |
| V2     | `INSERT_FIRST_ADMIN`              | Seed do primeiro administrador                          |
| V3     | `CREATE_RESTAURANTS_TABLE`        | Tabela de restaurantes (tenants) com CNPJ               |
| V4     | `CREATE_SECTORS_TABLE`            | Tabela de setores por restaurante                       |
| V5     | `CREATE_PRODUCTS_TABLE`           | Tabela de produtos por restaurante                      |
| V6     | `CREATE_LABELS_TABLE`             | Tabela de etiquetas com status e lote                   |
| V7     | `ADD_LOCKED_UNTIL_TO_CREDENTIALS` | Campo de bloqueio por força bruta                       |
| V8     | `ADD_PRODUCTS_TO_IZANAMI_TENANT`  | Seed de produtos para tenant                            |
| V9     | `ALTER_PRODUCT_NAME_FOR_IZANAMI`  | Ajuste de nomes de produtos                             |

---

## 📨 Mensageria AMQP

O backend publica jobs de impressão no RabbitMQ ao emitir ou reimprimir uma etiqueta:

```
Exchange:     label.exchange  (direct)
Routing Key:  print.{restaurantId}
Queue:        label.print.{restaurantId}  (criada pelo Print Agent)
```

**Fluxo:**

1. `POST /api/v1/labels` — backend persiste a etiqueta
2. Backend publica mensagem JSON no exchange com routing key `print.{restaurantId}`
3. Print Agent da unidade consome a mensagem e envia ZPL para a Zebra

---

## 🔧 Variáveis de Ambiente

Crie um arquivo `.env` na raiz de `lalouise/`:

```dotenv
# Banco de dados
DB_NAME=lalouise
DB_USERNAME=postgres
DB_PASSWORD=sua_senha_segura

# JWT
JWT_SECRET=sua_chave_jwt_minimo_256bits

# RabbitMQ
RABBIT_URI=amqps://user:pass@host:5671/vhost

# Redis
REDIS_HOST=redis_host
REDIS_PORT=6379
REDIS_PASSWORD=sua_senha_redis

# E-mail SMTP
MAIL_HOST=smtp.seuprovedor.com
MAIL_USERNAME=seu_email@dominio.com
MAIL_PASSWORD=sua_senha_email
```

---

## 📁 Estrutura de Pastas

```
lalouise/
├── src/
│   ├── main/
│   │   ├── java/br/com/matheusfragadev/lalouise/
│   │   │   ├── LalouiseApplication.java        # Entry point
│   │   │   ├── domain/
│   │   │   │   ├── label/                      # Entidade Label, Status, VOs
│   │   │   │   ├── product/                    # Entidade Product, Category, VOs
│   │   │   │   ├── restaurant/                 # Entidade Restaurant
│   │   │   │   ├── sector/                     # Entidade Sector
│   │   │   │   ├── user/                       # Entidade Credential/User
│   │   │   │   └── auditory/                   # Auditoria de ações
│   │   │   ├── application/
│   │   │   │   ├── auth/                       # AuthenticationService
│   │   │   │   ├── label/                      # LabelService
│   │   │   │   ├── product/                    # ProductService
│   │   │   │   ├── restaurant/                 # RestaurantService
│   │   │   │   ├── sector/                     # SectorService
│   │   │   │   └── user/                       # UserService
│   │   │   └── infra/
│   │   │       ├── controller/                 # REST Controllers por domínio
│   │   │       ├── security/                   # Filtro JWT, SecurityConfig
│   │   │       ├── config/                     # AMQP, Redis config
│   │   │       ├── context/                    # RestaurantContext, SectorContext
│   │   │       └── entrypoint/                 # Handlers de exceção globais
│   │   └── resources/
│   │       ├── application.properties          # Configurações base
│   │       ├── application-env.properties      # Overrides de ambiente
│   │       └── db/migration/                   # Migrações Flyway (V1..V9)
│   └── test/                                   # Testes unitários e de integração
├── docker-compose.yml                          # PostgreSQL + App
├── build.gradle
└── .env                                        # Variáveis de ambiente (não versionar)
```

---

## 🚀 Como Executar

### Com Docker Compose (recomendado)

```bash
# 1. Configure o ambiente
cp .env.example .env
# Edite .env com suas credenciais

# 2. Build do JAR
./gradlew clean bootJar

# 3. Suba os containers
docker compose up -d

# 4. Acompanhe os logs
docker compose logs -f app
```

A API estará disponível em `http://localhost:8080`.

### Localmente (dev)

```bash
# Garanta que PostgreSQL, Redis e RabbitMQ estejam rodando
./gradlew bootRun
```

---

## 🧪 Testes

```bash
# Rodar todos os testes
./gradlew test

# Relatório em: build/reports/tests/test/index.html
```

Suítes disponíveis:

- `AuthenticationServiceTest` — Autenticação e brute force
- `LabelServiceTest` — Lógica de emissão de etiquetas
- `ValidityCalculatorServiceTest` — Cálculo de validade e status
- `LoginResultTest` — Fluxo de login

---

## 🔗 Componentes Vinculados

- **Frontend**: [ui/README.md](../ui/README.md)
- **Print Agent**: [printer/README.md](../printer/README.md)

---

<div align="center">
  <sub>LaLouise Backend · Spring Boot 4.0.6 · Java 21</sub>
</div>
