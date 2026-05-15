package br.com.matheusfragadev.lalouise.infra.controller.auth.utils.dto;

import lombok.Builder;

import java.util.UUID;

@Builder
public record ManagerLoginResponse(
        String id,
        String nickname,
        String email,
        String role,
        UUID restaurantId
) implements LoginResponse {
}

