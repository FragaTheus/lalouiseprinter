package br.com.matheusfragadev.lalouise.application.print;

import br.com.matheusfragadev.lalouise.application.print.utils.command.PrintMessageCommand;
import br.com.matheusfragadev.lalouise.infra.config.RabbitConfig;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.AmqpException;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.stereotype.Service;

import java.util.UUID;

@Slf4j
@Service
@RequiredArgsConstructor
public class PrintJobService {

    private final RabbitTemplate rabbitTemplate;

    public void queue(String zpl, Integer copies, UUID restaurantId) {
        UUID jobId = UUID.randomUUID();

        PrintMessageCommand command = PrintMessageCommand.builder()
                .jobId(jobId)
                .zpl(zpl)
                .copies(copies)
                .build();

        String routingKey = RabbitConfig.PRINT_ROUTING_KEY_PREFIX + restaurantId;

        try {
            log.info("Despachando Job de impressão: ID={}, Restaurante={}, Cópias={}",
                    jobId, restaurantId, copies);

            rabbitTemplate.convertAndSend(
                    RabbitConfig.LABEL_EXCHANGE,
                    routingKey,
                    command
            );

            log.info("Job {} enfileirado com sucesso → routing key: {}", jobId, routingKey);

        } catch (AmqpException e) {
            log.error("FALHA DE COMUNICAÇÃO (RabbitMQ): Job {} não enviado para restaurante {}. Erro: {}",
                    jobId, restaurantId, e.getMessage());
            throw new RuntimeException("Serviço de fila de impressão indisponível.", e);

        } catch (Exception e) {
            log.error("ERRO INESPERADO ao processar Job {}: {}", jobId, e.getMessage());
            throw e;
        }
    }

}

