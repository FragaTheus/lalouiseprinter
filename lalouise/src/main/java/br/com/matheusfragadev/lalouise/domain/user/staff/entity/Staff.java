package br.com.matheusfragadev.lalouise.domain.user.staff.entity;

import br.com.matheusfragadev.lalouise.domain.user.credentials.enums.Role;
import br.com.matheusfragadev.lalouise.domain.user.credentials.vo.Email;
import br.com.matheusfragadev.lalouise.domain.user.credentials.vo.Nickname;
import br.com.matheusfragadev.lalouise.domain.user.credentials.vo.Password;
import jakarta.persistence.Column;
import jakarta.persistence.DiscriminatorValue;
import jakarta.persistence.Entity;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.util.UUID;

/**
 * Staff: colaborador vinculado a um restaurante e opcionalmente a um setor.
 * @DiscriminatorValue("STAFF") → Hibernate instancia Staff quando role = 'STAFF'.
 * Sem @Table — herda a tabela `credentials` (SINGLE_TABLE).
 */
@Getter
@Entity
@DiscriminatorValue("STAFF")
@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class Staff extends BaseStaff {

    // sector_id é nullable: staff pode existir sem setor atribuído ainda
    @Column(name = "sector_id")
    private UUID sectorId;

    public Staff(Nickname nickname, Email email, Password password, UUID restaurantId, UUID sectorId) {
        super(nickname, email, password, Role.STAFF, restaurantId);
        this.sectorId = sectorId;
    }

    public void changeSector(UUID newSectorId) {
        if (this.sectorId != null && this.sectorId.equals(newSectorId)) return;
        this.sectorId = newSectorId;
    }

    public void removeFromSector() {
        this.sectorId = null;
    }
}
