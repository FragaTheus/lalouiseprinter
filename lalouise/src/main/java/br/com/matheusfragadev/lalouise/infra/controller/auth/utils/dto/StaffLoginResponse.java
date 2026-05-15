package br.com.matheusfragadev.lalouise.infra.controller.auth.utils.dto;

import lombok.Builder;

import java.util.UUID;

@Builder
public record StaffLoginResponse(
        String id,
        String nickname,
        String email,
        String role,
        UUID restaurantId,
        UUID sectorId
) implements LoginResponse{
}
