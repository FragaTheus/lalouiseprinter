package br.com.matheusfragadev.lalouise.application.mail;

import br.com.matheusfragadev.lalouise.application.mail.utils.MailMessageContentCommand;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;

@Slf4j
@Service
@RequiredArgsConstructor
public class EmailService {

    private final JavaMailSender javaMailSender;

    @Async("mailExecutor")
    public void sendSimpleEmail(MailMessageContentCommand command){
        try{
            log.info("Sending email to: {}", command.to());
            var message = new SimpleMailMessage();
            message.setTo(command.to());
            message.setSubject(command.subject());
            message.setText(command.text());
            javaMailSender.send(message);
            log.info("Email sent successfully to: {}", command.to());
        }catch (Exception e){
            log.error("Failed to send email to {}: {}", command.to(), e.getMessage());
            throw e;
        }

    }

}
