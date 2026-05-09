package br.com.matheusfragadev.lalouise.domain.sector.enums;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public enum Storage {
    AMBIENT("Ambiente"),
    REFRIGERATED("Refrigerado"),
    FROZEN("Congelado"),
    DEEP_FROZEN("Hipercongelado");

    private final String label;
}
