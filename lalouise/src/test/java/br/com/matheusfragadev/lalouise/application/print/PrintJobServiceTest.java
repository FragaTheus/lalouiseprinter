package br.com.matheusfragadev.lalouise.application.print;

import br.com.matheusfragadev.lalouise.infra.config.RabbitConfig;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.amqp.AmqpException;
import org.springframework.amqp.rabbit.core.RabbitTemplate;

import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class PrintJobServiceTest {

    @Mock
    private RabbitTemplate rabbitTemplate;

    @InjectMocks
    private PrintJobService printJobService;

    @Test
    void queueShouldSendMessageToRabbitMQWithCorrectRoutingKey() {
        UUID restaurantId = UUID.randomUUID();
        String zpl = "^XA^XZ";
        int copies = 2;

        printJobService.queue(zpl, copies, restaurantId);

        String expectedRoutingKey = RabbitConfig.PRINT_ROUTING_KEY_PREFIX + restaurantId;
        verify(rabbitTemplate).convertAndSend(
                eq(RabbitConfig.LABEL_EXCHANGE),
                eq(expectedRoutingKey),
                any(Object.class)
        );
    }

    @Test
    void queueShouldWrapAmqpExceptionInRuntimeException() {
        UUID restaurantId = UUID.randomUUID();
        doThrow(new AmqpException("connection refused"))
                .when(rabbitTemplate)
                .convertAndSend(anyString(), anyString(), any(Object.class));

        RuntimeException ex = assertThrows(RuntimeException.class,
                () -> printJobService.queue("^XA^XZ", 1, restaurantId));

        assertTrue(ex.getMessage().contains("fila de impressão"));
        assertInstanceOf(AmqpException.class, ex.getCause());
    }

    @Test
    void queueShouldRethrowGenericException() {
        UUID restaurantId = UUID.randomUUID();
        RuntimeException cause = new RuntimeException("serialization error");
        doThrow(cause)
                .when(rabbitTemplate)
                .convertAndSend(anyString(), anyString(), any(Object.class));

        RuntimeException ex = assertThrows(RuntimeException.class,
                () -> printJobService.queue("^XA^XZ", 1, restaurantId));

        assertSame(cause, ex);
    }

    @Test
    void queueShouldSendWithCorrectZplAndCopies() {
        UUID restaurantId = UUID.randomUUID();
        String zpl = "^XA^FO50,50^ADN,36,20^FDHello^FS^XZ";
        int copies = 3;

        ArgumentCaptor<Object> captor = ArgumentCaptor.forClass(Object.class);
        printJobService.queue(zpl, copies, restaurantId);

        verify(rabbitTemplate).convertAndSend(
                eq(RabbitConfig.LABEL_EXCHANGE),
                anyString(),
                captor.capture()
        );

        var command = (br.com.matheusfragadev.lalouise.application.print.utils.command.PrintMessageCommand) captor.getValue();
        assertEquals(zpl, command.zpl());
        assertEquals(copies, command.copies());
        assertNotNull(command.jobId());
    }
}


