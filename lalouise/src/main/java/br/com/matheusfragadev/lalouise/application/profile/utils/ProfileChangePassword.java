package br.com.matheusfragadev.lalouise.application.profile.utils;

import lombok.Builder;

import java.util.UUID;

@Builder
public record ProfileChangePassword(
        UUID userId,
        String currentPassword,
        String newPassword,
        String confirmNewPassword
) {
}
