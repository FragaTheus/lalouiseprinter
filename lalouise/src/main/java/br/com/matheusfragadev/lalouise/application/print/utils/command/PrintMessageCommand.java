package br.com.matheusfragadev.lalouise.application.print.utils.command;

import lombok.Builder;

import java.util.UUID;

@Builder
public record PrintMessageCommand(
        UUID jobId,
        String zpl,
        Integer copies
) {
}

