package br.com.matheusfragadev.lalouise.infra.controller.profile.utils.dto.request;

import jakarta.validation.constraints.NotBlank;

public record ChangeNameRequest(
        @NotBlank(message = "Nome deve ser enviado")
        String newNickname
) {
}
