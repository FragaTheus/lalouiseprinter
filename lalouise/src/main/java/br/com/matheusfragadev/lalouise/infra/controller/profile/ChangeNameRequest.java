package br.com.matheusfragadev.lalouise.infra.controller.profile;
import jakarta.validation.constraints.NotBlank;
public record ChangeNameRequest(
        @NotBlank String newName
) {
}
