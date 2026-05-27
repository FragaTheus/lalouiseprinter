package br.com.matheusfragadev.lalouise.application.mail;

import br.com.matheusfragadev.lalouise.application.mail.utils.MailMessageContentCommand;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class EmailServiceTest {

    @Mock
    private JavaMailSender javaMailSender;

    @InjectMocks
    private EmailService emailService;

    @Test
    void sendSimpleEmailShouldCallJavaMailSender() {
        MailMessageContentCommand command = new MailMessageContentCommand(
                "user@lalouise.com",
                "Test Subject",
                "Test body"
        );

        emailService.sendSimpleEmail(command);

        verify(javaMailSender).send(any(SimpleMailMessage.class));
    }

    @Test
    void sendSimpleEmailShouldRethrowExceptionWhenMailSenderFails() {
        MailMessageContentCommand command = new MailMessageContentCommand(
                "user@lalouise.com",
                "Test Subject",
                "Test body"
        );

        doThrow(new RuntimeException("SMTP connection failed"))
                .when(javaMailSender).send(any(SimpleMailMessage.class));

        RuntimeException ex = assertThrows(RuntimeException.class,
                () -> emailService.sendSimpleEmail(command));

        assertEquals("SMTP connection failed", ex.getMessage());
    }
}

