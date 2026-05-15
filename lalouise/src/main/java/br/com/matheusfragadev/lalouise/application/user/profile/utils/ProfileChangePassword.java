package br.com.matheusfragadev.lalouise.application.user.profile.utils;

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
