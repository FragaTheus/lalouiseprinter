# 🖨️ LaLouise — Print Agent

Microsserviço de impressão assíncrona · Spring Boot 3.5.10 · Java 21 · AMQP / RabbitMQ · ZPL / Zebra

---

## 📚 Sumário Técnico

- [Visão Geral](#visão-geral)
- [Stack Tecnológica](#stack-tecnológica)
- [Arquitetura & Fluxo AMQP](#arquitetura--fluxo-amqp)
- [Configuração](#configuração)
- [Variáveis de Ambiente](#variáveis-de-ambiente)
- [Estrutura de Pastas](#estrutura-de-pastas)
- [Build](#build)
- [Execução](#execução)
- [Windows Service com WinSW](#windows-service-com-winsw)
- [Observações Técnicas](#observações-técnicas)

---

## Visão Geral

O **Print Agent** é um microsserviço que roda **localmente em cada unidade de restaurante** (Windows). Ele consome jobs de impressão publicados pelo backend via **RabbitMQ (AMQP)** e envia comandos **ZPL** diretamente para impressoras **Zebra** locais via `javax.print`.

Cada instância do agente é configurada com o `restaurantId` da unidade correspondente, garantindo que apenas os jobs daquela unidade sejam consumidos — sem conflito entre diferentes restaurantes da rede.

---

## 🛠️ Stack Tecnológica

| Categoria | Tecnologia |
|---|---|
| Runtime | Java 21 |
| Framework | Spring Boot 3.5.10 |
| Mensageria | Spring AMQP (RabbitMQ) |
| Serialização | Jackson Databind |
| Boilerplate | Lombok |
| Build | Gradle 8+ |
| Implantação | WinSW (Windows Service Wrapper) |
| Protocolo de impressão | ZPL (Zebra Programming Language) via `javax.print` |

---

## 📐 Arquitetura & Fluxo AMQP

```
┌──────────────────────────────────────────────────────┐
│                    VPS Cloud                          │
│                                                      │
│   Spring Boot API                                    │
│   POST /api/v1/labels                                │
│         │                                            │
│         ▼                                            │
│   RabbitMQ                                           │
│   Exchange: label.exchange (direct)                  │
│   Routing Key: print.{restaurantId}                  │
└──────────────────────┬───────────────────────────────┘
                       │ AMQPS
         ┌─────────────▼────────────────────┐
         │  Print Agent (Windows local)      │
         │  Queue: label.print.{restaurantId}│
         │         │                         │
         │         ▼                         │
         │  javax.print → ZPL → Zebra 🖨️    │
         └───────────────────────────────────┘
```

**Detalhes do roteamento:**
- Backend publica no exchange `label.exchange` com routing key `print.{restaurantId}`
- Print Agent declara a fila `label.print.{restaurantId}` e a vincula ao exchange
- Cada unidade consome apenas sua própria fila, identificada pelo `restaurantId` configurado

---

## 🔧 Configuração

Configure `src/main/resources/application.properties` com os dados da unidade:

```properties
# Identificador único do restaurante (obtido no painel admin)
restaurant.id=<UUID-DO-RESTAURANTE>

# Nome exato da impressora Zebra como aparece no Windows
# (Painel de Controle > Dispositivos e Impressoras)
printer.name=<NOME-EXATO-DA-IMPRESSORA>

# Conexão com RabbitMQ
spring.rabbitmq.addresses=amqps://user:pass@host:5671/vhost
```

---

| Propriedade | Descrição | Exemplo |
|---|---|---|
| `restaurant.id` | UUID do restaurante (tenant) | `3fa85f64-5717-...` |
| `printer.name` | Nome da impressora no Windows | `ZebraZD421` |
| `spring.rabbitmq.addresses` | URI de conexão AMQP/AMQPS | `amqps://u:p@host/vhost` |

---

## 📁 Estrutura de Pastas

```
printer/
├── src/
│   └── main/
│       ├── java/
│       │   └── (pacotes Spring Boot)
│       │       ├── config/         # Configuração AMQP (queue, exchange, binding)
│       │       ├── consumer/       # Listener RabbitMQ (consome jobs)
│       │       ├── service/        # Lógica de envio ZPL via javax.print
│       │       └── dto/            # DTO do job de impressão
│       └── resources/
│           └── application.properties
├── build.gradle
├── gradlew
└── lalouise-print-agent.xml        # Configuração WinSW (Windows Service)
```

---

## 🚀 Execução

### Execução Direta (dev/teste)

```bash
#Apos configurar application.properties, e ja ter o server RabbitMQ rodando, execute:
./gradlew clean bootrun
```

## 📝 Observações Técnicas

### Cópias ZPL
O envio para a Zebra não faz loop de cópias no código — o próprio ZPL carrega a quantidade via `^PQ`. Não altere essa lógica sem ajustar o payload.

### Erros Transitórios e Físicos da Impressora
Conexão e timeout — a exceção é relançada normalmente, permitindo que o RabbitMQ faça retry automático conforme a política de dead-letter configurada.

---

## 🔗 Componentes Vinculados

- **Backend**: [lalouise/README.md](../lalouise/README.md) — Publica jobs de impressão
- **Frontend**: [ui/README.md](../ui/README.md) — Interface de gerenciamento

---

<div align="center">
  <sub>LaLouise Print Agent · Spring Boot 3.5.10 · Java 21 · RabbitMQ · ZPL</sub>
</div>