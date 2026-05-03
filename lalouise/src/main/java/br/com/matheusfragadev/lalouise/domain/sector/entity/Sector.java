package br.com.matheusfragadev.lalouise.domain.sector.entity;

import br.com.matheusfragadev.lalouise.domain.auditory.Auditory;
import br.com.matheusfragadev.lalouise.domain.sector.vo.SectorName;
import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.util.UUID;

@Entity
@Getter
@Table(name = "sectors")
@AllArgsConstructor
@NoArgsConstructor(access = AccessLevel.PRIVATE)
@EqualsAndHashCode(of = "name", callSuper = false)
public class Sector extends Auditory {

    @Embedded
    private SectorName name;

    @Column(name = "restaurant_id", nullable = false)
    private UUID restaurantId;

    @Column(name = "responsible_id")
    private UUID responsibleId;
}
