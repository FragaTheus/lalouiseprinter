package matheusfragadev.br.com.lalouise.printerservice.application;

import lombok.Builder;

import java.util.UUID;

@Builder
public record PrintMessageCommand(
        UUID jobId,
        String zpl,
        Integer copies
) {
}
