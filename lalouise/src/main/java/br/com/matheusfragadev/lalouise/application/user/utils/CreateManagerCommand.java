package br.com.matheusfragadev.lalouise.application.user.utils;

import lombok.Builder;

import java.util.UUID;

@Builder
public record CreateManagerCommand(
        String nickname,
        String email,
        String password,
        String confirmPassword
) {
}

