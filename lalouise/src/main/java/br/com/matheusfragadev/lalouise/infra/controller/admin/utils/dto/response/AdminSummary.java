package br.com.matheusfragadev.lalouise.infra.controller.admin.utils.dto.response;

import lombok.Builder;

import java.util.UUID;

@Builder
public record AdminSummary(
        UUID id,
        String nickname,
        String email,
        boolean active
) {
}
