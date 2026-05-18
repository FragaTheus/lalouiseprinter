# lalouise-print-agent

Agente Windows Spring Boot responsável por consumir jobs de impressão do RabbitMQ e enviar ZPL diretamente para uma impressora Zebra local via `javax.print`.

## Fluxo

- O backend `lalouise` publica mensagens no exchange direto `label.exchange`.
- A routing key segue o padrão `print.{restaurantId}`.
- Este agente sobe com `restaurant.id` configurado localmente.
- Na inicialização, a fila `label.print.{restaurantId}` é declarada e vinculada ao exchange.
- Ao consumir um job, o agente envia **um único** spool ZPL para a impressora configurada em `printer.name`.

## Propriedades esperadas

Configure `src/main/resources/application.properties` ou exporte variáveis de ambiente equivalentes:

```properties
restaurant.id=<UUID-DO-RESTAURANTE>
printer.name=<NOME-EXATO-DA-IMPRESSORA-NO-WINDOWS>
spring.rabbitmq.host=<host>
spring.rabbitmq.port=5672
spring.rabbitmq.username=<user>
spring.rabbitmq.password=<pass>
```

## Build

Gera um JAR executável com nome fixo:

```bash
./gradlew clean bootJar
```

O artefato sai em `build/libs/lalouise-print-agent.jar`.

## Windows Service com WinSW

1. Baixe o `WinSW.exe` e renomeie-o para `lalouise-print-agent.exe`.
2. Coloque o executável do WinSW, o XML desta pasta e o JAR no mesmo diretório.
3. Ajuste o caminho do JAR dentro de `lalouise-print-agent.xml`.
4. Instale o serviço:

```powershell
./lalouise-print-agent.exe install
./lalouise-print-agent.exe start
```

Para remover:

```powershell
./lalouise-print-agent.exe stop
./lalouise-print-agent.exe uninstall
```

## Observações técnicas

- O envio para a Zebra não faz loop de cópias porque o ZPL já carrega o `^PQ`.
- Falhas físicas da impressora geram `AmqpRejectAndDontRequeueException`, evitando requeue infinito.
- Erros transitórios são lançados novamente para permitir retry pelo RabbitMQ.

