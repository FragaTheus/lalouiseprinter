package br.com.matheusfragadev.lalouise.domain.user.staff.entity;

import br.com.matheusfragadev.lalouise.domain.user.credentials.enums.Role;
import br.com.matheusfragadev.lalouise.domain.user.credentials.vo.Email;
import br.com.matheusfragadev.lalouise.domain.user.credentials.vo.Nickname;
import br.com.matheusfragadev.lalouise.domain.user.credentials.vo.Password;
import jakarta.persistence.DiscriminatorValue;
import jakarta.persistence.Entity;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.util.UUID;

/**
 * Manager: gerente vinculado a um restaurante.
 * @DiscriminatorValue("MANAGER") → Hibernate instancia Manager quando role = 'MANAGER'.
 * Sem @Table — herda a tabela `credentials` (SINGLE_TABLE).
 */
@Getter
@Entity
@DiscriminatorValue("MANAGER")
@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class Manager extends BaseStaff {

    public Manager(Nickname nickname, Email email, Password password, UUID restaurantId) {
        super(nickname, email, password, Role.MANAGER, restaurantId);
    }
}
