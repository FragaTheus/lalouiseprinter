package br.com.matheusfragadev.lalouise.infra.controller.profile;

import java.util.UUID;

public record StaffResponse(
        UUID id,
        String nickname,
        String email,
        String role,
        boolean active
) implements ProfileResponse {
}
