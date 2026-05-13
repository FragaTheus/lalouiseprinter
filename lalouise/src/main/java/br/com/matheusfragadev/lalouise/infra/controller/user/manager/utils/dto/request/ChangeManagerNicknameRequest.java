package br.com.matheusfragadev.lalouise.infra.controller.user.manager.utils.dto.request;

import jakarta.validation.constraints.NotBlank;

public record ChangeManagerNicknameRequest(
        @NotBlank(message = "Novo nome é obrigatório")
        String newNickname
) {
}

