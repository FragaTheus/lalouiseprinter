package br.com.matheusfragadev.lalouise.domain.user.staff.entity;

import br.com.matheusfragadev.lalouise.domain.user.credentials.enums.Role;
import br.com.matheusfragadev.lalouise.domain.user.credentials.vo.Email;
import br.com.matheusfragadev.lalouise.domain.user.credentials.vo.Nickname;
import br.com.matheusfragadev.lalouise.domain.user.credentials.vo.Password;
import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.util.UUID;

@Getter
@Entity
@Table(name = "staffs")
@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class Staff extends BaseStaff{

    @Column(name = "sector_id")
    private UUID sectorId;

    public Staff(Nickname nickname, Email email, Password password, UUID restaurantId, UUID sectorId) {
        super(nickname, email, password, Role.STAFF, restaurantId);
        this.sectorId = sectorId;
    }

    public void changeSector(UUID newSectorId){
        if (this.sectorId.equals(newSectorId)) return;
        this.sectorId = newSectorId;
    }

    public void removeFromSector(){
        this.sectorId = null;
    }
}
