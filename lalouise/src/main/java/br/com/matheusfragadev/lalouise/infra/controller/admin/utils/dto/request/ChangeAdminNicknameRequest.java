package br.com.matheusfragadev.lalouise.infra.controller.admin.utils.dto.request;

import jakarta.validation.constraints.NotBlank;

public record ChangeAdminNicknameRequest(
        @NotBlank(message = "Novo nome é obrigatório")
        String newNickname
) {
}
