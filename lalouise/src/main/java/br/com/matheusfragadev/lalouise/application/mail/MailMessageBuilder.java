package br.com.matheusfragadev.lalouise.application.mail;

import br.com.matheusfragadev.lalouise.application.mail.utils.MailMessageContentCommand;
import lombok.AccessLevel;
import lombok.NoArgsConstructor;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class MailMessageBuilder {

    public static MailMessageContentCommand buildLoginMail(String to) {
        var subject = "Login detectado em sua conta";

        var text = """
                Olá, seja bem-vindo(a) à La Louise!
                
                Vimos que você fez login em nossa plataforma.
                
                Se foi você, pode ignorar esse email, caso contrário, recomendamos que altere sua senha imediatamente.
                
                Atencionamente,
                Equipe de segurança da La Louise
                """;

        return new MailMessageContentCommand(to, subject, text);
    }

}
