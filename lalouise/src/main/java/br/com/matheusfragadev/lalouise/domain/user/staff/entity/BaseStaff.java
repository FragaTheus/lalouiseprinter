package br.com.matheusfragadev.lalouise.domain.user.staff.entity;

import br.com.matheusfragadev.lalouise.domain.user.credentials.entity.Credentials;
import br.com.matheusfragadev.lalouise.domain.user.credentials.enums.Role;
import br.com.matheusfragadev.lalouise.domain.user.credentials.vo.Email;
import br.com.matheusfragadev.lalouise.domain.user.credentials.vo.Nickname;
import br.com.matheusfragadev.lalouise.domain.user.credentials.vo.Password;
import jakarta.persistence.Column;
import jakarta.persistence.MappedSuperclass;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.util.UUID;

@Getter
@MappedSuperclass
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public abstract class BaseStaff extends Credentials {

    @Column(name = "restaurant_id", nullable = false)
    private UUID restaurantId;

    protected BaseStaff(Nickname nickname, Email email, Password password, Role role, UUID restaurantId) {
        super(nickname, email, password, role, true);
        this.restaurantId = restaurantId;
    }
}

