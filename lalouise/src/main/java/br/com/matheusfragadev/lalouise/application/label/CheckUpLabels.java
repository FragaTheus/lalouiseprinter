package br.com.matheusfragadev.lalouise.application.label;

import br.com.matheusfragadev.lalouise.application.label.utils.RestaurantAlertData;
import br.com.matheusfragadev.lalouise.application.mail.EmailService;
import br.com.matheusfragadev.lalouise.application.mail.MailMessageBuilder;
import br.com.matheusfragadev.lalouise.application.user.AdminService;
import br.com.matheusfragadev.lalouise.application.user.ManagerService;
import br.com.matheusfragadev.lalouise.domain.label.entity.Label;
import br.com.matheusfragadev.lalouise.domain.label.enums.Status;
import br.com.matheusfragadev.lalouise.domain.user.admin.entity.Admin;
import br.com.matheusfragadev.lalouise.domain.user.staff.entity.Manager;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@Slf4j
@Service
@RequiredArgsConstructor
public class CheckUpLabels {

    private final LabelService labelService;
    private final ManagerService managerService;
    private final EmailService emailService;
    private final AdminService adminService;

    @Transactional
    public void checkAndUpdateExpiringLabels(UUID restaurantId) {
        log.info("Iniciando verificação de etiquetas vencidas para restaurante: {}", restaurantId);
        try {
            var now = Instant.now();
            var labels = labelService.getActiveLabelsByRestaurant(restaurantId);
            List<Label> expiringLabels = new ArrayList<>();
            List<Label> expiredLabels = new ArrayList<>();

            for (Label label : labels) {
                var daysUntilExpiration = ChronoUnit.DAYS.between(now, label.getValidateDate());

                if (daysUntilExpiration <= 0) {
                    label.changeStatus(Status.EXPIRED);
                    expiredLabels.add(label);
                } else if (daysUntilExpiration <= 3) {
                    label.changeStatus(Status.EXPIRING);
                    expiringLabels.add(label);
                }
            }

            if (!expiredLabels.isEmpty() || !expiringLabels.isEmpty()) {
                sendManagersEmailMessage(restaurantId, expiringLabels, expiredLabels);
                sendAdminEmailMessage(restaurantId, expiringLabels, expiredLabels);
            }

            labelService.saveAll(labels);
            log.info("Etiquetas verificadas para o restaurante de id: {}", restaurantId);
        } catch (Exception e) {
            log.error("Erro ao verificar etiquetas vencidas", e);
            throw e;
        }
    }

    private void sendManagersEmailMessage(UUID restaurantId, List<Label> expiringLabels, List<Label> expiredLabels) {
        try {
            var managers = managerService.getAllActiveManagersByRestaurantId(restaurantId);
            for (Manager manager : managers) {
                var message = MailMessageBuilder.buildValidateAlert(
                        manager.getEmail().value(),
                        expiringLabels,
                        expiredLabels
                );
                emailService.sendSimpleEmail(message);
            }
            log.info("Notificações enviadas para {} managers", managers.size());
        } catch (Exception e) {
            log.error("Erro ao enviar emails para managers do restaurante: {}", restaurantId, e);
        }
    }

    private void sendAdminEmailMessage(UUID restaurantId, List<Label> expiringLabels, List<Label> expiredLabels) {
        try {
            var admins = adminService.getAllAdminsList();
            var restaurantData = new RestaurantAlertData(
                    restaurantId,
                    expiringLabels,
                    expiredLabels
            );

            for (Admin admin : admins) {
                var message = MailMessageBuilder.buildAdminAlert(
                        admin,
                        List.of(restaurantData)
                );
                emailService.sendSimpleEmail(message);
            }

            log.info("Notificação enviada para {} admin(s) - Restaurante: {}", admins.size(), restaurantId);
        } catch (Exception e) {
            log.error("Erro ao enviar email para admin", e);
        }
    }
}
