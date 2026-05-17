package br.com.matheusfragadev.lalouise.domain.label.entity;

import br.com.matheusfragadev.lalouise.domain.auditory.Auditory;
import br.com.matheusfragadev.lalouise.domain.label.enums.Status;
import br.com.matheusfragadev.lalouise.domain.label.vo.Lot;
import jakarta.persistence.*;
import lombok.*;

import java.time.Instant;
import java.util.UUID;

@Entity
@Getter
@Table(name = "labels")
@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class Label extends Auditory {

    @Column(name = "restaurant_id")
    private UUID restaurantId;
    @Column(name = "sector_id")
    private UUID sectorId;
    @Column(name = "product_id")
    private UUID productId;
    @Column(name = "user_id")
    private UUID userId;
    @Column(name = "validate_date")
    private Instant validateDate;
    @Embedded
    private Lot lot;
    @Enumerated(EnumType.STRING)
    @Column(name = "status")
    private Status status = Status.ACTIVE;

    public Label(UUID restaurantId, UUID sectorId, UUID productId, UUID userId, Instant validateDate) {
        this.restaurantId = restaurantId;
        this.sectorId = sectorId;
        this.productId = productId;
        this.userId = userId;
        this.validateDate = validateDate;
        this.lot = Lot.generate();
    }

    private Label(UUID restaurantId, UUID sectorId, UUID productId, UUID userId, Instant validateDate, Lot lot) {
        this.restaurantId = restaurantId;
        this.sectorId = sectorId;
        this.productId = productId;
        this.userId = userId;
        this.validateDate = validateDate;
        this.lot = lot;
    }

    public Label reprint(
            UUID sectorId,
            UUID userId,
            Instant validateDate
    ) {
        this.status = Status.DISCARDED;
        return new Label(
                this.restaurantId,
                sectorId,
                this.productId,
                userId,
                validateDate,
                this.lot
        );
    }

    public void changeStatus(Status status) {
        if (this.status == status) return;
        this.status = status;
    }

}