package br.com.matheusfragadev.lalouise.infra.context.sector;

import java.util.UUID;

public final class SectorContext {

    private static final ThreadLocal<UUID> CONTEXT = new ThreadLocal<>();

    private SectorContext() {}

    public static void set(UUID sectorId) {
        CONTEXT.set(sectorId);
    }

    public static UUID get() {
        return CONTEXT.get();
    }

    public static void clear() {
        CONTEXT.remove();
    }
}

