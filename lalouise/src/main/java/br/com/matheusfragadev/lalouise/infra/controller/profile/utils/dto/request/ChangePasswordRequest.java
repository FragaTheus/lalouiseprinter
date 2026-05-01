package br.com.matheusfragadev.lalouise.infra.controller.profile.utils.dto.request;
import jakarta.validation.constraints.NotBlank;
public record ChangePasswordRequest(
        @NotBlank String currentPassword,
        @NotBlank String newPassword,
        @NotBlank String confirmNewPassword
) {
}
