package br.com.matheusfragadev.lalouise.infra.controller.label.utils.dto;

import jakarta.validation.constraints.Max;

public record ReprintSameLabelRequest(
        @Max(value = 99, message = "Maximo de copias é 99")
        Integer copies
) {
}
