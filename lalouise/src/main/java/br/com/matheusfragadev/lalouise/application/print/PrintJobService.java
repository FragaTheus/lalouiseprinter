package br.com.matheusfragadev.lalouise.application.print;

import br.com.matheusfragadev.lalouise.application.label.utils.PrintMessageCommand;
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

    /**
     * Enfileira um job de impressão no RabbitMQ direcionado ao restaurante correto.
     *
     * @param zpl          código ZPL gerado pelo {@link ZplService}
     * @param copies       número de cópias (normalizado entre 1 e 99)
     * @param restaurantId UUID do restaurante — usado para montar a routing key dinâmica,
     *                     garantindo que a mensagem chegue APENAS ao agente desse restaurante
     */
    public void queue(String zpl, Integer copies, UUID restaurantId) {
        UUID jobId = UUID.randomUUID();

        // Monta o payload que será serializado como JSON e enviado ao RabbitMQ.
        // O agente Windows deserializa esse mesmo objeto ao receber.
        PrintMessageCommand command = PrintMessageCommand.builder()
                .jobId(jobId)
                .zpl(zpl)
                .copies(normalizeCopies(copies))
                .build();

        // A routing key dinâmica é o mecanismo de roteamento por restaurante.
        // Ex: "print.550e8400-e29b-41d4-a716-446655440000"
        // O exchange Direct do RabbitMQ entrega a mensagem SOMENTE para a fila
        // cujo binding key é idêntica a esta string — ou seja, a fila do agente
        // desse restaurante específico.
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
            // AmqpException cobre falhas de conexão, canal fechado, timeout, etc.
            // Logamos o erro e relançamos para que o LabelService possa tratar
            // e informar ao usuário que a impressão falhou.
            log.error("FALHA DE COMUNICAÇÃO (RabbitMQ): Job {} não enviado para restaurante {}. Erro: {}",
                    jobId, restaurantId, e.getMessage());
            throw new RuntimeException("Serviço de fila de impressão indisponível.", e);

        } catch (Exception e) {
            log.error("ERRO INESPERADO ao processar Job {}: {}", jobId, e.getMessage());
            throw e;
        }
    }

    /**
     * Garante que o número de cópias seja sempre válido.
     * null ou valores negativos viram 1; valores acima de 99 são limitados em 99.
     */
    private int normalizeCopies(Integer copies) {
        if (copies == null || copies < 1) return 1;
        return Math.min(copies, 99);
    }
}

