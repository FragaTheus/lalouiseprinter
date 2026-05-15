package br.com.matheusfragadev.lalouise.infra.controller.user.shared;

import jakarta.validation.constraints.NotBlank;

public record UserChangeNicknameRequest(
        @NotBlank(message = "Novo nome é obrigatório")
        String newNickname
) {
}
