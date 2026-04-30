package br.com.matheusfragadev.lalouise.domain.admin.entity;

import br.com.matheusfragadev.lalouise.domain.base.credentials.entity.Credentials;
import br.com.matheusfragadev.lalouise.domain.base.credentials.enums.Role;
import br.com.matheusfragadev.lalouise.domain.base.credentials.vo.Email;
import br.com.matheusfragadev.lalouise.domain.base.credentials.vo.Nickname;
import br.com.matheusfragadev.lalouise.domain.base.credentials.vo.Password;
import jakarta.persistence.Entity;
import jakarta.persistence.Table;

@Entity
@Table(name = "admins")
public class Admin extends Credentials {

    private Admin() {
    }

    public Admin(Nickname nickname, Email email, Password password) {
        super(nickname, email, password, Role.ADMIN, true);
    }
}
