package br.com.matheusfragadev.lalouise.infra.controller.label.utils.dto;

import br.com.matheusfragadev.lalouise.domain.sector.enums.Storage;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import org.jspecify.annotations.NonNull;

import java.util.UUID;


public record ReprintLabelRequest(
        @NotNull Storage storage,
        @Max(value = 99, message = "Maximo de copias é 99")
        Integer copies
) {
}


