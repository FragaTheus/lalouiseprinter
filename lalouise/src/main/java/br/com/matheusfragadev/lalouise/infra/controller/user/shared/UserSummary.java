package br.com.matheusfragadev.lalouise.infra.controller.user.shared;

import lombok.Builder;

import java.util.UUID;

@Builder
public record UserSummary(
        UUID id,
        String nickname,
        String email,
        boolean active
) {
}
