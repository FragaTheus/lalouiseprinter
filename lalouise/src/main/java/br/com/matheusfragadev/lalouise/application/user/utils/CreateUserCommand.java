package br.com.matheusfragadev.lalouise.application.user.utils;

import lombok.Builder;

@Builder
public record CreateUserCommand(
        String nickname,
        String email,
        String password,
        String confirmPassword
) {
}
