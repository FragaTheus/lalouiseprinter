package br.com.matheusfragadev.lalouise.application.label.utils;

import br.com.matheusfragadev.lalouise.domain.label.entity.Label;
import lombok.Builder;

import java.util.List;
import java.util.UUID;

@Builder
public record RestaurantAlertData(
        UUID restaurantId,
        List<Label> expiringLabels,
        List<Label> expiredLabels
) {
}