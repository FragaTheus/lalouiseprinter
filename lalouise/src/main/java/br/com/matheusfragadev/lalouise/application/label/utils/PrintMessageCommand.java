package br.com.matheusfragadev.lalouise.application.label.utils;

import lombok.Builder;

import java.util.UUID;

@Builder
public record PrintMessageCommand(
        UUID jobId,
        String zpl,
        Integer copies
) {
}

