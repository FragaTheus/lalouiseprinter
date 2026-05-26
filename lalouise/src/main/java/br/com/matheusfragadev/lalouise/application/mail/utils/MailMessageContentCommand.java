package br.com.matheusfragadev.lalouise.application.mail.utils;

public record MailMessageContentCommand(
        String to,
        String subject,
        String text
) {
}
