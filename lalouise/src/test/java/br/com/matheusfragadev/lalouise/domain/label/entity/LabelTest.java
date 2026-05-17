package br.com.matheusfragadev.lalouise.domain.label.entity;

import br.com.matheusfragadev.lalouise.domain.label.enums.Status;
import br.com.matheusfragadev.lalouise.domain.label.vo.Lot;
import org.junit.jupiter.api.Test;

import java.time.Instant;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;

class LabelTest {

    private static final String LOT_PATTERN = "^LT[A-Z0-9]{8}$";

    @Test
    void shouldCreateLabelWithActiveStatusByDefault() {
        Label label = new Label(
                UUID.randomUUID(),
                UUID.randomUUID(),
                UUID.randomUUID(),
                UUID.randomUUID(),
                Instant.parse("2026-05-15T10:15:30Z")
        );

        assertAll(
                () -> assertEquals(Status.ACTIVE, label.getStatus()),
                () -> assertNotNull(label.getLot()),
                () -> assertTrue(label.getLot().code().matches(LOT_PATTERN))
        );
    }

    @Test
    void shouldGenerateLotWhenLabelIsCreated() {
        Label label = new Label(
                UUID.randomUUID(),
                UUID.randomUUID(),
                UUID.randomUUID(),
                UUID.randomUUID(),
                Instant.parse("2026-05-15T10:15:30Z")
        );

        assertAll(
                () -> assertNotNull(label.getLot()),
                () -> assertEquals(10, label.getLot().code().length()),
                () -> assertTrue(label.getLot().code().startsWith("LT")),
                () -> assertTrue(label.getLot().code().matches(LOT_PATTERN))
        );
    }

    @Test
    void shouldReprintKeepingLotAndActivatingNewLabel() {
        UUID restaurantId = UUID.randomUUID();
        UUID sectorId = UUID.randomUUID();
        UUID productId = UUID.randomUUID();
        UUID userId = UUID.randomUUID();
        UUID newSectorId = UUID.randomUUID();
        UUID newUserId = UUID.randomUUID();
        Instant validateDate = Instant.parse("2026-05-15T10:15:30Z");
        Instant newValidateDate = Instant.parse("2026-06-01T08:00:00Z");

        Label label = new Label(restaurantId, sectorId, productId, userId, validateDate);
        Lot originalLot = label.getLot();

        Label reprinted = label.reprint(newSectorId, newUserId, newValidateDate);

        assertAll(
                () -> assertEquals(Status.DISCARDED, label.getStatus()),
                () -> assertEquals(restaurantId, reprinted.getRestaurantId()),
                () -> assertEquals(productId, reprinted.getProductId()),
                () -> assertEquals(newSectorId, reprinted.getSectorId()),
                () -> assertEquals(newUserId, reprinted.getUserId()),
                () -> assertEquals(newValidateDate, reprinted.getValidateDate()),
                () -> assertEquals(Status.ACTIVE, reprinted.getStatus()),
                () -> assertSame(originalLot, reprinted.getLot()),
                () -> assertSame(originalLot, label.getLot())
        );
    }
}

