package br.com.matheusfragadev.lalouise.domain.user.staff.entity;

import br.com.matheusfragadev.lalouise.domain.user.credentials.entity.Credentials;
import br.com.matheusfragadev.lalouise.domain.user.credentials.enums.Role;
import br.com.matheusfragadev.lalouise.domain.user.credentials.vo.Email;
import br.com.matheusfragadev.lalouise.domain.user.credentials.vo.Nickname;
import br.com.matheusfragadev.lalouise.domain.user.credentials.vo.Password;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.util.UUID;

/**
 * Entidade intermediária abstrata da hierarquia SINGLE_TABLE.
 *
 * Remove @MappedSuperclass: não pode ser usado entre dois @Entity na mesma
 * cadeia de herança JPA. Como `Credentials` agora é @Entity, `BaseStaff`
 * também precisa ser @Entity para que o campo `restaurant_id` seja mapeado
 * na tabela `credentials` para Manager e Staff.
 *
 * Sem @DiscriminatorValue pois é abstrata — Hibernate nunca a instancia diretamente.
 * Sem @Table — usa a tabela `credentials` do pai (SINGLE_TABLE).
 */
@Getter
@Entity
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public abstract class BaseStaff extends Credentials {

    // Nullable no schema para permitir ADMIN (que não tem restaurante),
    // mas Manager e Staff sempre recebem este valor no construtor.
    @Column(name = "restaurant_id")
    private UUID restaurantId;

    protected BaseStaff(Nickname nickname, Email email, Password password, Role role, UUID restaurantId) {
        super(nickname, email, password, role, true);
        this.restaurantId = restaurantId;
    }
}
