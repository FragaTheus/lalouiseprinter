package br.com.matheusfragadev.lalouise.domain.user.admin.entity;

import br.com.matheusfragadev.lalouise.domain.user.credentials.entity.Credentials;
import br.com.matheusfragadev.lalouise.domain.user.credentials.enums.Role;
import br.com.matheusfragadev.lalouise.domain.user.credentials.vo.Email;
import br.com.matheusfragadev.lalouise.domain.user.credentials.vo.Nickname;
import br.com.matheusfragadev.lalouise.domain.user.credentials.vo.Password;
import jakarta.persistence.DiscriminatorValue;
import jakarta.persistence.Entity;
import lombok.AccessLevel;
import lombok.NoArgsConstructor;

/**
 * Subclasse de Credentials com role = ADMIN.
 * Sem @Table — usa a tabela `credentials` da classe pai (SINGLE_TABLE).
 * @DiscriminatorValue("ADMIN") diz ao Hibernate: "ao ler uma linha com role = 'ADMIN', instancie Admin".
 */
@Entity
@DiscriminatorValue("ADMIN")
@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class Admin extends Credentials {

    public Admin(Nickname nickname, Email email, Password password) {
        super(nickname, email, password, Role.ADMIN, true);
    }

}
