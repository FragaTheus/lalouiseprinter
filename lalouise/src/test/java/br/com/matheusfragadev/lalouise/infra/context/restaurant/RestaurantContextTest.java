package br.com.matheusfragadev.lalouise.infra.context.restaurant;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Test;

import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;

class RestaurantContextTest {

    @AfterEach
    void cleanup() {
        RestaurantContext.clear();
    }

    @Test
    void shouldSetAndGetRestaurantId() {
        var id = UUID.randomUUID();
        RestaurantContext.set(id);
        assertEquals(id, RestaurantContext.get());
    }

    @Test
    void shouldReturnNullAfterClear() {
        var id = UUID.randomUUID();
        RestaurantContext.set(id);
        RestaurantContext.clear();
        assertNull(RestaurantContext.get());
    }

    @Test
    void shouldReturnNullWhenNotSet() {
        assertNull(RestaurantContext.get());
    }

    @Test
    void shouldIsolatePerThread() throws InterruptedException {
        var mainId = UUID.randomUUID();
        var threadId = UUID.randomUUID();
        RestaurantContext.set(mainId);

        UUID[] captured = new UUID[1];
        Thread t = new Thread(() -> {
            RestaurantContext.set(threadId);
            captured[0] = RestaurantContext.get();
            RestaurantContext.clear();
        });
        t.start();
        t.join();

        assertEquals(mainId, RestaurantContext.get());
        assertEquals(threadId, captured[0]);
    }
}

