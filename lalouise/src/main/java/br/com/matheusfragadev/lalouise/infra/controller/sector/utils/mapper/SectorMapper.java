package br.com.matheusfragadev.lalouise.infra.controller.sector.utils.mapper;

import br.com.matheusfragadev.lalouise.domain.sector.entity.Sector;
import br.com.matheusfragadev.lalouise.infra.controller.sector.utils.dto.response.SectorInfo;
import br.com.matheusfragadev.lalouise.infra.controller.sector.utils.dto.response.SectorSummary;
import lombok.AccessLevel;
import lombok.NoArgsConstructor;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
public final class SectorMapper {

    public static SectorSummary toSectorSummary(Sector sector) {
        return SectorSummary.builder()
                .id(sector.getId())
                .name(sector.getName().value())
                .active(sector.isActive())
                .build();
    }

    public static SectorInfo toSectorInfo(Sector sector) {
        return SectorInfo.builder()
                .id(sector.getId())
                .name(sector.getName().value())
                .description(sector.getDescription().value())
                .active(sector.isActive())
                .storages(sector.getStorages())
                .restaurantId(sector.getRestaurantId())
                .responsibleId(sector.getResponsibleId())
                .createdAt(sector.getCreatedAt())
                .updatedAt(sector.getUpdatedAt())
                .build();
    }
}

