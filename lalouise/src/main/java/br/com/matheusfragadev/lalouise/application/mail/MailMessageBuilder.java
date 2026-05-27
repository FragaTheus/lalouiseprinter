package br.com.matheusfragadev.lalouise.application.mail;

import br.com.matheusfragadev.lalouise.application.label.utils.RestaurantAlertData;
import br.com.matheusfragadev.lalouise.application.mail.utils.MailMessageContentCommand;
import br.com.matheusfragadev.lalouise.domain.label.entity.Label;
import br.com.matheusfragadev.lalouise.domain.user.admin.entity.Admin;
import lombok.AccessLevel;
import lombok.NoArgsConstructor;

import java.util.List;
import java.util.stream.Collectors;

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

    public static MailMessageContentCommand buildValidateAlert(
            String to,
            List<Label> expiringLabels,
            List<Label> expiredLabels) {
        var subject = "Alerta! Produtos a vencer";

        var expiringText = expiringLabels.isEmpty()
                ? "\n⏳ PRODUTOS A VENCER: Nenhum produto a vencer nos próximos 3 dias.\n"
                : buildExpiringLabelsText(expiringLabels);

        var expiredText = expiredLabels.isEmpty()
                ? "\n❌ PRODUTOS VENCIDOS: Nenhum produto vencido.\n"
                : buildExpiredLabelsText(expiredLabels);

        var text = """
                Olá, administrador(a) da La Louise!
                
                Este é um alerta automático sobre o status de suas etiquetas de produtos.
                """ + expiringText + expiredText + """
                
                Acesse sua plataforma para mais detalhes.
                
                Atenciosamente,
                Equipe La Louise
                """;

        return new MailMessageContentCommand(to, subject, text);
    }

    public static MailMessageContentCommand buildAdminAlert(Admin admin, List<RestaurantAlertData> restaurantDataList) {
        var subject = "Relatório de Produtos a Vencer - " + restaurantDataList.size() + " restaurante(s)";

        StringBuilder restaurantDetails = new StringBuilder();
        for (RestaurantAlertData data : restaurantDataList) {
            restaurantDetails.append("\n📍 RESTAURANTE ID: ").append(data.restaurantId()).append("\n");

            if (!data.expiringLabels().isEmpty()) {
                restaurantDetails.append(buildExpiringLabelsText(data.expiringLabels()));
            } else {
                restaurantDetails.append("\n⏳ PRODUTOS A VENCER: Nenhum produto a vencer nos próximos 3 dias.\n");
            }

            if (!data.expiredLabels().isEmpty()) {
                restaurantDetails.append(buildExpiredLabelsText(data.expiredLabels()));
            } else {
                restaurantDetails.append("\n❌ PRODUTOS VENCIDOS: Nenhum produto vencido.\n");
            }

            restaurantDetails.append("\n-------------------------------------------\n");
        }

        var text = """
                Olá, %s da La Louise!
                
                Segue o relatório consolidado de produtos a vencer em seus restaurantes:
                """ + restaurantDetails.toString() + """
                
                Acesse sua plataforma para mais detalhes.
                
                Atenciosamente,
                Equipe La Louise
                """.formatted(admin.getNickname().value());

        return new MailMessageContentCommand(admin.getEmail().value(), subject, text);
    }

    private static String buildExpiringLabelsText(List<Label> labels) {
        var labelsList = labels.stream()
                .map(label -> "  • Lote: " + label.getLot().code() + " - Vence em: " + label.getValidateDate())
                .collect(Collectors.joining("\n"));

        return """
                
                ⏳ PRODUTOS A VENCER (Nos próximos 3 dias):
                """ + labelsList + "\n";
    }

    private static String buildExpiredLabelsText(List<Label> labels) {
        var labelsList = labels.stream()
                .map(label -> "  • Lote: " + label.getLot().code() + " - Venceu em: " + label.getValidateDate())
                .collect(Collectors.joining("\n"));

        return """
                
                ❌ PRODUTOS VENCIDOS:
                """ + labelsList + "\n";
    }

}
