package br.com.matheusfragadev.lalouise.infra.controller.product.utils.dto.response;

import java.util.UUID;

public record ProductLookup(
        UUID lookupId,
        String lookupName
) {
}
