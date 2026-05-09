package br.com.matheusfragadev.lalouise.infra.controller.sector.utils.dto.response;

import br.com.matheusfragadev.lalouise.domain.sector.enums.Storage;
import lombok.Builder;

import java.time.Instant;
import java.util.List;
import java.util.UUID;

@Builder
public record SectorInfo(
        UUID id,
        String name,
        String description,
        boolean active,
        List<Storage> storages,
        UUID restaurantId,
        UUID responsibleId,
        Instant createdAt,
        Instant updatedAt
) {
}

