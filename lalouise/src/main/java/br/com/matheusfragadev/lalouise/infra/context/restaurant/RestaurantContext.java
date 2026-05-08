package br.com.matheusfragadev.lalouise.infra.context.restaurant;

import java.util.UUID;

public final class RestaurantContext {

    private static final ThreadLocal<UUID> CONTEXT = new ThreadLocal<>();

    private RestaurantContext() {}

    public static void set(UUID restaurantId) {
        CONTEXT.set(restaurantId);
    }

    public static UUID get() {
        return CONTEXT.get();
    }

    public static void clear() {
        CONTEXT.remove();
    }
}

