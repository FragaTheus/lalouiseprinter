package br.com.matheusfragadev.lalouise.application.print.utils.command;

import br.com.matheusfragadev.lalouise.domain.sector.enums.Storage;
import lombok.Builder;

import java.util.UUID;

/**
 * Comando de impressão de etiqueta.
 * userRole foi removido: com SINGLE_TABLE, getUserName(id) não precisa mais do Role
 * para saber em qual tabela buscar — CredentialsRepository cobre todas as roles.
 */
@Builder
public record PrintLabelCommand(
        UUID productId,
        UUID userId,
        Storage storage,
        Integer copies
) {
}
