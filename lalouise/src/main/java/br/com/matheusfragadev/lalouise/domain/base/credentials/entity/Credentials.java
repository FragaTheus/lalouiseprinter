package br.com.matheusfragadev.lalouise.domain.base.credentials.entity;

import br.com.matheusfragadev.lalouise.domain.base.credentials.enums.Role;
import br.com.matheusfragadev.lalouise.domain.base.credentials.exception.ActiveException;
import br.com.matheusfragadev.lalouise.domain.base.credentials.vo.Email;
import br.com.matheusfragadev.lalouise.domain.base.credentials.vo.Nickname;
import br.com.matheusfragadev.lalouise.domain.base.credentials.vo.Password;
import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.util.UUID;

@Getter
@MappedSuperclass
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class Credentials{

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;
    @Embedded
    private Nickname nickname;
    @Embedded
    private Email email;
    @Embedded
    private Password password;
    @Enumerated(EnumType.STRING)
    private Role role;

    private boolean active;

    protected Credentials(Nickname nickname, Email email, Password password, Role role, boolean active) {
        this.nickname = nickname;
        this.email = email;
        this.password = password;
        this.role = role;
        this.active = active;
    }

    public void changeNickname(Nickname nickname) {
        this.nickname = nickname;
    }

    public void changePassword(Password password) {
        this.password = password;
    }

    public void deactivate() {
        if (!active) {
            throw new ActiveException("Usuário já está inativo");
        }
        this.active = false;
    }

    public void reactivate(){
        if (active) {
            throw new ActiveException("Usuário já está ativo");
        }
        this.active = true;
    }
}
