package br.com.matheusfragadev.lalouise.domain.user.admin.entity;

import br.com.matheusfragadev.lalouise.domain.user.credentials.entity.Credentials;
import br.com.matheusfragadev.lalouise.domain.user.credentials.enums.Role;
import br.com.matheusfragadev.lalouise.domain.user.credentials.vo.Email;
import br.com.matheusfragadev.lalouise.domain.user.credentials.vo.Nickname;
import br.com.matheusfragadev.lalouise.domain.user.credentials.vo.Password;
import jakarta.persistence.Entity;
import jakarta.persistence.Table;
import lombok.AccessLevel;
import lombok.NoArgsConstructor;

@Entity
@Table(name = "admins")
@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class Admin extends Credentials {

    public Admin(Nickname nickname, Email email, Password password) {
        super(nickname, email, password, Role.ADMIN, true);
    }

}
