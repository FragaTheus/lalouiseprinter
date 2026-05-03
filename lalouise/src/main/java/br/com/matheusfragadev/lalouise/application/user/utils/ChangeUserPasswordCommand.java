package br.com.matheusfragadev.lalouise.application.user.utils;

import lombok.Builder;

import java.util.UUID;

@Builder
public record ChangeUserPasswordCommand(
        UUID targetId,
        String newPassword,
        String confirmNewPassword
) {
}
