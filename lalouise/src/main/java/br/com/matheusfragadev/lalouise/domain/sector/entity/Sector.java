package br.com.matheusfragadev.lalouise.domain.sector.entity;

import br.com.matheusfragadev.lalouise.domain.auditory.Auditory;
import br.com.matheusfragadev.lalouise.domain.sector.enums.Storage;
import br.com.matheusfragadev.lalouise.domain.sector.exception.SectorActiveException;
import br.com.matheusfragadev.lalouise.domain.sector.exception.StorageException;
import br.com.matheusfragadev.lalouise.domain.sector.vo.SectorDescription;
import br.com.matheusfragadev.lalouise.domain.sector.vo.SectorName;
import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@Entity
@Getter
@Table(name = "sectors")
@NoArgsConstructor(access = AccessLevel.PRIVATE)
@EqualsAndHashCode(of = {"name", "restaurantId"}, callSuper = false)
public class Sector extends Auditory {

    @Embedded
    private SectorName name;

    @Embedded
    private SectorDescription description;

    @Column(name = "restaurant_id", nullable = false)
    private UUID restaurantId;

    @Column(name = "responsible_id")
    private UUID responsibleId;

    @Column(nullable = false)
    private boolean active;

    @ElementCollection(fetch = FetchType.EAGER)
    @CollectionTable(
            name = "sector_storages",
            joinColumns = @JoinColumn(name = "sector_id")
    )
    @Enumerated(EnumType.STRING)
    @Column(name = "storage", nullable = false)
    private List<Storage> storages = new ArrayList<>();

    public Sector(SectorName name, SectorDescription description, UUID restaurantId, List<Storage> storages) {
        this.name = name;
        this.description = description;
        this.restaurantId = restaurantId;
        verifyStorage(storages);
        this.storages = new ArrayList<>(storages);
        this.active = true;
    }

    public void deactivate() {
        if (!active) {
            throw new SectorActiveException("Setor já está inativo");
        }
        this.active = false;
    }

    public void reactivate() {
        if (active) {
            throw new SectorActiveException("Setor já está ativo");
        }
        this.active = true;
    }

    public void changeName(SectorName name) {
        this.name = name;
    }

    public void changeDescription(SectorDescription description) {
        this.description = description;
    }

    public void updateStorages(List<Storage> storages) {
        verifyStorage(storages);
        this.storages = new ArrayList<>(storages);
    }

    private void verifyStorage(List<Storage> storages){
        if (storages == null || storages.isEmpty()){
            throw new StorageException("Restaurante deve conter ao menos um tipo de armazenamento");
        }
    }
}
