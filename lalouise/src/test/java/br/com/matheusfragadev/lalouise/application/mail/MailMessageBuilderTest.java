package br.com.matheusfragadev.lalouise.application.mail;

import br.com.matheusfragadev.lalouise.application.label.utils.RestaurantAlertData;
import br.com.matheusfragadev.lalouise.application.mail.utils.MailMessageContentCommand;
import br.com.matheusfragadev.lalouise.domain.label.entity.Label;
import br.com.matheusfragadev.lalouise.domain.label.vo.Lot;
import br.com.matheusfragadev.lalouise.domain.user.admin.entity.Admin;
import br.com.matheusfragadev.lalouise.domain.user.credentials.vo.Email;
import br.com.matheusfragadev.lalouise.domain.user.credentials.vo.Nickname;
import br.com.matheusfragadev.lalouise.domain.user.credentials.vo.Password;
import org.junit.jupiter.api.Test;

import java.time.Instant;
import java.util.List;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

class MailMessageBuilderTest {

    @Test
    void buildLoginMailShouldSetCorrectToSubjectAndText() {
        String userEmail = "user@lalouise.com";

        MailMessageContentCommand command = MailMessageBuilder.buildLoginMail(userEmail);

        assertEquals(userEmail, command.to());
        assertEquals("Login detectado em sua conta", command.subject());
        assertTrue(command.text().contains("bem-vindo"));
    }

    @Test
    void buildValidateAlertWithEmptyListsShouldContainNoProductMessages() {
        String to = "manager@lalouise.com";

        MailMessageContentCommand command = MailMessageBuilder.buildValidateAlert(to, List.of(), List.of());

        assertEquals(to, command.to());
        assertTrue(command.text().contains("Nenhum produto a vencer"));
        assertTrue(command.text().contains("Nenhum produto vencido"));
    }

    @Test
    void buildValidateAlertWithNonEmptyListsShouldIncludeLotInfo() {
        String to = "manager@lalouise.com";

        Label expiringLabel = mock(Label.class);
        Label expiredLabel = mock(Label.class);
        Lot expiringLot = mock(Lot.class);
        Lot expiredLot = mock(Lot.class);

        when(expiringLabel.getLot()).thenReturn(expiringLot);
        when(expiringLabel.getValidateDate()).thenReturn(Instant.now().plusSeconds(86400));
        when(expiringLot.code()).thenReturn("LTEXP001");

        when(expiredLabel.getLot()).thenReturn(expiredLot);
        when(expiredLabel.getValidateDate()).thenReturn(Instant.now().minusSeconds(86400));
        when(expiredLot.code()).thenReturn("LTEXP002");

        MailMessageContentCommand command = MailMessageBuilder.buildValidateAlert(
                to, List.of(expiringLabel), List.of(expiredLabel)
        );

        assertTrue(command.text().contains("LTEXP001"));
        assertTrue(command.text().contains("LTEXP002"));
    }

    @Test
    void buildAdminAlertShouldIncludeAdminNicknameAndRestaurantInfo() {
        Admin admin = new Admin(
                new Nickname("Admin User"),
                new Email("admin@lalouise.com"),
                Password.of("Strong@123", s -> "hashed")
        );
        UUID restaurantId = UUID.randomUUID();

        RestaurantAlertData alertData = RestaurantAlertData.builder()
                .restaurantId(restaurantId)
                .expiringLabels(List.of())
                .expiredLabels(List.of())
                .build();

        MailMessageContentCommand command = MailMessageBuilder.buildAdminAlert(admin, List.of(alertData));

        assertEquals("admin@lalouise.com", command.to());
        assertTrue(command.text().contains(restaurantId.toString()));
        assertTrue(command.text().contains("Nenhum produto a vencer"));
        assertTrue(command.text().contains("Nenhum produto vencido"));
    }
}


