package matheusfragadev.br.com.lalouise.printerservice.infra;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import matheusfragadev.br.com.lalouise.printerservice.application.PrintMessageCommand;
import matheusfragadev.br.com.lalouise.printerservice.application.ZebraPrinterService;
import org.springframework.amqp.AmqpRejectAndDontRequeueException;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.stereotype.Component;

import javax.print.PrintException;

@Slf4j
@Component
@RequiredArgsConstructor
public class PrintJobListener {

    private final ZebraPrinterService zebraPrinterService;

    @RabbitListener(queues = "#{@printQueue.name}")
    public void consume(PrintMessageCommand command) {
        try {
            log.info("Job recebido: {}", command.jobId());

            zebraPrinterService.sendToPrinter(command);

        } catch (PrintException e) {
            log.error("Falha física ao imprimir o job {}. A mensagem não será reenfileirada agora.", command.jobId(), e);
            throw new AmqpRejectAndDontRequeueException("Falha física ao imprimir o job " + command.jobId(), e);
        } catch (Exception e) {
            log.error("Erro transitório ao processar o job {}. O RabbitMQ poderá reenfileirar a mensagem.", command.jobId(), e);
            throw new RuntimeException("Erro transitório ao processar o job " + command.jobId(), e);
        }
    }

}
